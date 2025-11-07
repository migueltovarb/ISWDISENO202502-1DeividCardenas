package com.gestionproyectos.gestion_tareas.dto;

import com.gestionproyectos.gestion_tareas.enums.ProjectStatus;

import java.time.LocalDate;
import java.util.List;

/**
 * ProjectDetailsDto - DTO de respuesta con detalles completos del proyecto
 *
 * PROPÓSITO:
 * Transportar información completa del proyecto incluyendo sus tareas
 * desde el servicio a la vista del líder.
 *
 * INCLUYE:
 * - Información básica del proyecto
 * - Lista completa de tareas del proyecto
 * - Nombre del líder (no solo ID)
 * - Fechas y progreso
 *
 * USADO EN:
 * - Vista "Ver Proyecto" del líder (HU-06)
 * - Dashboard del líder con resumen de proyectos
 * - Reportes de proyecto
 *
 * @author [Tu Nombre]
 * @version 1.0
 * @since 2025-11-03
 */
public record ProjectDetailsDto(
        /**
         * ID del proyecto
         */
        String id,

        /**
         * Nombre del proyecto
         */
        String name,

        /**
         * Descripción del proyecto
         */
        String description,

        /**
         * Estado actual del proyecto
         */
        ProjectStatus status,

        /**
         * Porcentaje de progreso (0-100)
         * Calculado automáticamente según tareas completadas
         */
        int progress,

        /**
         * Nombre del líder del proyecto
         * Más amigable que mostrar solo el ID
         */
        String leaderName,

        /**
         * Lista de tareas del proyecto
         * Incluye todos los detalles necesarios para mostrar
         */
        List<TaskDetailsDto> tasks,

        /**
         * Fecha de inicio del proyecto
         * Puede ser null si no está definida
         */
        LocalDate startDate,

        /**
         * Fecha de fin estimada del proyecto
         * Puede ser null si no está definida
         */
        LocalDate endDate
) {
    /**
     * Obtiene el número total de tareas
     *
     * @return int - Cantidad de tareas
     */
    public int getTotalTasks() {
        return tasks != null ? tasks.size() : 0;
    }

    /**
     * Obtiene el número de tareas completadas
     *
     * @return int - Tareas con estado COMPLETADA
     */
    public int getCompletedTasks() {
        if (tasks == null) return 0;
        return (int) tasks.stream()
                .filter(TaskDetailsDto::isCompleted)
                .count();
    }

    /**
     * Obtiene el número de tareas pendientes
     *
     * @return int - Tareas con estado PENDIENTE
     */
    public int getPendingTasks() {
        if (tasks == null) return 0;
        return (int) tasks.stream()
                .filter(TaskDetailsDto::isPending)
                .count();
    }

    /**
     * Obtiene el número de tareas en progreso
     *
     * @return int - Tareas con estado EN_PROGRESO
     */
    public int getInProgressTasks() {
        if (tasks == null) return 0;
        return (int) tasks.stream()
                .filter(TaskDetailsDto::isInProgress)
                .count();
    }

    /**
     * Obtiene tareas vencidas
     *
     * @return List<TaskDetailsDto> - Tareas con fecha pasada y no completadas
     */
    public List<TaskDetailsDto> getOverdueTasks() {
        if (tasks == null) return List.of();
        return tasks.stream()
                .filter(TaskDetailsDto::isOverdue)
                .toList();
    }

    /**
     * Obtiene tareas por estado específico
     *
     * @param status Estado a filtrar
     * @return List<TaskDetailsDto>
     */
    public List<TaskDetailsDto> getTasksByStatus(com.gestionproyectos.gestion_tareas.enums.TaskStatus status) {
        if (tasks == null) return List.of();
        return tasks.stream()
                .filter(task -> task.status() == status)
                .toList();
    }

    /**
     * Verifica si el proyecto está completado
     *
     * @return boolean
     */
    public boolean isCompleted() {
        return status == ProjectStatus.COMPLETADO;
    }

    /**
     * Verifica si el proyecto está activo
     *
     * @return boolean
     */
    public boolean isActive() {
        return status == ProjectStatus.EN_PROGRESO;
    }

    /**
     * Obtiene la clase CSS para el badge de estado
     *
     * @return String - Clases Tailwind
     */
    public String getStatusBadgeClass() {
        return status != null ? status.getBadgeClass() : "";
    }
}
