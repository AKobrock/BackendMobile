package com.usuarios.Demo.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.usuarios.Demo.model.AdminModel;
import com.usuarios.Demo.repository.IAdminModelRepository;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class AdminModelService {

    private final IAdminModelRepository adminModelRepository;

    public AdminModelService(IAdminModelRepository adminModelRepository) {
        this.adminModelRepository = adminModelRepository;
    }

    /* Obtener todos los administradores */
    public List<AdminModel> getAllAdmins() {
        List<AdminModel> admins = adminModelRepository.findAll();
        if (admins.isEmpty()) {
            throw new EntityNotFoundException("No existen administradores registrados en el sistema.");
        }
        return admins;
    }

    /* Buscar administrador por ID */
    public AdminModel getAdminId(UUID id) {
        return adminModelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Administrador no encontrado con ID " + id));
    }

    /* Crear un nuevo administrador */
    public AdminModel createAdmin(AdminModel admin) {
        if (admin.getId() != null && adminModelRepository.findById(admin.getId()).isPresent()) {
            throw new EntityExistsException("El administrador con ID " + admin.getId() + " ya existe.");
        }

        // Asignar rol automáticamente
        admin.setRol("ADMIN");

        return adminModelRepository.save(admin);
    }

    /* Actualizar datos de un administrador existente */
    public AdminModel actualizarAdmin(UUID id, AdminModel admin) {
        AdminModel adminActual = adminModelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Administrador con ID " + id + " no encontrado."));

        if (admin.getPassword() != null && !admin.getPassword().isEmpty()) {
            adminActual.setPassword(admin.getPassword());
        }

        if (admin.getEmail() != null && !admin.getEmail().isEmpty()) {
            adminActual.setEmail(admin.getEmail());
        }

        if (admin.getAddress() != null && !admin.getAddress().isEmpty()) {
            adminActual.setAddress(admin.getAddress());
        }

        if (admin.getAvatarURL() != null && !admin.getAvatarURL().isEmpty()) {
            adminActual.setAvatarURL(admin.getAvatarURL());
        }

        return adminModelRepository.save(adminActual);
    }

    /* Eliminar un administrador */
    public String matarAdmin(UUID id) {
        AdminModel admin = adminModelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró al administrador con ID " + id));

        adminModelRepository.deleteById(id);
        return "Administrador eliminado: " + admin.getUsername() + " " + admin.getLastname();
    }
}
