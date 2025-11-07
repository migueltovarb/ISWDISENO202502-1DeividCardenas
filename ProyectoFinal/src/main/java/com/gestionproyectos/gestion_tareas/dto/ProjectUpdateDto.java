package com.gestionproyectos.gestion_tareas.dto;

import com.gestionproyectos.gestion_tareas.enums.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * ProjectUpdateDto - DTO para actualización de proyectos
 *
 * PROPÓSITO:
 * - Transferir datos desde el formulario de edición al backend
 * - Validar los datos antes de actualizar el proyecto
 *
 * CAMPOS ACTUALIZABLES:
 * - name: Nombre del proyecto
 * - description: Descripción del proyecto
 * - leaderId: Líder asignado al proyecto
 * - status: Estado del proyecto (ACTIVO, EN_PAUSA, COMPLETADO, CANCELADO)
 *
 * @param id ID del proyecto a actualizar
 * @param name Nuevo nombre del proyecto
 * @param description Nueva descripción del proyecto
 * @param leaderId ID del nuevo líder
 * @param status Nuevo estado del proyecto
 *
 * @author Sistema de Gestión de Tareas
 * @version 1.0
 * @since 2025-11-05
 */
public record ProjectUpdateDto(
    @NotBlank(message = "El ID del proyecto es obligatorio")
    String id,

    @NotBlank(message = "El nombre del proyecto es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    String name,

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 10, max = 1000, message = "La descripción debe tener entre 10 y 1000 caracteres")
    String description,

    @NotBlank(message = "Debe asignar un líder al proyecto")
    String leaderId,

    @NotNull(message = "El estado del proyecto es obligatorio")
    ProjectStatus status
) {
    /**
     * Constructor compacto para validaciones y normalización
     */
    public ProjectUpdateDto {
        if (name != null) {
            name = name.trim();
        }
        if (description != null) {
            description = description.trim();
        }
    }
}
