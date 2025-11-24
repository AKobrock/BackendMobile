package com.usuarios.Demo.controller;

import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.usuarios.Demo.model.UserModel;
import com.usuarios.Demo.service.UserModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import com.usuarios.Demo.dto.APIResponse;
import com.usuarios.Demo.dto.LoginRequest;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "*", allowCredentials = "false")
public class UserModelController {

    private final UserModelService userModelService;

    public UserModelController(UserModelService userModelService) {
        this.userModelService = userModelService;
    }

/*Buscamos toods los usuarios */

    @Operation(summary = "Obtiene todos los usuarios.", description = "Devuelve todos los usuarios si que existen.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "¡Usuarios obtenidos correctamente!"),
        @ApiResponse(responseCode = "404", description = "No hay usuarios."),
        @ApiResponse(responseCode = "500", description = "El server se muricio.")
    })

    @GetMapping
    public ResponseEntity<APIResponse<List<UserModel>>> getAllUsers() {
        try {
            List<UserModel> users = userModelService.getAllUsers();
            return ResponseEntity.ok(
                new APIResponse<>("OK", "Usuarios obtenidos correctamente", users)
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new APIResponse<>("ERROR", e.getMessage(), null)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new APIResponse<>("ERROR", "Error inesperado al obtener los usuarios", null)
            );
        }
    }


/*Buscmaos al usuario por id */
    @Operation(summary = "Obtiene un usuario segun su ID.", description = "Devuelve al usuario si es que existe.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "¡Usuario obtenido correctamente!"),
        @ApiResponse(responseCode = "404", description = "No hay usuarios."),
        @ApiResponse(responseCode = "500", description = "El server ta muertecido.")
    })

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<UserModel>> getUserById(@PathVariable UUID id) {
        UserModel user = userModelService.getUserId(id);
        if (user != null) {
            return ResponseEntity.ok(
                new APIResponse<>("OK", "Usuario encontrado", user)
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new APIResponse<>("ERROR", "Usuario con ID " + id + " no encontrado", null)
            );
        }
    }

/*Creamos un usuario */
    @Operation(summary = "Crea un usuario nuevo.", description = "Devuelve al usuario si es que se creo.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "¡Usuario se ha creado correctamente!"),
        @ApiResponse(responseCode = "404", description = "No se pudo crear al usuario."),
        @ApiResponse(responseCode = "500", description = "El server ta entero muerto.")
    })
    @PostMapping
    public ResponseEntity<APIResponse<UserModel>> createUser(@RequestBody UserModel user) {
        try {
            UserModel nuevoUser = userModelService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                new APIResponse<>("OK", "Usuario creado correctamente", nuevoUser)
            );
        } catch (EntityExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new APIResponse<>("ERROR", e.getMessage(), null)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new APIResponse<>("ERROR", "Error inesperado al crear el usuario", null)
            );
        }
    }

/*Los loggeamos en el usuario */
    @PostMapping("/auth/login")
    @Operation(summary = "Login de usuario", description = "Permite al usuario autenticarse con email y password")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login exitoso"),
        @ApiResponse(responseCode = "401", description = "Credenciales incorrectas"),
        @ApiResponse(responseCode = "500", description = "Error del servidor")
    })
    public ResponseEntity<APIResponse<UserModel>> login(@RequestBody LoginRequest loginRequest) {
        try {
            UserModel user = userModelService.login(loginRequest.getEmail(), loginRequest.getPassword());
            if (user != null) {
                return ResponseEntity.ok(
                    new APIResponse<>("OK", "Login exitoso", user)
                );
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new APIResponse<>("ERROR", "Credenciales incorrectas", null)
                );
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new APIResponse<>("ERROR", "Error inesperado al intentar login", null)
            );
        }
    }


