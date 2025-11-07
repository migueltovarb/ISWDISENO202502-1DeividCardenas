package com.gestionproyectos.gestion_tareas.controller;

import com.gestionproyectos.gestion_tareas.enums.ProjectStatus;
import com.gestionproyectos.gestion_tareas.enums.Role;
import com.gestionproyectos.gestion_tareas.model.Project;
import com.gestionproyectos.gestion_tareas.model.User;
import com.gestionproyectos.gestion_tareas.service.ProjectService;
import com.gestionproyectos.gestion_tareas.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * AdminDashboardController - Panel de control administrativo
 *
 * RESPONSABILIDADES:
 * - Dashboard principal con estadísticas
 * - Resumen de usuarios por rol
 * - Resumen de proyectos por estado
 * - Listado de usuarios y proyectos recientes
 *
 * REFACTORIZACIÓN:
 * Extraído de AdminController.java para mejor separación de responsabilidades.
 * Principio SRP (Single Responsibility Principle): Este controlador solo maneja
 * la visualización del dashboard administrativo.
 *
 * @author Sistema de Gestión de Tareas
 * @version 2.0
 * @since 2025-11-06
 */
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Slf4j
public class AdminDashboardController {

    private final UserService userService;
    private final ProjectService projectService;

    /**
     * Dashboard administrativo principal
     *
     * MÉTRICAS MOSTRADAS:
     * 1. Usuarios:
     *    - Total de usuarios
     *    - Distribución por rol (ADMIN, LIDER, COLABORADOR)
     *    - 5 usuarios más recientes
     *
     * 2. Proyectos:
     *    - Proyectos activos totales
     *    - Distribución por estado (PLANIFICACION, EN_PROGRESO, COMPLETADO)
     *    - 5 proyectos más recientes
     *
     * OPTIMIZACIÓN:
     * Se podrían cachear estas estadísticas con @Cacheable si el dashboard
     * se consulta frecuentemente y los datos no cambian constantemente.
     *
     * @param model Modelo para pasar datos a la vista
     * @return Vista del dashboard administrativo
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        log.debug("Mostrando dashboard de administrador");

        // ==================== ESTADÍSTICAS DE USUARIOS ====================
        long adminCount = userService.countByRole(Role.ADMIN);
        long liderCount = userService.countByRole(Role.LIDER);
        long colaboradorCount = userService.countByRole(Role.COLABORADOR);
        long totalUsers = adminCount + liderCount + colaboradorCount;

        model.addAttribute("adminCount", adminCount);
        model.addAttribute("liderCount", liderCount);
        model.addAttribute("colaboradorCount", colaboradorCount);
        model.addAttribute("totalUsers", totalUsers);

        // ==================== ESTADÍSTICAS DE PROYECTOS ====================
        long activeProjectsCount = projectService.countActiveProjects();
        long planningProjects = projectService.countByStatus(ProjectStatus.PLANIFICACION);
        long inProgressProjects = projectService.countByStatus(ProjectStatus.EN_PROGRESO);
        long completedProjects = projectService.countByStatus(ProjectStatus.COMPLETADO);

        model.addAttribute("activeProjectsCount", activeProjectsCount);
        model.addAttribute("planningProjects", planningProjects);
        model.addAttribute("inProgressProjects", inProgressProjects);
        model.addAttribute("completedProjects", completedProjects);

        // ==================== USUARIOS RECIENTES ====================
        // Top 5 usuarios creados recientemente
        List<User> recentUsers = userService.getAllActiveUsers().stream()
                .sorted((u1, u2) -> u2.getCreatedAt().compareTo(u1.getCreatedAt()))
                .limit(5)
                .toList();
        model.addAttribute("recentUsers", recentUsers);

        // ==================== PROYECTOS RECIENTES ====================
        // Top 5 proyectos creados recientemente
        List<Project> recentProjects = projectService.getAllActiveProjects().stream()
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .limit(5)
                .toList();
        model.addAttribute("recentProjects", recentProjects);

        log.debug("Dashboard cargado - {} usuarios, {} proyectos activos",
                totalUsers, activeProjectsCount);

        return "admin/dashboard";
    }

    /**
     * NOTAS PARA MEJORAS FUTURAS:
     *
     * 1. CACHÉ DE ESTADÍSTICAS:
     *    Las estadísticas cambian poco, se podrían cachear:
     *    ```
     *    @Cacheable(value = "adminStats", key = "'dashboard'")
     *    public DashboardStats getDashboardStats() { ... }
     *    ```
     *
     * 2. GRÁFICOS:
     *    Añadir gráficos con Chart.js o similar:
     *    - Distribución de usuarios por rol (pie chart)
     *    - Proyectos por estado (bar chart)
     *    - Tendencia de creación de proyectos (line chart)
     *
     * 3. FILTROS TEMPORALES:
     *    Permitir ver estadísticas de:
     *    - Última semana
     *    - Último mes
     *    - Último año
     *
     * 4. ALERTAS:
     *    Mostrar alertas si:
     *    - Hay proyectos bloqueados
     *    - Hay usuarios inactivos hace mucho tiempo
     *    - Hay proyectos vencidos
     */
}
