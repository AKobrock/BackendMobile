package com.usuarios.Demo.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Locale;

@Service
public class ApiExternaService {

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Consulta la API pública de Nominatim (OpenStreetMap)
     * y devuelve la dirección según coordenadas (en formato JSON).
     */
    public String obtenerDireccionPorCoordenadas(double latitud, double longitud) {
        // Asegura formato con punto decimal (no coma)
        String latStr = String.format(Locale.US, "%.6f", latitud);
        String lonStr = String.format(Locale.US, "%.6f", longitud);

        // URL de Nominatim correctamente formateada
        String url = String.format(
            "https://nominatim.openstreetmap.org/reverse?lat=%s&lon=%s&format=json&accept-language=es&zoom=18&addressdetails=1",
            latStr,
            lonStr
        );

        System.out.println("🌍 URL generada para Nominatim: " + url);

        // Nominatim exige un User-Agent o email identificador
        HttpHeaders headers = new HttpHeaders();
        headers.add("User-Agent", "AquiPapa/1.0 (tucorreo@ejemplo.com)");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return response.getBody();
        } catch (Exception e) {
            return "Error al consultar la API externa: " + e.getMessage();
        }
    }
}
