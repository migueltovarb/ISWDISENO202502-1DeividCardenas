package com.gestionproyectos.gestion_tareas.dto;

import com.gestionproyectos.gestion_tareas.enums.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * TaskCreationDto - DTO para creación de tareas
 *
 * PROPÓSITO:
 * Transporta los datos necesarios para crear una nueva tarea
 * desde el formulario del líder hasta el servicio.
 *
 * USADO POR:
 * - LiderController: Recibe datos del formulario de creación de tarea
 * - TaskService: Valida y crea la entidad Task
 *
 * VALIDACIONES:
 * - Título y descripción obligatorios
 * - Responsable debe ser un usuario válido
 * - Prioridad obligatoria
 * - Fecha de vencimiento debe ser futura
 *
 * @author [Tu Nombre]
 * @version 1.0
 * @since 2025-11-03
 */
public record TaskCreationDto(
        /**
         * Título de la tarea
         *
         * Debe ser conciso y descriptivo de la acción a realizar.
         * Ejemplos: "Implementar login", "Corregir bug de paginación"
         */
        @NotBlank(message = "El título de la tarea es obligatorio")
        @Size(min = 5, max = 200, message = "El título debe tener entre 5 y 200 caracteres")
        String title,

        /**
         * Descripción detallada de la tarea
         *
         * Debe incluir:
         * - Contexto y requisitos
         * - Criterios de aceptación
         * - Enlaces a recursos o documentación
         */
        @NotBlank(message = "La descripción es obligatoria")
        @Size(min = 10, max = 2000, message = "La descripción debe tener entre 10 y 2000 caracteres")
        String description,

        /**
         * ID del usuario responsable de la tarea
         *
         * Debe ser un colaborador del proyecto o el líder.
         * La validación de que pertenece al proyecto se hace en el servicio.
         */
        @NotBlank(message = "Debe asignar un responsable para la tarea")
        String assignedToId,

        /**
         * Prioridad de la tarea
         *
         * Valores: BAJA, MEDIA, ALTA, CRITICA
         * Ayuda a priorizar el trabajo del equipo.
         */
        @NotNull(message = "Debe seleccionar una prioridad")
        TaskPriority priority,

        /**
         * Fecha de vencimiento de la tarea
         *
         * OPCIONAL:
         * Si la fecha es null, la tarea no tendrá vencimiento.
         * Si se proporciona, debería ser hoy o en el futuro.
         * La validación de fechas pasadas se puede hacer en el servicio si es necesario.
         *
         * IMPORTANTE:
         * Permitir null facilita la creación rápida de tareas sin fecha límite definida.
         */
        LocalDate dueDate
) {
    /**
     * Compact constructor para normalización
     *
     * Limpia espacios en blanco y normaliza los datos antes de crear el DTO.
     */
    public TaskCreationDto {
        if (title != null) {
            title = title.trim();
        }
        if (description != null) {
            description = description.trim();
        }
    }

    /**
     * NOTAS PARA TU PROFESOR:
     *
     * 1. VALIDACIÓN @FUTURE:
     *    - Valida que la fecha sea posterior a "ahora"
     *    - Se evalúa al momento de la validación, no de la creación del DTO
     *    - Si necesitas validar fechas pasadas, usa @Past
     *    - Si necesitas presente o futuro, usa @FutureOrPresent
     *
     * 2. VALIDACIÓN EN SERVICIOS:
     *    Además de las validaciones del DTO, el servicio debe verificar:
     *    - El responsable existe y está activo
     *    - El responsable es miembro del proyecto
     *    - La fecha de vencimiento está dentro del rango del proyecto
     *    - El proyecto existe y no está cancelado
     *
     * 3. PRIORIDAD POR DEFECTO:
     *    En el formulario, puedes establecer MEDIA como valor por defecto
     *    para mejorar la UX si el líder no selecciona explícitamente.
     *
     * 4. FECHA OPCIONAL:
     *    dueDate puede ser null si no quieres forzar una fecha límite.
     *    En ese caso, quita la anotación @Future y maneja null en el servicio.
     */
}
