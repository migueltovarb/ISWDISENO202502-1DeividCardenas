package com.gestionproyectos.gestion_tareas.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Validador de complejidad de contraseñas
 *
 * IMPLEMENTACIÓN:
 * Valida que la contraseña cumpla con los requisitos de seguridad:
 * - Longitud mínima de 8 caracteres
 * - Al menos una letra mayúscula
 * - Al menos una letra minúscula
 * - Al menos un dígito
 * - Al menos un carácter especial
 *
 * @author Sistema de Gestión de Tareas
 * @version 1.0
 * @since 2025-11-05
 */
public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    // Patrones de validación
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile(".*[A-Z].*");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile(".*[a-z].*");
    private static final Pattern DIGIT_PATTERN = Pattern.compile(".*[0-9].*");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile(".*[@$!%*?&].*");
    private static final int MIN_LENGTH = 8;

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        // No necesita inicialización especial
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        // Si la contraseña es null o vacía, se maneja con @NotBlank
        if (password == null || password.isEmpty()) {
            return true; // Dejar que @NotBlank maneje esto
        }

        // Validar longitud mínima
        if (password.length() < MIN_LENGTH) {
            return false;
        }

        // Validar presencia de mayúsculas
        if (!UPPERCASE_PATTERN.matcher(password).matches()) {
            return false;
        }

        // Validar presencia de minúsculas
        if (!LOWERCASE_PATTERN.matcher(password).matches()) {
            return false;
        }

        // Validar presencia de dígitos
        if (!DIGIT_PATTERN.matcher(password).matches()) {
            return false;
        }

        // Validar presencia de caracteres especiales
        if (!SPECIAL_CHAR_PATTERN.matcher(password).matches()) {
            return false;
        }

        return true;
    }
}
