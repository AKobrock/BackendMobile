package com.usuarios.Demo.controller;

import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.usuarios.Demo.dto.APIResponse;
import com.usuarios.Demo.model.AdminModel;
import com.usuarios.Demo.service.AdminModelService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/v1/admins")
public class AdminModelController {

    private final AdminModelService adminModelService;
    public AdminModelController(AdminModelService adminModelService) {
        this.adminModelService = adminModelService;
    }

/*Buscamos a todos los administradores que hayan */
    @Operation(summary = "Obtiene todos los administradores.", description = "Devuelve todos los administradores si es que existen.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "¡Administradores obtenidos correctamente!"),
        @ApiResponse(responseCode = "404", description = "No hay administradores."),
        @ApiResponse(responseCode = "500", description = "El server se muricio.")
    })

    @GetMapping
    public ResponseEntity<?> getAllAdmins() {
        try {
            List<AdminModel> admins = adminModelService.getAllAdmins();
            return ResponseEntity.ok(
                new APIResponse<>("OK", "Lista de administradores obtenida correctamente", admins)
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new APIResponse<>("ERROR", e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new APIResponse<>("ERROR", "Error inesperado al obtener administradores", null));
        }
    }

/*Buscamos un administrador por su id */
    @Operation(summary = "Obtiene un administrador segun su ID.", description = "Devuelve al administrador si es que existe.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "¡Administrador obtenido correctamente!"),
        @ApiResponse(responseCode = "404", description = "No hay administrador."),
        @ApiResponse(responseCode = "500", description = "El server ta muertecido.")
    })

    @GetMapping("/{id}")
    public ResponseEntity<?> getAdminId(@PathVariable UUID id) {
        try {
            AdminModel admin = adminModelService.getAdminId(id);
            if (admin == null) {
                throw new EntityNotFoundException("Administrador no encontrado con ID " + id);
            }
            return ResponseEntity.ok(
                new APIResponse<>("OK", "Administrador encontrado", admin)
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new APIResponse<>("ERROR", e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new APIResponse<>("ERROR", "Error inesperado al buscar el administrador", null));
        }
    }

/*Creamos un administrador */
    @Operation(summary = "Crea un administrador nuevo.", description = "Devuelve al administrador si es que se creo.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "¡Administrador se ha creado correctamente!"),
        @ApiResponse(responseCode = "404", description = "No se pudo crear al administrador."),
        @ApiResponse(responseCode = "500", description = "El server ta entero muerto.")
    })
    @PostMapping
    public ResponseEntity<?> createAdmin(@RequestBody AdminModel admin) {
        try {
            AdminModel nuevoAdmin = adminModelService.CreateAdmin(admin);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new APIResponse<>("OK", "Administrador creado correctamente", nuevoAdmin));
        } catch (EntityExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new APIResponse<>("ERROR", e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new APIResponse<>("ERROR", "Error inesperado al crear el administrador", null));
        }
    }

/*Actualizamos al administrador */
    @Operation(summary = "Actualiza un administrador.", description = "Devuelve al administrador si es que se actualizó.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "¡Administrador se ha actualizado correctamente!"),
        @ApiResponse(responseCode = "404", description = "No se pudo actualizar al administrador."),
        @ApiResponse(responseCode = "500", description = "El server ta ded.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarAdmin(@PathVariable UUID id, @RequestBody AdminModel admin) {
        try {
            AdminModel actualizado = adminModelService.ActualizarAdmin(id, admin);
            return ResponseEntity.ok(
                new APIResponse<>("OK", "Administrador actualizado correctamente", actualizado)
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new APIResponse<>("ERROR", e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new APIResponse<>("ERROR", "Error inesperado al actualizar el administrador", null));
        }
    }

/*Eliminamos al administrador a travez de su id */
    @Operation(summary = "Se elimina al administrador según su ID.", description = "Nos elimina al administrador.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "¡Administrador eliminado!"),
        @ApiResponse(responseCode = "404", description = "No se pudo eliminar al administrador."),
        @ApiResponse(responseCode = "500", description = "Server no ta.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> matarAdmin(@PathVariable UUID id) {
        try {
            String mensaje = adminModelService.matarAdmin(id);
            return ResponseEntity.ok(
                new APIResponse<>("OK", mensaje, null)
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new APIResponse<>("ERROR", e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new APIResponse<>("ERROR", "Error inesperado al eliminar el administrador", null));
        }
    }
}

