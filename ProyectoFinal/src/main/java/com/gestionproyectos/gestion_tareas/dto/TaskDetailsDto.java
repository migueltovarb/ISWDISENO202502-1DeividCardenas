package com.gestionproyectos.gestion_tareas.dto;

import com.gestionproyectos.gestion_tareas.enums.TaskPriority;
import com.gestionproyectos.gestion_tareas.enums.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * TaskDetailsDto - DTO de respuesta con detalles completos de una tarea
 *
 * PROPÓSITO:
 * Transportar información de tareas desde el servicio a la vista,
 * incluyendo datos calculados y nombres de usuarios en lugar de solo IDs.
 *
 * VENTAJAS sobre retornar la entidad Task:
 * - Incluye nombres legibles (assigneeName) en lugar de solo IDs
 * - Incluye datos calculados (isOverdue)
 * - No expone información sensible
 * - Más fácil de usar en las vistas Thymeleaf
 *
 * USADO EN:
 * - Lista "Mis Tareas" del colaborador
 * - Tablero de tareas del proyecto para el líder
 * - Cualquier vista que muestre información de tareas
 *
 * @author [Tu Nombre]
 * @version 1.0
 * @since 2025-11-03
 */
public record TaskDetailsDto(
        /**
         * ID de la tarea
         * Usado para acciones (actualizar estado, ver detalles)
         */
        String id,

        /**
         * Título de la tarea
         */
        String title,

        /**
         * Descripción detallada
         */
        String description,

        /**
         * Estado actual de la tarea
         * Para mostrar badges con colores
         */
        TaskStatus status,

        /**
         * Prioridad de la tarea
         * Para ordenar y filtrar
         */
        TaskPriority priority,

        /**
         * Fecha de vencimiento
         * Puede ser null si no tiene fecha límite
         */
        LocalDate dueDate,

        /**
         * Nombre del responsable
         * Más amigable que mostrar un ID
         * Ejemplo: "Juan Pérez"
         */
        String assigneeName,

        /**
         * ID del responsable
         * Usado para validaciones (¿es mi tarea?)
         */
        String assigneeId,

        /**
         * Nombre del creador
         * Para mostrar quién creó la tarea
         */
        String creatorName,

        /**
         * Indica si la tarea está vencida
         * Campo calculado para mostrar alertas
         */
        boolean isOverdue,

        /**
         * Fecha de creación
         * Para ordenamiento y tracking
         */
        LocalDateTime createdAt
) {
    /**
     * Obtiene una representación del estado para badges en la UI
     *
     * Retorna las clases CSS de Tailwind para mostrar el badge
     * con el color apropiado según el estado.
     *
     * @return String - Clases CSS para el badge
     */
    public String getStatusBadgeClass() {
        return status != null ? status.getBadgeClass() : "";
    }

    /**
     * Obtiene una representación de la prioridad para badges en la UI
     *
     * @return String - Clases CSS para el badge de prioridad
     */
    public String getPriorityBadgeClass() {
        return priority != null ? priority.getBadgeClass() : "";
    }

    /**
     * Formato amigable de la fecha de vencimiento
     *
     * @return String - Fecha formateada o "Sin fecha límite"
     */
    public String getFormattedDueDate() {
        if (dueDate == null) {
            return "Sin fecha límite";
        }
        return dueDate.toString();
    }

    /**
     * Indica si la tarea está completada
     *
     * @return boolean
     */
    public boolean isCompleted() {
        return status == TaskStatus.COMPLETADA;
    }

    /**
     * Indica si la tarea está pendiente
     *
     * @return boolean
     */
    public boolean isPending() {
        return status == TaskStatus.PENDIENTE;
    }

    /**
     * Indica si la tarea está en progreso
     *
     * @return boolean
     */
    public boolean isInProgress() {
        return status == TaskStatus.EN_PROGRESO;
    }

    /**
     * Método factory para crear TaskDetailsDto desde una entidad Task
     *
     * @param task Entidad Task
     * @param assignedTo Usuario asignado (puede ser null)
     * @param creator Usuario creador (puede ser null)
     * @return TaskDetailsDto con toda la información
     */
    public static TaskDetailsDto fromTask(
            com.gestionproyectos.gestion_tareas.model.Task task,
            com.gestionproyectos.gestion_tareas.model.User assignedTo,
            com.gestionproyectos.gestion_tareas.model.User creator
    ) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }

        // Calcular si está vencida
        boolean isOverdue = task.getDueDate() != null
                && task.getDueDate().isBefore(java.time.LocalDate.now())
                && task.getStatus() != TaskStatus.COMPLETADA;

        return new TaskDetailsDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                assignedTo != null ? assignedTo.getFullName() : "Sin asignar",
                assignedTo != null ? assignedTo.getId() : null,
                creator != null ? creator.getFullName() : "Desconocido",
                isOverdue,
                task.getCreatedAt()
        );
    }
}
