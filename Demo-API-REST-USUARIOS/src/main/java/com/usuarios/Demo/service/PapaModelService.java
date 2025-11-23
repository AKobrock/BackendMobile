package com.usuarios.Demo.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.usuarios.Demo.model.PapaModel;
import com.usuarios.Demo.repository.IPapaModelRepository;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class PapaModelService {

    private final IPapaModelRepository papaModelRepository;

    public PapaModelService(IPapaModelRepository papaModelRepository) {
        this.papaModelRepository = papaModelRepository;
    }

    /* Obtener todos los papás */
    public List<PapaModel> getAllPapas() {
        List<PapaModel> papas = papaModelRepository.findAll();
        if (papas.isEmpty()) {
            throw new EntityNotFoundException("No existen papás registrados.");
        }
        return papas;
    }

    /* Obtener papá por ID */
    public PapaModel getPapaById(UUID id) {
        return papaModelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el papá con ID " + id));
    }

    /* Crear un nuevo papá */
    public PapaModel createPapa(PapaModel papa) {

        // Validar número de hijos
        if (papa.getNumeroHijos() < 1) {
            throw new IllegalArgumentException("El papá debe tener al menos un hijo.");
        }

        // Validar fecha de nacimiento
        if (papa.getFechaNacimiento() == null || papa.getFechaNacimiento().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de nacimiento no es válida.");
        }

        // Calcular edad según fecha de nacimiento
        int edad = calcularEdad(papa.getFechaNacimiento());

        // Validar edad mínima
        if (edad < 30) {
            throw new IllegalArgumentException("El papá debe tener al menos 30 años.");
        }

        // Validar que no exista un papá con el mismo RUT
        boolean rutExiste = papaModelRepository.findAll()
                .stream()
                .anyMatch(p -> p.getRut().equalsIgnoreCase(papa.getRut()));
        if (rutExiste) {
            throw new EntityExistsException("Ya existe un papá con el RUT " + papa.getRut());
        }

        return papaModelRepository.save(papa);
    }

    /* Actualizar papá existente */
    public PapaModel actualizarPapa(UUID id, PapaModel papa) {
        PapaModel papaActual = papaModelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("El papá con ID " + id + " no existe."));

        // No se puede modificar: nombre, apellido, rut, nacionalidad, tipo de papá, ni fecha de nacimiento

        if (papa.getOcupacion() != null && !papa.getOcupacion().isEmpty()) {
            papaActual.setOcupacion(papa.getOcupacion());
        }

        if (papa.getEstadoCivil() != null && !papa.getEstadoCivil().isEmpty()) {
            papaActual.setEstadoCivil(papa.getEstadoCivil());
        }

        if (papa.getNumeroHijos() >= 1) {
            papaActual.setNumeroHijos(papa.getNumeroHijos());
        } else if (papa.getNumeroHijos() == 0) {
            throw new IllegalArgumentException("El papá debe tener al menos un hijo.");
        }

        if (papa.getHobbies() != null && !papa.getHobbies().isEmpty()) {
            papaActual.setHobbies(papa.getHobbies());
        }

        if (papa.getLema() != null && !papa.getLema().isEmpty()) {
            papaActual.setLema(papa.getLema());
        }

        if (papa.getDescripcion() != null && !papa.getDescripcion().isEmpty()) {
            papaActual.setDescripcion(papa.getDescripcion());
        }

        if (papa.getPrecio() > 0) {
            papaActual.setPrecio(papa.getPrecio());
        }

        if (papa.getImagenURL() != null && !papa.getImagenURL().isEmpty()) {
            papaActual.setImagenURL(papa.getImagenURL());
        }

        // Recalcular edad (solo para mantener consistencia, no editable manualmente)
        int edad = calcularEdad(papaActual.getFechaNacimiento());
        if (edad < 30) {
            throw new IllegalArgumentException("El papá debe tener al menos 30 años.");
        }

        return papaModelRepository.save(papaActual);
    }

    /* Eliminar papá */
    public String papaFueAComprarCigarros(UUID id) {
        PapaModel papa = papaModelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se ha encontrado al papá con ID " + id));

        papaModelRepository.deleteById(id);

        return "Papá " + papa.getNombre() + " " + papa.getApellido() + ", de ID: " + papa.getId()
                + ", se ha ido a comprar cigarros y leche.";
    }

    /* Método auxiliar para calcular edad automáticamente */
    private int calcularEdad(LocalDate fechaNacimiento) {
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }
}