/*Actualizamos al user usando su id */
    @Operation(summary = "Actualiza un usuario.", description = "Devuelve al usuario si es que se actualizó.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "¡Usuario se ha actualizado correctamente!"),
        @ApiResponse(responseCode = "404", description = "No se pudo actualizar al usuario."),
        @ApiResponse(responseCode = "500", description = "El server ta ded.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<UserModel>> actualizarUser(@PathVariable UUID id, @RequestBody UserModel user) {
        try {
            UserModel updatedUser = userModelService.actualizarUser(id, user);
            return ResponseEntity.ok(
                new APIResponse<>("OK", "Usuario actualizado correctamente", updatedUser)
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new APIResponse<>("ERROR", e.getMessage(), null)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new APIResponse<>("ERROR", "Error inesperado al actualizar el usuario", null)
            );
        }
    }

    /*Mostramos los usuarios activos */
    @Operation(summary = "Muestra todos los usuarios activos.", description = "Devuelte todos los usuarios activos.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "¡Mira, los usuarios activos están aquí!"),
        @ApiResponse(responseCode = "404", description = "No se pudo crear al usuario."),
        @ApiResponse(responseCode = "500", description = "Lamento informarle, el fallacimiento, muerte y recien sepultado, servidor.")
    })
    @GetMapping("/active")
    public ResponseEntity<APIResponse<List<UserModel>>> mostrarUsuariosActivos() {
        try {
            List<UserModel> users = userModelService.mostrarUsuariosActivos();
            return ResponseEntity.ok(
                new APIResponse<>("OK", "Usuarios activos obtenidos correctamente", users)
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new APIResponse<>("ERROR", e.getMessage(), null)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new APIResponse<>("ERROR", "Error al obtener los usuarios activos", null)
            );
        }
    }

    /*Mostramos los usuarios inactivos */
    @Operation(summary = "Muestra todos los usuarios inactivos.", description = "Devuelte todos los usuarios inactivos.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "¡Estos usuarios son los inactivos, atrápenlos!"),
        @ApiResponse(responseCode = "404", description = "No se pudo crear al usuario"),
        @ApiResponse(responseCode = "500", description = "¡Oopsy daisy! El server se calló :).")
    })
    @GetMapping("/inactive")
    public ResponseEntity<APIResponse<List<UserModel>>> mostrarUsuariosInactivos() {
        try {
            List<UserModel> users = userModelService.mostrarUsuariosInactivos();
            return ResponseEntity.ok(
                new APIResponse<>("OK", "Usuarios inactivos obtenidos correctamente", users)
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new APIResponse<>("ERROR", e.getMessage(), null)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new APIResponse<>("ERROR", "Error al obtener los usuarios inactivos", null)
            );
        }
    }

    /*Reactivamos los usuarios en el controller */
    @Operation(summary = "Reactivamos al usuario según su ID.", description = "Devuelte al usuario reactivado.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "¡Usuario reactivado!"),
        @ApiResponse(responseCode = "404", description = "No se pudo crear al usuario"),
        @ApiResponse(responseCode = "500", description = "[Gasp in spanish] El server no se encuentra disponible.")
    })
    @PutMapping("/{id}/reactivar")
    public ResponseEntity<APIResponse<UserModel>> reactivarUsuario(@PathVariable UUID id) {
        try {
            UserModel userReactivado = userModelService.reactivarUsuario(id);
            return ResponseEntity.ok(
                new APIResponse<>("OK", "Usuario reactivado correctamente", userReactivado)
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new APIResponse<>("ERROR", e.getMessage(), null)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new APIResponse<>("ERROR", e.getMessage(), null)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new APIResponse<>("ERROR", "Error inesperado al reactivar el usuario", null)
            );
        }
    }

/*Borramos usuarios */
    @Operation(summary = "Se elimina al usuario según su ID.", description = "Nos elimina al usuario.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "¡Usuario eliminado!"),
        @ApiResponse(responseCode = "404", description = "No se pudo eliminar al usuario."),
        @ApiResponse(responseCode = "500", description = "Server no ta.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<String>> deleteUser(@PathVariable UUID id) {
        try {
            String mensaje = userModelService.matarUser(id);
            return ResponseEntity.ok(
                new APIResponse<>("OK", mensaje, null)
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new APIResponse<>("ERROR", e.getMessage(), null)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new APIResponse<>("ERROR", "Error inesperado al eliminar el usuario", null)
            );
        }
    }
}

