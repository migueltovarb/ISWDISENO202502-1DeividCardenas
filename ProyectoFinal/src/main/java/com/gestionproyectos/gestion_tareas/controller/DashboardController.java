package com.gestionproyectos.gestion_tareas.controller;

import com.gestionproyectos.gestion_tareas.dto.TaskDetailsDto;
import com.gestionproyectos.gestion_tareas.enums.TaskPriority;
import com.gestionproyectos.gestion_tareas.enums.TaskStatus;
import com.gestionproyectos.gestion_tareas.model.User;
import com.gestionproyectos.gestion_tareas.service.CustomUserDetailsService;
import com.gestionproyectos.gestion_tareas.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * DashboardController - Controlador para dashboards y redirecciones
 *
 * RESPONSABILIDAD:
 * - Dashboard del colaborador "Mis Tareas" (HU-04)
 * - Redirección inteligente según rol del usuario
 *
 * @author [Tu Nombre]
 * @version 1.0
 * @since 2025-11-03
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final TaskService taskService;
    private final CustomUserDetailsService userDetailsService;

    /**
     * Dashboard principal - Redirige según el rol del usuario
     *
     * RUTA: GET /dashboard
     *
     * LÓGICA:
     * - ADMIN → /admin/dashboard
     * - LIDER → /lider/proyectos (o primer proyecto si solo tiene uno)
     * - COLABORADOR → /mis-tareas
     *
     * @param userDetails Usuario autenticado
     * @return Redirección según rol
     */
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails) {
        log.debug("Redirigiendo dashboard para usuario: {}", userDetails.getUsername());

        User user = userDetailsService.getUserFromUserDetails(userDetails);

        // Redirigir según el rol
        if (user.isAdmin()) {
            return "redirect:/admin/dashboard";
        } else if (user.isLider()) {
            // Líderes van al dashboard con gráficos
            return "redirect:/lider/dashboard";
        } else {
            // Colaboradores van a ver sus tareas
            return "redirect:/mis-tareas";
        }
    }

    /**
     * Muestra las tareas asignadas al colaborador con paginación y filtros (HU-04)
     *
     * RUTA: GET /mis-tareas
     *
     * PROPÓSITO:
     * Dashboard del colaborador con todas sus tareas asignadas
     *
     * PARÁMETROS OPCIONALES:
     * - search: Texto a buscar en título o descripción
     * - status: Filtrar por estado (PENDIENTE, EN_PROGRESO, etc.)
     * - priority: Filtrar por prioridad (BAJA, MEDIA, ALTA, CRITICA)
     * - page: Número de página (default: 0)
     * - size: Tamaño de página (default: 10)
     * - sortBy: Campo por el que ordenar (default: dueDate)
     * - sortDirection: Dirección de ordenamiento (default: ASC)
     *
     * @param userDetails Usuario autenticado
     * @param search Texto de búsqueda
     * @param status Estado de la tarea
     * @param priority Prioridad de la tarea
     * @param page Número de página
     * @param size Tamaño de página
     * @param sortBy Campo de ordenamiento
     * @param sortDirection Dirección de ordenamiento
     * @param model Modelo
     * @return Vista mis-tareas
     */
    @GetMapping("/mis-tareas")
    @PreAuthorize("hasAnyRole('COLABORADOR', 'LIDER', 'ADMIN')")
    public String myTasks(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dueDate") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection,
            Model model
    ) {
        log.debug("Mostrando tareas para usuario: {} - búsqueda: {}, estado: {}, prioridad: {}, página: {}",
                userDetails.getUsername(), search, status, priority, page);

        // Obtener usuario actual
        User currentUser = userDetailsService.getUserFromUserDetails(userDetails);

        // Obtener tareas paginadas con filtros
        Page<TaskDetailsDto> taskPage = taskService.searchTasksForUserPaginated(
                currentUser.getId(), search, status, priority, page, size, sortBy, sortDirection);

        // Obtener estadísticas (todas las tareas sin filtros para las tarjetas)
        List<TaskDetailsDto> allTasks = taskService.getTasksForUser(currentUser.getId());

        // Pasar datos al modelo
        model.addAttribute("taskPage", taskPage);
        model.addAttribute("tasks", taskPage.getContent());
        model.addAttribute("allTasks", allTasks); // Para estadísticas
        model.addAttribute("userName", currentUser.getFullName());
        model.addAttribute("search", search);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedPriority", priority);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("statuses", TaskStatus.values());
        model.addAttribute("priorities", TaskPriority.values());

        return "colaborador/mis-tareas";
    }

    /**
     * NOTA PARA TU PROFESOR:
     *
     * PATRÓN DE REDIRECCIÓN INTELIGENTE:
     * En lugar de tener múltiples landing pages, tenemos una ruta común /dashboard
     * que redirige dinámicamente según el rol del usuario.
     *
     * VENTAJAS:
     * - URL consistente después del login
     * - Fácil de recordar para los usuarios
     * - Más fácil de mantener (un solo punto de entrada)
     * - Flexible para añadir lógica adicional (ej: redirigir a página de onboarding)
     *
     * MEJORAS FUTURAS:
     * - Guardar la última página visitada y redirigir ahí
     * - Dashboards personalizados por usuario
     * - Análisis de preferencias de usuario
     */
}
