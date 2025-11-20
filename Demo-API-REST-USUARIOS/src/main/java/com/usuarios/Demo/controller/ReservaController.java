package com.usuarios.Demo.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.usuarios.Demo.model.ReservaModel;
import com.usuarios.Demo.service.ReservaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/v1/reservas")
@CrossOrigin(origins = "*")
public class ReservaController {

    private final ReservaService reservaService;
    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    /* Obtener todas las reservas */
    @Operation(summary = "Obtiene todas las reservas.", description = "Devuelve todos los reservas si que existen.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "¡Reservas obtenidos correctamente!"),
        @ApiResponse(responseCode = "404", description = "No hay reservas."),
        @ApiResponse(responseCode = "500", description = "El server se muricio.")
    })
    @GetMapping
    public ResponseEntity<?> obtenerReservas() {
        try {
            List<ReservaModel> reservas = reservaService.getAllReservas();
            return ResponseEntity.ok(reservas);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /* Obtener una reserva por ID */
    @Operation(summary = "Obtiene una reserva segun su ID.", description = "Devuelve la reserva si es que existe.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "¡Reserva obtenida correctamente!"),
        @ApiResponse(responseCode = "404", description = "No hay reservas."),
        @ApiResponse(responseCode = "500", description = "El server ta muertecido.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerReservaPorId(@PathVariable UUID id) {
        try {
            ReservaModel reserva = reservaService.getReservaById(id);
            return ResponseEntity.ok(reserva);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /* Crear nueva reserva */
    @Operation(summary = "Crea un reserva nuevo.", description = "Devuelve al reserva si es que se creo.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "¡Reserva se ha creado correctamente!"),
        @ApiResponse(responseCode = "404", description = "No se pudo crear al reserva."),
        @ApiResponse(responseCode = "500", description = "El server ta entero muerto.")
    })
    @PostMapping
    public ResponseEntity<?> crearReserva(
            @RequestParam UUID userId,
            @RequestParam UUID papaId,
            @RequestParam String fechaVisita,
            @RequestParam String direccion) {

        try {
            LocalDate fecha = LocalDate.parse(fechaVisita);
            ReservaModel nueva = reservaService.crearReserva(userId, papaId, fecha, direccion);
            return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear la reserva: " + e.getMessage());
        }
    }

    /* Cancelar una reserva */
    @Operation(summary = "Cancela una reserva.", description = "Devuelve la cancelación de la reserva si es que se canceló.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "¡Reserva se ha cancelado correctamente!"),
        @ApiResponse(responseCode = "404", description = "No se pudo cancelar la reserva."),
        @ApiResponse(responseCode = "500", description = "El server ta ded.")
    })
    @PutMapping("/cancelar/{id}")
    public ResponseEntity<?> cancelarReserva(@PathVariable UUID id) {
        try {
            ReservaModel cancelada = reservaService.cancelarReserva(id);
            return ResponseEntity.ok(cancelada);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /* Eliminar reserva */
    @Operation(summary = "Se elimina la reserva según su ID.", description = "Nos elimina la reserva.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "¡Reserva eliminado!"),
        @ApiResponse(responseCode = "404", description = "No se pudo eliminar la reserva."),
        @ApiResponse(responseCode = "500", description = "Server no ta.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarReserva(@PathVariable UUID id) {
        try {
            String mensaje = reservaService.eliminarReserva(id);
            return ResponseEntity.ok(mensaje);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
