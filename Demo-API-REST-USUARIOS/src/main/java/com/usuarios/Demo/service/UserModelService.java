package com.usuarios.Demo.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.usuarios.Demo.model.UserModel;
import com.usuarios.Demo.repository.IUserModelRepository;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserModelService {

    private final IUserModelRepository userModelRepository;
    private final EmailService emailService; // ← agregamos el servicio de correos

    // Constructor con ambos servicios inyectados
    public UserModelService(IUserModelRepository userModelRepository, EmailService emailService) {
        this.userModelRepository = userModelRepository;
        this.emailService = emailService;
    }

    public List<UserModel> getAllUsers() {
        List<UserModel> users = userModelRepository.findAll();
        if (users.isEmpty()) {
            throw new EntityNotFoundException("No existen usuarios registrados");
        }
        // Actualizamos el estado (Activo / inactivo) del usuario según su actividad
        users.forEach(this::checkAndUpdateInactivity);
        return users;
    }

    public UserModel getUserId(UUID id) {
        UserModel user = userModelRepository.findById(id).orElse(null);
        if (user != null) checkAndUpdateInactivity(user);
        return user;
    }

    public UserModel createUser(UserModel user) {
        if (user.getId() != null && userModelRepository.findById(user.getId()).isPresent()) {
            throw new EntityExistsException("El usuario con ID " + user.getId() + " ya existe.");
        }

        user.setActive(true);
        user.setLastActivity(LocalDateTime.now()); // Primera actividad
        UserModel nuevo = userModelRepository.save(user);

        // Enviar correo de bienvenida (sin romper si falla)
        try {
            emailService.enviarCorreoBienvenida(nuevo.getEmail(), nuevo.getUsername());
        } catch (Exception e) {
            System.err.println("⚠️ Error al enviar correo: " + e.getMessage());
        }

        return nuevo;
    }

    public UserModel login(String email, String password) {
        UserModel user = userModelRepository.findByEmail(email); // Asegúrate de que este método exista en tu repositorio
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public UserModel actualizarUser(UUID id, UserModel user) {
        Optional<UserModel> existeUser = userModelRepository.findById(id);
        if (existeUser.isEmpty()) {
            throw new EntityNotFoundException("El usuario con ID " + id + " no existe.");
        }

        UserModel userActual = existeUser.get();

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            userActual.setPassword(user.getPassword());
        }
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            userActual.setEmail(user.getEmail());
        }
        if (user.getAddress() != null && !user.getAddress().isEmpty()) {
            userActual.setAddress(user.getAddress());
        }
        if (user.getAvatarURL() != null && !user.getAvatarURL().isEmpty()) {
            userActual.setAvatarURL(user.getAvatarURL());
        }

        // Si actualiza algo, hubo actividad
        userActual.setLastActivity(LocalDateTime.now());
        userActual.setActive(true);

        return userModelRepository.save(userActual);
    }

    // Marca el usuario como inactivo si ha pasado más de 1 año desde su última actividad.
    private void checkAndUpdateInactivity(UserModel user) {
        if (user.getLastActivity() == null) {
            user.setActive(false);
            userModelRepository.save(user);
            emailService.enviarAvisoDesactivacion(user.getEmail(), user.getUsername());
        } else {
            int years = (int) ChronoUnit.YEARS.between(user.getLastActivity(), LocalDateTime.now());
            if (years >= 1 && Boolean.TRUE.equals(user.getActive())) {
                user.setActive(false);
                userModelRepository.save(user);

                // Enviar correo de aviso
                emailService.enviarAvisoDesactivacion(user.getEmail(), user.getUsername());
            }
        }

        // Usamos int en vez de long, dado que nuestra empresa no durará más de mil años :)
    }

    // Buscamos todos los usuarios activos
    public List<UserModel> mostrarUsuariosActivos() {
        List<UserModel> users = userModelRepository.findAll()
            .stream().filter(user -> Boolean.TRUE.equals(user.getActive())).toList();
        if (users.isEmpty()) {
            throw new EntityNotFoundException("No existen usuarios activos actualmente.");
        }
        return users;
    }

    public List<UserModel> mostrarUsuariosInactivos() {
        List<UserModel> users = userModelRepository.findAll()
            .stream()
            .filter(user -> Boolean.FALSE.equals(user.getActive()))
            .toList();

        if (users.isEmpty()) {
            throw new EntityNotFoundException("No existen usuarios inactivos actualmente.");
        }
        return users;
    }

    /* Reactivamos los usuarios */
    public UserModel reactivarUsuario(UUID id) {
        Optional<UserModel> existeUser = userModelRepository.findById(id);
        if (existeUser.isEmpty()) {
            throw new EntityNotFoundException("No se ha encontrado al usuario con ID " + id);
        }

        UserModel user = existeUser.get();

        if (Boolean.TRUE.equals(user.getActive())) {
            throw new IllegalArgumentException("El usuario ya se encuentra activo.");
        }

        user.setActive(true);
        user.setLastActivity(LocalDateTime.now());

        return userModelRepository.save(user);
    }

    public String matarUser(UUID id) {
        Optional<UserModel> existeUser = userModelRepository.findById(id);
        if (existeUser.isEmpty()) {
            throw new EntityNotFoundException("No se ha encontrado al usuario de ID " + id);
        }

        UserModel user = existeUser.get();
        userModelRepository.deleteById(id);

        return "Se ha eliminado al usuario: " + user.getUsername() + " " + user.getLastname() + ", de ID: " + user.getId();
    }
}
