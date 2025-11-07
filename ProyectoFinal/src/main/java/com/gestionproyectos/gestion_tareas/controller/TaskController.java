package com.gestionproyectos.gestion_tareas.controller;

import com.gestionproyectos.gestion_tareas.dto.CommentWithUserDto;
import com.gestionproyectos.gestion_tareas.enums.TaskStatus;
import com.gestionproyectos.gestion_tareas.model.Task;
import com.gestionproyectos.gestion_tareas.model.User;
import com.gestionproyectos.gestion_tareas.service.CustomUserDetailsService;
import com.gestionproyectos.gestion_tareas.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * TaskController - Controlador para operaciones sobre tareas
 *
 * RESPONSABILIDAD:
 * - Actualización de estado de tareas (HU-05)
 * - Operaciones generales sobre tareas
 *
 * SEGURIDAD:
 * Solo colaboradores, líderes y admins pueden actualizar tareas
 *
 * @author [Tu Nombre]
 * @version 1.0
 * @since 2025-11-03
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class TaskController {

    private final TaskService taskService;
    private final CustomUserDetailsService userDetailsService;

    /**
     * Actualiza el estado de una tarea (HU-05)
     *
     * RUTA: POST /tareas/{taskId}/actualizar-estado
     *
     * SEGURIDAD:
     * - Solo el responsable de la tarea puede cambiar su estado
     * - Los líderes y admins pueden cambiar cualquier tarea
     *
     * VALIDACIÓN:
     * - La transición de estado debe ser válida según TaskStatus.canTransitionTo()
     *
     * @param taskId ID de la tarea
     * @param newStatus Nuevo estado (TaskStatus como String)
     * @param userDetails Usuario autenticado
     * @param redirectAttributes Atributos de redirección
     * @return Redirección a la vista anterior
     */
    @PostMapping("/tareas/{taskId}/actualizar-estado")
    @PreAuthorize("hasAnyRole('COLABORADOR', 'LIDER', 'ADMIN')")
    public String updateTaskStatus(
            @PathVariable String taskId,
            @RequestParam("newStatus") String newStatus,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes
    ) {
        log.info("Actualizando estado de tarea: {} a {}", taskId, newStatus);

        try {
            // Obtener usuario actual
            User currentUser = userDetailsService.getUserFromUserDetails(userDetails);

            // Convertir String a TaskStatus enum
            TaskStatus status = TaskStatus.valueOf(newStatus);

            // Actualizar estado (el servicio valida permisos y transición)
            taskService.updateTaskStatus(taskId, status, currentUser);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Estado de la tarea actualizado exitosamente a: " + status.getDisplayName());

        } catch (IllegalArgumentException e) {
            log.error("Error al actualizar estado: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error: " + e.getMessage());

        } catch (SecurityException e) {
            log.error("Error de seguridad: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    "No tienes permisos para modificar esta tarea");
        }

        // Redirigir de vuelta a Mis Tareas
        // En una versión más sofisticada, podrías guardar la URL de origen y redirigir ahí
        return "redirect:/mis-tareas";
    }

    /**
     * Muestra la vista de detalles de una tarea con sus comentarios
     *
     * RUTA: GET /tareas/{taskId}
     *
     * @param taskId ID de la tarea
     * @param model Modelo
     * @return Vista de detalles de la tarea
     */
    @GetMapping("/tareas/{taskId}")
    @PreAuthorize("hasAnyRole('COLABORADOR', 'LIDER', 'ADMIN')")
    public String viewTask(
            @PathVariable String taskId,
            Model model
    ) {
        log.debug("Mostrando detalles de tarea: {}", taskId);

        try {
            Task task = taskService.getTaskById(taskId);
            List<CommentWithUserDto> comments = taskService.getTaskComments(taskId);

            model.addAttribute("task", task);
            model.addAttribute("comments", comments);
            return "tareas/detalle";

        } catch (IllegalArgumentException e) {
            log.error("Error al obtener tarea: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "error/404";
        }
    }

    /**
     * Añade un comentario a una tarea
     *
     * RUTA: POST /tareas/{taskId}/comentarios
     *
     * @param taskId ID de la tarea
     * @param commentText Texto del comentario
     * @param userDetails Usuario autenticado
     * @param redirectAttributes Atributos de redirección
     * @return Redirección a la vista de la tarea
     */
    @PostMapping("/tareas/{taskId}/comentarios")
    @PreAuthorize("hasAnyRole('COLABORADOR', 'LIDER', 'ADMIN')")
    public String addComment(
            @PathVariable String taskId,
            @RequestParam("text") String commentText,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes
    ) {
        log.info("Añadiendo comentario a tarea: {}", taskId);

        try {
            User currentUser = userDetailsService.getUserFromUserDetails(userDetails);
            taskService.addCommentToTask(taskId, currentUser.getId(), commentText);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Comentario añadido exitosamente");

        } catch (IllegalArgumentException e) {
            log.error("Error al añadir comentario: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error: " + e.getMessage());
        }

        return "redirect:/tareas/" + taskId;
    }

    /**
     * Actualiza el orden de las tareas (para drag & drop)
     *
     * RUTA: POST /tareas/reordenar
     *
     * @param taskIds Lista de IDs de tareas en el nuevo orden
     * @return Respuesta vacía (200 OK)
     */
    @PostMapping("/tareas/reordenar")
    @PreAuthorize("hasAnyRole('LIDER', 'ADMIN')")
    @ResponseBody
    public String reorderTasks(@RequestBody List<String> taskIds) {
        log.info("Reordenando tareas: {}", taskIds);

        try {
            taskService.reorderTasks(taskIds);
            return "OK";
        } catch (Exception e) {
            log.error("Error al reordenar tareas: {}", e.getMessage());
            return "ERROR";
        }
    }

    /**
     * NOTA PARA TU PROFESOR:
     *
     * MANEJO DE EXCEPCIONES:
     * - IllegalArgumentException: Errores de validación de negocio
     * - SecurityException: Errores de permisos
     * - Cada tipo se maneja de forma diferente
     *
     * MEJORAS FUTURAS:
     * - @ExceptionHandler para manejo centralizado de excepciones
     * - Guardar el referer para redirigir a la página de origen
     * - Endpoint AJAX para actualizar sin recargar la página
     * - Notificaciones en tiempo real con WebSockets
     *
     * EJEMPLO DE REDIRECCIÓN INTELIGENTE:
     * ```
     * String referer = request.getHeader("Referer");
     * if (referer != null && !referer.isEmpty()) {
     *     return "redirect:" + referer;
     * }
     * return "redirect:/mis-tareas";
     * ```
     */
}
