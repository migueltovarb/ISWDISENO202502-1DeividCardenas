package com.gestionproyectos.gestion_tareas.dto;

import com.gestionproyectos.gestion_tareas.enums.Role;
import com.gestionproyectos.gestion_tareas.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * UserRegistrationDto - DTO para registro de nuevos usuarios
 *
 * PROPÓSITO:
 * Este record transporta los datos necesarios para crear un nuevo usuario
 * desde el formulario de registro hasta el servicio.
 *
 * RECORDS (Java 16+):
 * Un record es una clase inmutable con:
 * - Constructor automático con todos los campos
 * - Getters automáticos (sin prefijo "get")
 * - equals(), hashCode() y toString() automáticos
 * - Campos finales (no se pueden modificar después de crear el objeto)
 *
 * VENTAJAS sobre clases tradicionales:
 * - Menos código boilerplate
 * - Inmutabilidad por defecto (más seguro)
 * - Intención clara: transportar datos, no lógica de negocio
 * - Perfecto para DTOs
 *
 * PATRÓN DTO (Data Transfer Object):
 * - Separa la capa de presentación de la capa de dominio
 * - Evita exponer entidades de dominio directamente
 * - Permite validaciones específicas para cada caso de uso
 * - Facilita cambios en la API sin afectar el dominio
 *
 * VALIDACIONES JAKARTA:
 * Las anotaciones de validación se ejecutan automáticamente cuando usamos
 * @Valid en el controlador. Si alguna validación falla:
 * - No se ejecuta la lógica del controlador
 * - Los errores se añaden al BindingResult
 * - Podemos mostrar mensajes personalizados en la vista
 *
 * @author [Tu Nombre]
 * @version 1.0
 * @since 2025-11-03
 */
