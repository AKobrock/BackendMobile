package com.usuarios.Demo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.usuarios.Demo.model.PapaModel;
import com.usuarios.Demo.model.ReservaModel;
import com.usuarios.Demo.model.UserModel;
import com.usuarios.Demo.repository.IReservaRepository;
import com.usuarios.Demo.repository.IPapaModelRepository;
import com.usuarios.Demo.repository.IUserModelRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class ReservaService {

    private final IReservaRepository reservaRepository;
    private final IUserModelRepository userRepository;
    private final IPapaModelRepository papaRepository;
    private final EmailService emailService;

    public ReservaService(IReservaRepository reservaRepository, IUserModelRepository userRepository,
                          IPapaModelRepository papaRepository, EmailService emailService) {
        this.reservaRepository = reservaRepository;
        this.userRepository = userRepository;
        this.papaRepository = papaRepository;
        this.emailService = emailService;
    }

    /* Obtener todas las reservas */
    public List<ReservaModel> getAllReservas() {
        List<ReservaModel> reservas = reservaRepository.findAll();
        if (reservas.isEmpty()) {
            throw new EntityNotFoundException("No existen reservas registradas.");
        }
        return reservas;
    }

    /* Obtener reserva por ID */
    public ReservaModel getReservaById(UUID id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró la reserva con ID " + id));
    }

    /* Crear nueva reserva con validación */
    public ReservaModel crearReserva(UUID userId, UUID papaId, LocalDate fechaVisita, String direccion) {

        UserModel usuario = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + userId));

        PapaModel papa = papaRepository.findById(papaId)
                .orElseThrow(() -> new EntityNotFoundException("Papá no encontrado con ID: " + papaId));

        if (fechaVisita.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Nuestros papás no viajan en el tiempo, por lo que la fecha de visita debe ser posterior al día actual.");
        }

        // Validar si el papá ya está reservado para esa fecha
        boolean papaOcupado = !reservaRepository.findByPapaAndFechaVisita(papa, fechaVisita).isEmpty();
        if (papaOcupado) {
            throw new IllegalStateException("El papá ya está reservado para esa fecha. Por favor, elija otra.");
        }

        ReservaModel reserva = new ReservaModel();
        reserva.setUsuario(usuario);
        reserva.setPapa(papa);
        reserva.setFechaReserva(LocalDate.now());
        reserva.setFechaVisita(fechaVisita);
        reserva.setDireccionVisita(direccion);
        reserva.setEstado("CONFIRMADA");

        ReservaModel nuevaReserva = reservaRepository.save(reserva);

        // Enviar correo (opcional)
        try {
            emailService.enviarConfirmacionReserva(
                usuario.getEmail(),
                usuario.getUsername(),
                papa.getNombre(),
                fechaVisita.toString()
            );
        } catch (Exception e) {
            System.out.println("⚠️ No se pudo enviar el correo: " + e.getMessage());
        }

        return nuevaReserva;
    }

    /* Cancelar reserva */
    public ReservaModel cancelarReserva(UUID id) {
        ReservaModel reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró la reserva con ID " + id));

        reserva.setEstado("CANCELADA");
        return reservaRepository.save(reserva);
    }

    /* Reservas del día siguiente (para recordatorios automáticos) */
    public List<ReservaModel> obtenerReservasDeManana() {
        return reservaRepository.findByFechaVisita(LocalDate.now().plusDays(1));
    }

    /* Eliminar reserva */
    public String eliminarReserva(UUID id) {
        ReservaModel reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró la reserva con ID " + id));

        reservaRepository.deleteById(id);
        return "Reserva eliminada correctamente (ID: " + id + ")";
    }
}
