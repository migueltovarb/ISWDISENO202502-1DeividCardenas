package com.gestionproyectos.gestion_tareas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para creación de proyectos
 * Usado por el administrador para crear nuevos proyectos y asignar un líder
 */
public record ProjectCreationDto(
        @NotBlank(message = "El nombre del proyecto es obligatorio")
        @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
        String name,

        @NotBlank(message = "La descripción es obligatoria")
        @Size(min = 10, max = 1000, message = "La descripción debe tener entre 10 y 1000 caracteres")
        String description,

        @NotBlank(message = "Debe seleccionar un líder para el proyecto")
        String leaderId
) {
    // Compact constructor para normalización
    public ProjectCreationDto {
        if (name != null) name = name.trim();
        if (description != null) description = description.trim();
    }
}
