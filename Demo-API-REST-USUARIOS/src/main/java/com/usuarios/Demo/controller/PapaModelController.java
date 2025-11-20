package com.usuarios.Demo.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.usuarios.Demo.model.PapaModel;
import com.usuarios.Demo.service.PapaModelService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/v1/papas")
@CrossOrigin(origins = "*", allowCredentials = "false") // Permite peticiones desde cualquier frontend
public class PapaModelController {

    private final PapaModelService papaModelService;

    public PapaModelController(PapaModelService papaModelService) {
        this.papaModelService = papaModelService;
    }

    /* Obtener todos los papás */
    @Operation(summary = "Obtiene todos los papás.", description = "Devuelve todos los papás si que existen.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "¡Papás obtenidos correctamente!"),
        @ApiResponse(responseCode = "404", description = "No hay papás."),
        @ApiResponse(responseCode = "500", description = "El server se muricio.")
    })
    @GetMapping
    public ResponseEntity<?> obtenerTodosLosPapas() {
        try {
            List<PapaModel> papas = papaModelService.getAllPapas();
            return ResponseEntity.ok(papas);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /* Obtener papá por ID */
    @Operation(summary = "Obtiene un papá segun su ID.", description = "Devuelve al papá si es que existe.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "¡Papá obtenido correctamente!"),
        @ApiResponse(responseCode = "404", description = "No hay papá."),
        @ApiResponse(responseCode = "500", description = "El server ta muertecido.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPapaPorId(@PathVariable UUID id) {
        try {
            PapaModel papa = papaModelService.getPapaById(id);
            return ResponseEntity.ok(papa);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /* Crear un nuevo papá */
    @Operation(summary = "Crea un papá nuevo.", description = "Devuelve al papá si es que se creo.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "¡Papá se ha creado correctamente!"),
        @ApiResponse(responseCode = "404", description = "No se pudo crear al papá."),
        @ApiResponse(responseCode = "500", description = "El server ta entero muerto.")
    })
    @PostMapping
    public ResponseEntity<?> crearPapa(@RequestBody PapaModel papa) {
        try {
            PapaModel nuevoPapa = papaModelService.createPapa(papa);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPapa);
        } catch (IllegalArgumentException | EntityExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear el papá: " + e.getMessage());
        }
    }

    /* Actualizar papá existente */
    @Operation(summary = "Actualiza un papá.", description = "Devuelve al papá si es que se actualizó.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "¡Papá se ha actualizado correctamente!"),
        @ApiResponse(responseCode = "404", description = "No se pudo actualizar al papá."),
        @ApiResponse(responseCode = "500", description = "El server ta ded.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarPapa(@PathVariable UUID id, @RequestBody PapaModel papa) {
        try {
            PapaModel papaActualizado = papaModelService.actualizarPapa(id, papa);
            return ResponseEntity.ok(papaActualizado);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el papá: " + e.getMessage());
        }
    }

    /* Eliminar papá (se fue a comprar cigarros) */
    @Operation(summary = "Eliminamos un papá segun su ID.", description = "Nos devuelve el mensaje de eliminación de papá")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "¡Papá se fue a comprar cigarros!"),
        @ApiResponse(responseCode = "404", description = "Papa no tenía dinero para cigarros, no pudo salir."),
        @ApiResponse(responseCode = "500", description = "El server se fue a comprar cigarrillos y leche, junto al papá.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarPapa(@PathVariable UUID id) {
        try {
            String mensaje = papaModelService.papaFueAComprarCigarros(id);
            return ResponseEntity.ok(mensaje);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar el papá: " + e.getMessage());
        }
    }
}

