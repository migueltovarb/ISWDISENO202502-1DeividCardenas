package com.gestionproyectos.gestion_tareas.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Validador de complejidad de contraseñas - VERSIÓN OPTIMIZADA 2.0
 *
 * REQUISITOS DE SEGURIDAD:
 * - Longitud entre 8 y 64 caracteres
 * - Al menos una letra mayúscula
 * - Al menos una letra minúscula
 * - Al menos un dígito
 * - Al menos un carácter especial
 * - No contener contraseñas comunes
 * - No contener secuencias repetidas
 * - No contener patrones de teclado
 *
 * OPTIMIZACIONES v2.0:
 * - Añadida lista de contraseñas prohibidas
 * - Validación de secuencias repetidas (ej: "aaaa", "1111")
 * - Validación de patrones de teclado (ej: "qwerty", "12345")
 * - Longitud máxima para prevenir ataques DoS
 * - Mensajes de error más descriptivos
 *
 * @author Sistema de Gestión de Tareas
 * @version 2.0
 * @since 2025-11-11
 */
public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    // Patrones de validación
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile(".*[A-Z].*");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile(".*[a-z].*");
    private static final Pattern DIGIT_PATTERN = Pattern.compile(".*[0-9].*");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile(".*[@$!%*?&#+\\-_=<>].*");
    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 64;

    /**
     * Lista de contraseñas comunes PROHIBIDAS
     * Top 20 contraseñas más usadas según estudios de seguridad
     */
    private static final List<String> COMMON_PASSWORDS = Arrays.asList(
        "password", "Password1", "Password123", "password123",
        "12345678", "123456789", "1234567890",
        "qwerty", "qwerty123", "qwertyuiop",
        "admin123", "Admin123", "administrator",
        "welcome", "welcome123", "Welcome123",
        "letmein", "Login123", "login123",
        "passw0rd", "p@ssw0rd", "P@ssword1"
    );

    /**
     * Patrones de teclado comunes (secuencias consecutivas)
     */
    private static final List<String> KEYBOARD_PATTERNS = Arrays.asList(
        "qwerty", "asdfgh", "zxcvbn",
        "12345", "1234567", "123456789",
        "abcdef", "fedcba"
    );

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

        // Deshabilitar mensaje por defecto para usar mensajes personalizados
        context.disableDefaultConstraintViolation();

        // 1. VALIDAR LONGITUD
        if (password.length() < MIN_LENGTH) {
            context.buildConstraintViolationWithTemplate(
                String.format("La contraseña debe tener al menos %d caracteres", MIN_LENGTH)
            ).addConstraintViolation();
            return false;
        }

        if (password.length() > MAX_LENGTH) {
            context.buildConstraintViolationWithTemplate(
                String.format("La contraseña no puede exceder %d caracteres", MAX_LENGTH)
            ).addConstraintViolation();
            return false;
        }

        // 2. VALIDAR CONTRASEÑAS COMUNES
        if (isCommonPassword(password)) {
            context.buildConstraintViolationWithTemplate(
                "Esta contraseña es demasiado común. Por favor elige una más segura"
            ).addConstraintViolation();
            return false;
        }

        // 3. VALIDAR SECUENCIAS REPETIDAS
        if (hasRepeatedSequence(password)) {
            context.buildConstraintViolationWithTemplate(
                "La contraseña no debe contener caracteres repetidos consecutivamente (ej: 'aaaa', '1111')"
            ).addConstraintViolation();
            return false;
        }

        // 4. VALIDAR PATRONES DE TECLADO
        if (hasKeyboardPattern(password)) {
            context.buildConstraintViolationWithTemplate(
                "La contraseña no debe contener secuencias de teclado comunes (ej: 'qwerty', '12345')"
            ).addConstraintViolation();
            return false;
        }

        // 5. VALIDAR PRESENCIA DE MAYÚSCULAS
        if (!UPPERCASE_PATTERN.matcher(password).matches()) {
            context.buildConstraintViolationWithTemplate(
                "La contraseña debe contener al menos una letra mayúscula (A-Z)"
            ).addConstraintViolation();
            return false;
        }

        // 6. VALIDAR PRESENCIA DE MINÚSCULAS
        if (!LOWERCASE_PATTERN.matcher(password).matches()) {
            context.buildConstraintViolationWithTemplate(
                "La contraseña debe contener al menos una letra minúscula (a-z)"
            ).addConstraintViolation();
            return false;
        }

        // 7. VALIDAR PRESENCIA DE DÍGITOS
        if (!DIGIT_PATTERN.matcher(password).matches()) {
            context.buildConstraintViolationWithTemplate(
                "La contraseña debe contener al menos un número (0-9)"
            ).addConstraintViolation();
            return false;
        }

        // 8. VALIDAR PRESENCIA DE CARACTERES ESPECIALES
        if (!SPECIAL_CHAR_PATTERN.matcher(password).matches()) {
            context.buildConstraintViolationWithTemplate(
                "La contraseña debe contener al menos un carácter especial (@$!%*?&#+\\-_=<>)"
            ).addConstraintViolation();
            return false;
        }

        return true;
    }

    /**
     * Verifica si la contraseña está en la lista de contraseñas comunes
     *
     * @param password Contraseña a validar
     * @return true si es una contraseña común
     */
    private boolean isCommonPassword(String password) {
        String lowerPassword = password.toLowerCase();

        // Verificar coincidencia exacta
        for (String common : COMMON_PASSWORDS) {
            if (lowerPassword.equals(common.toLowerCase())) {
                return true;
            }
        }

        // Verificar si contiene contraseñas comunes como substring
        for (String common : COMMON_PASSWORDS) {
            if (lowerPassword.contains(common.toLowerCase()) && common.length() >= 6) {
                return true;
            }
        }

        return false;
    }

    /**
     * Detecta secuencias de caracteres repetidos (ej: "aaaa", "1111")
     *
     * @param password Contraseña a validar
     * @return true si contiene 4 o más caracteres repetidos consecutivos
     */
    private boolean hasRepeatedSequence(String password) {
        if (password.length() < 4) {
            return false;
        }

        for (int i = 0; i <= password.length() - 4; i++) {
            char first = password.charAt(i);
            boolean allSame = true;

            for (int j = 1; j < 4; j++) {
                if (password.charAt(i + j) != first) {
                    allSame = false;
                    break;
                }
            }

            if (allSame) {
                return true; // Encontró 4 caracteres iguales consecutivos
            }
        }

        return false;
    }

    /**
     * Detecta patrones de teclado comunes (ej: "qwerty", "12345")
     *
     * @param password Contraseña a validar
     * @return true si contiene patrones de teclado
     */
    private boolean hasKeyboardPattern(String password) {
        String lowerPassword = password.toLowerCase();

        for (String pattern : KEYBOARD_PATTERNS) {
            // Verificar patrón normal
            if (lowerPassword.contains(pattern)) {
                return true;
            }

            // Verificar patrón invertido
            String reversed = new StringBuilder(pattern).reverse().toString();
            if (lowerPassword.contains(reversed)) {
                return true;
            }
        }

        return false;
    }
}
