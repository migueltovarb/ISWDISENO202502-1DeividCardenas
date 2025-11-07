package com.gestionproyectos.gestion_tareas.dto;

import com.gestionproyectos.gestion_tareas.validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;

/**
 * AdminPasswordChangeDto - DTO para cambio de contraseña por admin
 *
 * PROPÓSITO:
 * - Permite al administrador cambiar la contraseña de cualquier usuario
 * - NO requiere la contraseña actual (es un reset administrativo)
 *
 * DIFERENCIA CON PasswordChangeDto (para usuarios):
 * - Usuario normal: Requiere contraseña actual para cambiarla
 * - Admin: Puede cambiar sin conocer la contraseña actual
 *
 * USO:
 * - Cuando un usuario olvida su contraseña
 * - Cuando se requiere reset por seguridad
 * - Primera asignación de contraseña temporal
 *
 * @param userId ID del usuario al que cambiar la contraseña
 * @param newPassword Nueva contraseña a establecer
 *
 * @author Sistema de Gestión de Tareas
 * @version 1.0
 * @since 2025-11-05
 */
public record AdminPasswordChangeDto(
    @NotBlank(message = "El ID del usuario es obligatorio")
    String userId,

    @NotBlank(message = "La nueva contraseña es obligatoria")
    @ValidPassword
    String newPassword
) {
    /**
     * Constructor compacto para validaciones adicionales
     */
    public AdminPasswordChangeDto {
        // La validación de complejidad se maneja con @ValidPassword
    }
}
