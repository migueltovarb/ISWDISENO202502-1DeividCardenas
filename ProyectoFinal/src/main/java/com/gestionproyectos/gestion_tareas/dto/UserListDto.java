package com.gestionproyectos.gestion_tareas.dto;

import com.gestionproyectos.gestion_tareas.enums.Role;
import com.gestionproyectos.gestion_tareas.model.User;

import java.time.LocalDateTime;

/**
 * UserListDto - DTO para listar usuarios en tablas
 *
 * PROPÓSITO:
 * - Mostrar información relevante de usuarios en listas
 * - No expone información sensible (password)
 * - Incluye datos para administración (estado, rol, fechas)
 *
 * USO:
 * - Tabla de usuarios en panel de admin
 * - Listados y búsquedas
 * - Exportación de reportes
 *
 * @author Sistema de Gestión de Tareas
 * @version 1.0
 * @since 2025-11-05
 */
public record UserListDto(
    String id,
    String fullName,
    String email,
    Role role,
    boolean active,
    LocalDateTime createdAt,
    LocalDateTime lastModifiedAt,
    int leadProjectCount,
    int collaboratorProjectCount
) {
    /**
     * Convierte una entidad User a UserListDto
     *
     * @param user Entidad User a convertir
     * @return UserListDto con los datos del usuario
     */
    public static UserListDto fromUser(User user) {
        return new UserListDto(
            user.getId(),
            user.getFullName(),
            user.getEmail(),
            user.getRole(),
            user.isActive(),
            user.getCreatedAt(),
            user.getUpdatedAt(),
            user.getLeadProjectIds() != null ? user.getLeadProjectIds().size() : 0,
            user.getCollaboratorProjectIds() != null ? user.getCollaboratorProjectIds().size() : 0
        );
    }

    /**
     * Obtiene el nombre del rol para mostrar
     *
     * @return String con el nombre del rol
     */
    public String getRoleDisplayName() {
        return role != null ? role.getDisplayName() : "Sin rol";
    }

    /**
     * Obtiene el badge de estado
     *
     * @return String con la clase CSS para el badge
     */
    public String getStatusBadgeClass() {
        return active ? "bg-green-100 text-green-800" : "bg-red-100 text-red-800";
    }

    /**
     * Obtiene el texto de estado
     *
     * @return String con el estado
     */
    public String getStatusText() {
        return active ? "Activo" : "Inactivo";
    }

    /**
     * Calcula el total de proyectos
     *
     * @return Total de proyectos (como líder + como colaborador)
     */
    public int getTotalProjects() {
        return leadProjectCount + collaboratorProjectCount;
    }
}
