package com.gestionproyectos.gestion_tareas.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.NoSuchElementException;

/**
 * GlobalExceptionHandler - Manejo centralizado de excepciones
 *
 * PROPÓSITO:
 * Captura y maneja excepciones de forma centralizada en toda la aplicación.
 * Esto evita duplicar código de manejo de errores en cada controlador.
 *
 * PATRÓN: Exception Handler Pattern
 * - @ControllerAdvice aplica este manejador a todos los @Controller
 * - @ExceptionHandler define qué método maneja cada tipo de excepción
 *
 * VENTAJAS:
 * - Código DRY (Don't Repeat Yourself)
 * - Respuestas de error consistentes
 * - Logging centralizado de errores
 * - Fácil mantenimiento
 *
 * @author Sistema de Gestión de Tareas
 * @version 1.0
 * @since 2025-11-06
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Maneja IllegalArgumentException
     *
     * Se lanza cuando hay argumentos inválidos en la lógica de negocio.
     * Ejemplo: Email duplicado, usuario no encontrado, etc.
     *
     * @param ex La excepción capturada
     * @param redirectAttributes Para pasar mensajes de error
     * @return Redirección a la página anterior con mensaje de error
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(
            IllegalArgumentException ex,
            RedirectAttributes redirectAttributes) {

        log.warn("IllegalArgumentException capturada: {}", ex.getMessage());

        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/dashboard";
    }

    /**
     * Maneja NoSuchElementException
     *
     * Se lanza cuando se busca una entidad que no existe.
     * Ejemplo: Proyecto no encontrado, tarea no encontrada, etc.
     *
     * @param ex La excepción capturada
     * @param redirectAttributes Para pasar mensajes de error
     * @return Redirección con mensaje de error
     */
    @ExceptionHandler(NoSuchElementException.class)
    public String handleNoSuchElementException(
            NoSuchElementException ex,
            RedirectAttributes redirectAttributes) {

        log.warn("NoSuchElementException capturada: {}", ex.getMessage());

        redirectAttributes.addFlashAttribute("error",
            "El recurso solicitado no existe: " + ex.getMessage());
        return "redirect:/dashboard";
    }

    /**
     * Maneja MethodArgumentNotValidException
     *
     * Se lanza cuando falla la validación de un @Valid DTO.
     * Ejemplo: Email inválido, contraseña débil, campos requeridos vacíos.
     *
     * @param ex La excepción capturada
     * @param redirectAttributes Para pasar mensajes de error
     * @return Redirección con mensajes de validación
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidationException(
            MethodArgumentNotValidException ex,
            RedirectAttributes redirectAttributes) {

        log.warn("Errores de validación: {}", ex.getBindingResult().getAllErrors());

        StringBuilder errorMessage = new StringBuilder("Errores de validación: ");
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errorMessage.append(error.getField())
                .append(": ")
                .append(error.getDefaultMessage())
                .append("; ")
        );

        redirectAttributes.addFlashAttribute("error", errorMessage.toString());
        return "redirect:/dashboard";
    }

    /**
     * Maneja AccessDeniedException
     *
     * Se lanza cuando un usuario intenta acceder a un recurso sin permisos.
     * Spring Security lanza esta excepción automáticamente.
     *
     * @param ex La excepción capturada
     * @param model Modelo para la vista de error
     * @return Vista de error 403
     */
    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDeniedException(
            AccessDeniedException ex,
            Model model) {

        log.warn("Acceso denegado: {}", ex.getMessage());

        model.addAttribute("error", "No tienes permisos para acceder a este recurso");
        model.addAttribute("status", HttpStatus.FORBIDDEN.value());
        return "error/403";
    }

    /**
     * Maneja cualquier otra excepción no capturada
     *
     * Es el manejador "catch-all" para errores inesperados.
     *
     * @param ex La excepción capturada
     * @param redirectAttributes Para pasar mensajes de error
     * @return Redirección con mensaje genérico de error
     */
    @ExceptionHandler(Exception.class)
    public String handleGenericException(
            Exception ex,
            RedirectAttributes redirectAttributes) {

        log.error("Excepción inesperada capturada", ex);

        redirectAttributes.addFlashAttribute("error",
            "Ha ocurrido un error inesperado. Por favor, contacta al administrador.");
        return "redirect:/dashboard";
    }
}
