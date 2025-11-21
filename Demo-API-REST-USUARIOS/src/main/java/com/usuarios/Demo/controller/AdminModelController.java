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
@CrossOrigin(origins = "*", allowCredentials = "false")
public class AdminModelController {

    private final AdminModelService adminModelService;

    public AdminModelController(AdminModelService adminModelService) {
        this.adminModelService = adminModelService;
    }

    /* Obtener todos los administradores */
    @Operation(summary = "Obtiene todos los administradores", description = "Devuelve la lista completa de administradores si existen")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Administradores obtenidos correctamente"),
        @ApiResponse(responseCode = "404", description = "No hay administradores registrados")
    })
    @GetMapping
    public ResponseEntity<APIResponse<List<AdminModel>>> getAllAdmins() {
        try {
            List<AdminModel> admins = adminModelService.getAllAdmins();
            return ResponseEntity.ok(new APIResponse<>("OK", "Lista de administradores obtenida correctamente", admins));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new APIResponse<>("ERROR", e.getMessage(), null));
        }
    }

    /* Buscar administrador por ID */
    @Operation(summary = "Obtiene un administrador por su ID", description = "Devuelve el administrador correspondiente al ID entregado")
    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<AdminModel>> getAdminId(@PathVariable UUID id) {
        try {
            AdminModel admin = adminModelService.getAdminId(id);
            return ResponseEntity.ok(new APIResponse<>("OK", "Administrador encontrado", admin));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new APIResponse<>("ERROR", e.getMessage(), null));
        }
    }

    /* Crear nuevo administrador */
    @Operation(summary = "Crea un nuevo administrador", description = "Crea un administrador con rol 'ADMIN'")
    @PostMapping
    public ResponseEntity<APIResponse<AdminModel>> createAdmin(@RequestBody AdminModel admin) {
        try {
            AdminModel nuevoAdmin = adminModelService.createAdmin(admin);
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

    /* Actualizar administrador */
    @Operation(summary = "Actualiza un administrador existente", description = "Permite actualizar datos básicos del administrador")
    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<AdminModel>> actualizarAdmin(@PathVariable UUID id, @RequestBody AdminModel admin) {
        try {
            AdminModel actualizado = adminModelService.actualizarAdmin(id, admin);
            return ResponseEntity.ok(new APIResponse<>("OK", "Administrador actualizado correctamente", actualizado));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new APIResponse<>("ERROR", e.getMessage(), null));
        }
    }

    /* Eliminar administrador */
    @Operation(summary = "Elimina un administrador", description = "Elimina un administrador según su ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<String>> matarAdmin(@PathVariable UUID id) {
        try {
            String mensaje = adminModelService.matarAdmin(id);
            return ResponseEntity.ok(new APIResponse<>("OK", mensaje, null));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new APIResponse<>("ERROR", e.getMessage(), null));
        }
    }
}