public record UserRegistrationDto(
        /**
         * Nombre completo del usuario
         *
         * VALIDACIONES:
         * - @NotBlank: No puede ser null, vacío o solo espacios
         * - @Size: Longitud entre 3 y 100 caracteres
         *
         * El mensaje personalizado se muestra si la validación falla.
         */
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
        String fullName,

        /**
         * Email del usuario (usado como username para login)
         *
         * VALIDACIONES:
         * - @NotBlank: No puede ser vacío
         * - @Email: Debe tener formato válido de email (user@domain.com)
         *
         * IMPORTANTE:
         * - El email debe ser único en el sistema
         * - Esta validación se hace en el servicio, no aquí
         * - El email se convierte a minúsculas antes de guardar
         */
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "Debe ser un email válido")
        String email,

        /**
         * Contraseña del usuario
         *
         * VALIDACIONES:
         * - @NotBlank: No puede ser vacía
         * - @ValidPassword: Validación de complejidad (mayúsculas, minúsculas, números, símbolos)
         *   * Mínimo 8 caracteres
         *   * Al menos una mayúscula
         *   * Al menos una minúscula
         *   * Al menos un número
         *   * Al menos un carácter especial (@$!%*?&)
         *
         * SEGURIDAD:
         * - Esta contraseña se encripta con BCrypt antes de guardar
         * - NUNCA se almacena en texto plano
         * - Validación de complejidad mejora la seguridad
         */
        @NotBlank(message = "La contraseña es obligatoria")
        @ValidPassword
        String password,

        /**
         * Rol del usuario en el sistema
         *
         * VALIDACIONES:
         * - @NotNull: El rol no puede ser null
         *
         * VALORES POSIBLES:
         * - Role.ADMIN: Administrador con permisos completos
         * - Role.LIDER: Líder de proyecto, puede crear proyectos y tareas
         * - Role.COLABORADOR: Colaborador, ejecuta tareas asignadas
         *
         * IMPORTANTE:
         * El formulario debe mostrar un <select> con los valores del enum.
         * Spring convierte automáticamente el String a enum.
         */
        @NotNull(message = "El rol es obligatorio")
        Role role
) {
    /**
     * MÉTODOS ADICIONALES (Opcional en records)
     *
     * Aunque los records son inmutables, puedes añadir métodos de utilidad.
     * Ejemplo: métodos de validación personalizada, transformación de datos, etc.
     */

    /**
     * Constructor compacto (Compact Constructor)
     *
     * Se ejecuta ANTES del constructor canónico.
     * Útil para:
     * - Normalizar datos (ej: convertir email a minúsculas)
     * - Validaciones adicionales
     * - Logging
     *
     * IMPORTANTE:
     * No puedes asignar directamente a los campos en un compact constructor.
     * Debes reasignar el parámetro:
     */
    public UserRegistrationDto {
        // Normalizar el email a minúsculas
        if (email != null) {
            email = email.toLowerCase().trim();
        }

        // Trim del nombre (eliminar espacios al inicio y final)
        if (fullName != null) {
            fullName = fullName.trim();
        }

        // La contraseña NO se debe modificar aquí (no hacer trim ni toLowerCase)
        // porque podría cambiar la contraseña que el usuario ingresó
    }

    /**
     * EJEMPLO DE VALIDACIÓN PERSONALIZADA (no implementada, pero útil conocer)
     *
     * Si quisieras validaciones más complejas que las anotaciones estándar:
     *
     * 1. Crear una anotación personalizada:
     * ```
     * @Target({FIELD})
     * @Retention(RUNTIME)
     * @Constraint(validatedBy = StrongPasswordValidator.class)
     * public @interface StrongPassword {
     *     String message() default "La contraseña debe contener mayúsculas, minúsculas, números y símbolos";
     *     Class<?>[] groups() default {};
     *     Class<? extends Payload>[] payload() default {};
     * }
     * ```
     *
     * 2. Implementar el validador:
     * ```
     * public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {
     *     @Override
     *     public boolean isValid(String password, ConstraintValidatorContext context) {
     *         if (password == null) return false;
     *         return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
     *     }
     * }
     * ```
     *
     * 3. Usar la anotación:
     * ```
     * @StrongPassword
     * String password
     * ```
     */

    /**
     * NOTAS PARA TU PROFESOR:
     *
     * 1. RECORDS vs CLASES:
     *    - Records: Inmutables, para transportar datos
     *    - Clases: Mutables, para encapsular lógica
     *    - DTOs deben ser records (o clases inmutables con Lombok)
     *
     * 2. VALIDACIONES EN CAPAS:
     *    - Frontend (HTML5, JavaScript): Validación de UX, mejora experiencia
     *    - DTO (Jakarta Validation): Validación de formato y estructura
     *    - Servicio: Validación de reglas de negocio (email único, etc.)
     *    - Base de datos: Constraints (índices únicos, foreign keys)
     *
     * 3. SEPARACIÓN DE CONCERNS:
     *    - UserRegistrationDto: Solo para crear usuarios
     *    - UserUpdateDto: Para actualizar usuarios (diferentes campos requeridos)
     *    - UserResponseDto: Para enviar usuarios al frontend (sin contraseña)
     *
     * 4. VALIDACIÓN CON @VALID:
     *    En el controlador:
     *    ```
     *    @PostMapping("/crear-usuario")
     *    public String createUser(
     *        @Valid UserRegistrationDto dto,  // Activa las validaciones
     *        BindingResult result             // Almacena los errores
     *    ) {
     *        if (result.hasErrors()) {
     *            return "form";  // Volver al formulario con errores
     *        }
     *        // Proceso normal...
     *    }
     *    ```
     *
     * 5. MENSAJES DE ERROR PERSONALIZADOS:
     *    Los mensajes se pueden internacionalizar:
     *    - Crear messages.properties
     *    - Usar claves: @NotBlank(message = "{user.fullName.notBlank}")
     *    - Spring los resuelve automáticamente según el idioma
     *
     * 6. BUENAS PRÁCTICAS:
     *    ✓ Un DTO por caso de uso (crear, actualizar, etc.)
     *    ✓ Validaciones claras y mensajes descriptivos
     *    ✓ Inmutabilidad (usar records)
     *    ✓ No incluir lógica de negocio en DTOs
     *    ✓ Documentar cada campo y sus validaciones
     */
}
