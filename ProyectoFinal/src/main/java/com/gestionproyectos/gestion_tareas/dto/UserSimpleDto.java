package com.gestionproyectos.gestion_tareas.dto;

/**
 * UserSimpleDto - DTO simplificado de usuario
 *
 * PROPÓSITO:
 * Versión ligera de User para usar en selects y listas donde solo
 * necesitamos mostrar el nombre del usuario.
 *
 * VENTAJAS sobre enviar la entidad completa:
 * - Más eficiente: menos datos en memoria y red
 * - Más seguro: no expone contraseñas ni información sensible
 * - Más claro: solo los datos necesarios para la UI
 *
 * CASOS DE USO:
 * - Select de responsables en formulario de creación de tarea
 * - Lista de miembros de un proyecto
 * - Autocompletado de usuarios
 *
 * @author [Tu Nombre]
 * @version 1.0
 * @since 2025-11-03
 */
public record UserSimpleDto(
        /**
         * ID del usuario
         * Usado como value en el <option> del <select>
         */
        String id,

        /**
         * Nombre completo del usuario
         * Mostrado al usuario en la UI
         */
        String fullName,

        /**
         * Email del usuario
         * Útil para mostrar información adicional
         * Ejemplo: "Juan Pérez (juan@example.com)"
         */
        String email
) {
    /**
     * Constructor de conveniencia desde entidad User
     *
     * Permite crear fácilmente un DTO desde una entidad:
     * User user = ...;
     * UserSimpleDto dto = UserSimpleDto.fromUser(user);
     *
     * @param user Entidad User
     * @return UserSimpleDto con los datos básicos
     */
    public static UserSimpleDto fromUser(com.gestionproyectos.gestion_tareas.model.User user) {
        return new UserSimpleDto(
                user.getId(),
                user.getFullName(),
                user.getEmail()
        );
    }

    /**
     * Formato para mostrar en select
     *
     * Retorna: "Juan Pérez (juan@example.com)"
     * Útil en Thymeleaf: th:text="${user.displayName()}"
     *
     * @return String formateado para mostrar
     */
    public String displayName() {
        return fullName + " (" + email + ")";
    }
}
