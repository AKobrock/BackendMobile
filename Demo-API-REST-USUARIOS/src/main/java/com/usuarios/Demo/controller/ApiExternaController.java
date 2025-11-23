package com.usuarios.Demo.controller;

import com.usuarios.Demo.service.ApiExternaService;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/geocoding")
public class ApiExternaController {

    private final ApiExternaService apiExternaService;

    public ApiExternaController(ApiExternaService apiExternaService) {
        this.apiExternaService = apiExternaService;
    }

    @GetMapping("/reverse")
    public ResponseEntity<String> obtenerDireccion(
            @RequestParam double lat,
            @RequestParam double lon) {

        String respuestaJson = apiExternaService.obtenerDireccionPorCoordenadas(lat, lon);

        try {
            JSONObject json = new JSONObject(respuestaJson);
            String direccion = json.optString("display_name", "Dirección no encontrada");
            return ResponseEntity.ok(direccion);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al procesar la respuesta: " + e.getMessage());
        }
    }
}
