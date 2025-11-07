package com.gestionproyectos.gestion_tareas.dto;

import com.gestionproyectos.gestion_tareas.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * UserUpdateDto - DTO para actualización de usuarios
 *
 * PROPÓSITO:
 * - Transferir datos desde el formulario de edición al backend
 * - Validar los datos antes de actualizar el usuario
 *
 * DIFERENCIA CON UserRegistrationDto:
 * - NO incluye password (se cambia por separado por seguridad)
 * - NO incluye email (no permitimos cambiar el username)
 * - Incluye ID para identificar qué usuario actualizar
 *
 * VALIDACIONES:
 * - fullName: Mínimo 3, máximo 100 caracteres
 * - role: Debe ser un rol válido (no puede ser null)
 *
 * @param id ID del usuario a actualizar
 * @param fullName Nombre completo del usuario
 * @param role Nuevo rol del usuario
 *
 * @author Sistema de Gestión de Tareas
 * @version 1.0
 * @since 2025-11-05
 */
public record UserUpdateDto(
    @NotBlank(message = "El ID del usuario es obligatorio")
    String id,

    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    String fullName,

    @NotNull(message = "El rol es obligatorio")
    Role role
) {
    /**
     * Constructor compacto para validaciones adicionales
     */
    public UserUpdateDto {
        // Validaciones adicionales si son necesarias
        if (fullName != null) {
            fullName = fullName.trim();
        }
    }
}
