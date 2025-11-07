package com.gestionproyectos.gestion_tareas.controller;

import com.gestionproyectos.gestion_tareas.dto.ProjectDetailsDto;
import com.gestionproyectos.gestion_tareas.dto.TaskCreationDto;
import com.gestionproyectos.gestion_tareas.dto.TaskDetailsDto;
import com.gestionproyectos.gestion_tareas.dto.UserSimpleDto;
import com.gestionproyectos.gestion_tareas.enums.TaskPriority;
import com.gestionproyectos.gestion_tareas.enums.TaskStatus;
import com.gestionproyectos.gestion_tareas.model.User;
import com.gestionproyectos.gestion_tareas.model.Project;
import com.gestionproyectos.gestion_tareas.enums.ProjectStatus;
import com.gestionproyectos.gestion_tareas.service.MetricsService;
import com.gestionproyectos.gestion_tareas.service.ProjectService;
import com.gestionproyectos.gestion_tareas.service.TaskService;
import com.gestionproyectos.gestion_tareas.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * LiderController - Controlador para funcionalidades del líder de proyecto
 *
 * RESPONSABILIDAD:
 * - Crear tareas en proyectos
 * - Ver detalles de proyectos con todas sus tareas
 * - Gestionar el equipo del proyecto
 *
 * SEGURIDAD:
 * Solo usuarios con rol LIDER o ADMIN pueden acceder a estos endpoints.
 *
 * @author [Tu Nombre]
 * @version 1.0
 * @since 2025-11-03
 */
@Controller
@RequestMapping("/lider")
@PreAuthorize("hasAnyRole('LIDER', 'ADMIN')")
@RequiredArgsConstructor
@Slf4j
public class LiderController {

    private final TaskService taskService;
    private final ProjectService projectService;
    private final UserService userService;
    private final MetricsService metricsService;

    // ==================== DASHBOARD CON MÉTRICAS ====================

    /**
     * Dashboard del líder con gráficos y métricas avanzadas
     *
     * RUTA: GET /lider/dashboard
     *
     * @param userDetails Usuario autenticado
     * @param model Modelo
     * @return Vista del dashboard con gráficos
     */
    @GetMapping("/dashboard")
    public String dashboard(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model
    ) {
        log.debug("Mostrando dashboard para líder: {}", userDetails.getUsername());

        // Obtener el usuario actual
        String userEmail = userDetails.getUsername();
        User currentUser = userService.getUserByEmail(userEmail);

        // Obtener métricas del líder
        var projectMetrics = metricsService.getLeaderProjectMetrics(currentUser.getId());
        var userMetrics = metricsService.getUserMetrics(currentUser.getId());

        // Obtener proyectos recientes (últimos 5)
        var allProjects = projectService.getProjectsForUser(currentUser.getId());
        var recentProjects = allProjects.stream()
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .limit(5)
                .toList();

        model.addAttribute("userName", currentUser.getFullName());
        model.addAttribute("projectMetrics", projectMetrics);
        model.addAttribute("userMetrics", userMetrics);
        model.addAttribute("recentProjects", recentProjects);

        return "lider/dashboard";
    }

    // ==================== LISTADO DE PROYECTOS ====================

    /**
     * Muestra todos los proyectos del líder
     *
     * RUTA: GET /lider/proyectos
     *
     * @param userDetails Usuario autenticado
     * @param model Modelo
     * @return Vista con lista de proyectos
     */
    @GetMapping("/proyectos")
    public String listProjects(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model
    ) {
        log.debug("Mostrando proyectos para líder: {}", userDetails.getUsername());

        // Obtener el usuario actual
        String userEmail = userDetails.getUsername();
        User currentUser = userService.getUserByEmail(userEmail);

        // Obtener proyectos del usuario (como líder o colaborador)
        List<com.gestionproyectos.gestion_tareas.model.Project> projects =
                projectService.getProjectsForUser(currentUser.getId());

        // Crear un mapa de IDs de líderes a nombres para la vista
        java.util.Map<String, String> leaderNames = new java.util.HashMap<>();
        for (com.gestionproyectos.gestion_tareas.model.Project project : projects) {
            if (project.getLeaderId() != null && !leaderNames.containsKey(project.getLeaderId())) {
                User leader = userService.getUserById(project.getLeaderId());
                if (leader != null) {
                    leaderNames.put(project.getLeaderId(), leader.getFullName());
                }
            }
        }

        model.addAttribute("projects", projects);
        model.addAttribute("leaderNames", leaderNames);
        model.addAttribute("userName", currentUser.getFullName());

        return "lider/proyectos";
    }

    // ==================== CREACIÓN DE PROYECTOS ====================

    /**
     * Muestra el formulario de creación de proyecto para el líder
     *
     * RUTA: GET /lider/crear-proyecto
     *
     * El líder será automáticamente asignado como líder del proyecto.
     *
     * @param model Modelo
     * @return Vista del formulario de creación
     */
    @GetMapping("/crear-proyecto")
    public String showCreateProjectForm(Model model) {
        log.debug("Mostrando formulario de creación de proyecto");

        // Crear un DTO vacío para el formulario
        model.addAttribute("projectDto", new com.gestionproyectos.gestion_tareas.dto.ProjectCreationDto("", "", ""));

        return "lider/crear-proyecto";
    }

    /**
     * Procesa el formulario de creación de proyecto
     *
     * RUTA: POST /lider/crear-proyecto
     *
     * El líder autenticado será automáticamente asignado como líder del proyecto.
     *
     * @param name Nombre del proyecto
     * @param description Descripción del proyecto
     * @param userDetails Usuario autenticado
     * @param model Modelo
     * @param redirectAttributes Atributos de redirección
     * @return Redirección o vista del formulario
     */
    @PostMapping("/crear-proyecto")
    public String createProject(
            @RequestParam String name,
            @RequestParam String description,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        log.info("Procesando creación de proyecto: {}", name);

        // Validaciones básicas
        if (name == null || name.trim().isEmpty()) {
            model.addAttribute("errorMessage", "El nombre del proyecto es obligatorio");
            model.addAttribute("projectDto", new com.gestionproyectos.gestion_tareas.dto.ProjectCreationDto(
                    name, description, ""));
            return "lider/crear-proyecto";
        }

        if (description == null || description.trim().isEmpty()) {
            model.addAttribute("errorMessage", "La descripción del proyecto es obligatoria");
            model.addAttribute("projectDto", new com.gestionproyectos.gestion_tareas.dto.ProjectCreationDto(
                    name, description, ""));
            return "lider/crear-proyecto";
        }

        try {
            // Obtener el ID del usuario actual (será el líder del proyecto)
            String userEmail = userDetails.getUsername();
            User currentUser = userService.getUserByEmail(userEmail);

            // Crear el DTO con el líder actual
            com.gestionproyectos.gestion_tareas.dto.ProjectCreationDto projectDto =
                    new com.gestionproyectos.gestion_tareas.dto.ProjectCreationDto(
                            name.trim(),
                            description.trim(),
                            currentUser.getId()
                    );

            // Crear el proyecto
            projectService.createProject(projectDto);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Proyecto creado exitosamente: " + name);

            // Redirigir a la lista de proyectos
            return "redirect:/lider/proyectos";

        } catch (IllegalArgumentException e) {
            log.error("Error al crear proyecto: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("projectDto", new com.gestionproyectos.gestion_tareas.dto.ProjectCreationDto(
                    name, description, ""));
            return "lider/crear-proyecto";
        }
    }

    // ==================== CREACIÓN DE TAREAS (HU-03) ====================

    /**
     * Muestra el formulario de creación de tarea
     *
     * RUTA: GET /lider/proyecto/{projectId}/nueva-tarea
     *
     * @param projectId ID del proyecto
     * @param model Modelo para la vista
     * @return Vista del formulario
     */
    @GetMapping("/proyecto/{projectId}/nueva-tarea")
    public String showCreateTaskForm(
            @PathVariable String projectId,
            Model model
    ) {
        log.debug("Mostrando formulario de creación de tarea para proyecto: {}", projectId);

        // Verificar que el proyecto existe
        projectService.getProjectById(projectId);

        // Obtener colaboradores para el select
        List<UserSimpleDto> collaborators = userService.findCollaborators();

        // Crear DTO vacío
        TaskCreationDto taskDto = new TaskCreationDto(
                "",
                "",
                "",
                TaskPriority.MEDIA,  // Prioridad por defecto
                null
        );

        model.addAttribute("projectId", projectId);
        model.addAttribute("taskDto", taskDto);
        model.addAttribute("collaborators", collaborators);
        model.addAttribute("priorities", TaskPriority.values());

        return "lider/crear-tarea";
    }

    /**
     * Procesa el formulario de creación de tarea
     *
     * RUTA: POST /lider/proyecto/{projectId}/nueva-tarea
     *
     * @param projectId ID del proyecto
     * @param taskDto Datos de la tarea
     * @param result Resultado de validación
     * @param userDetails Usuario autenticado
     * @param model Modelo
     * @param redirectAttributes Atributos de redirección
     * @return Redirección o vista del formulario
     */
    @PostMapping("/proyecto/{projectId}/nueva-tarea")
    public String createTask(
            @PathVariable String projectId,
            @Valid @ModelAttribute("taskDto") TaskCreationDto taskDto,
            BindingResult result,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        log.info("Procesando creación de tarea: {} en proyecto: {}", taskDto.title(), projectId);

        // Validar errores de formato
        if (result.hasErrors()) {
            log.warn("Errores de validación al crear tarea");
            model.addAttribute("projectId", projectId);
            model.addAttribute("collaborators", userService.findCollaborators());
            model.addAttribute("priorities", TaskPriority.values());
            return "lider/crear-tarea";
        }

        try {
            // Obtener el ID del usuario actual (creador de la tarea)
            String creatorEmail = userDetails.getUsername();
            String creatorId = userService.getUserByEmail(creatorEmail).getId();

            // Crear la tarea
            taskService.createTask(taskDto, projectId, creatorId);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Tarea creada exitosamente: " + taskDto.title());

            // Redirigir a la vista del proyecto
            return "redirect:/lider/proyecto/" + projectId;

        } catch (IllegalArgumentException e) {
            log.error("Error al crear tarea: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("projectId", projectId);
            model.addAttribute("collaborators", userService.findCollaborators());
            model.addAttribute("priorities", TaskPriority.values());
            return "lider/crear-tarea";
        }
    }

    // ==================== VISUALIZACIÓN DE PROYECTO (HU-06) ====================

    /**
     * Muestra los detalles de un proyecto con sus tareas paginadas y filtradas (HU-06)
     *
     * RUTA: GET /lider/proyecto/{projectId}
     *
     * PARÁMETROS OPCIONALES:
     * - search: Texto a buscar en título o descripción
     * - status: Filtrar por estado (PENDIENTE, EN_PROGRESO, etc.)
     * - priority: Filtrar por prioridad (BAJA, MEDIA, ALTA, CRITICA)
     * - page: Número de página (default: 0)
     * - size: Tamaño de página (default: 15)
     * - sortBy: Campo por el que ordenar (default: orderIndex)
     * - sortDirection: Dirección de ordenamiento (default: ASC)
     *
     * @param projectId ID del proyecto
     * @param search Texto de búsqueda
     * @param status Estado de la tarea
     * @param priority Prioridad de la tarea
     * @param page Número de página
     * @param size Tamaño de página
     * @param sortBy Campo de ordenamiento
     * @param sortDirection Dirección de ordenamiento
     * @param model Modelo
     * @return Vista del proyecto
     */
    @GetMapping("/proyecto/{projectId}")
    public String viewProject(
            @PathVariable String projectId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) Boolean overdue,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(defaultValue = "orderIndex") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection,
            Model model
    ) {
        log.debug("Mostrando proyecto: {} - búsqueda: {}, estado: {}, prioridad: {}, vencidas: {}, página: {}",
                projectId, search, status, priority, overdue, page);

        try {
            // Obtener información básica del proyecto
            Project project = projectService.getProjectById(projectId);

            // Obtener todas las tareas para estadísticas
            ProjectDetailsDto projectDetails = taskService.getProjectDetails(projectId);

            // Obtener tareas paginadas con filtros
            Page<TaskDetailsDto> taskPage = taskService.searchTasksForProjectPaginated(
                    projectId, search, status, priority, page, size, sortBy, sortDirection);

            // Si se filtra por vencidas, reemplazar con tareas vencidas
            List<TaskDetailsDto> displayTasks;
            if (overdue != null && overdue) {
                displayTasks = projectDetails.getOverdueTasks();
            } else {
                displayTasks = taskPage.getContent();
            }

            // Pasar datos al modelo
            model.addAttribute("project", projectDetails);
            model.addAttribute("taskPage", taskPage);
            model.addAttribute("tasks", displayTasks);
            model.addAttribute("allTasks", projectDetails.tasks()); // Para estadísticas
            model.addAttribute("search", search);
            model.addAttribute("selectedStatus", status);
            model.addAttribute("selectedPriority", priority);
            model.addAttribute("overdue", overdue);
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", size);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDirection", sortDirection);
            model.addAttribute("statuses", TaskStatus.values());
            model.addAttribute("priorities", TaskPriority.values());

            return "lider/ver-proyecto";

        } catch (IllegalArgumentException e) {
            log.error("Error al obtener proyecto: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "error/404";
        }
    }

    // ==================== EDITAR PROYECTO ====================

    /**
     * Muestra el formulario de edición de proyecto
     *
     * RUTA: GET /lider/proyecto/{projectId}/editar
     *
     * @param projectId ID del proyecto
     * @param model Modelo
     * @return Vista del formulario de edición
     */
    @GetMapping("/proyecto/{projectId}/editar")
    public String showEditProjectForm(
            @PathVariable String projectId,
            Model model
    ) {
        log.debug("Mostrando formulario de edición para proyecto: {}", projectId);

        try {
            Project project = projectService.getProjectById(projectId);
            model.addAttribute("project", project);
            model.addAttribute("statuses", ProjectStatus.values());
            return "lider/editar-proyecto";
        } catch (Exception e) {
            log.error("Error al cargar proyecto para edición: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "error/404";
        }
    }

    /**
     * Procesa la actualización de un proyecto
     *
     * RUTA: POST /lider/proyecto/{projectId}/editar
     *
     * @param projectId ID del proyecto
     * @param name Nombre del proyecto
     * @param description Descripción
     * @param status Estado
     * @param startDate Fecha de inicio
     * @param endDate Fecha de fin
     * @param redirectAttributes Atributos de redirección
     * @return Redirección
     */
    @PostMapping("/proyecto/{projectId}/editar")
    public String updateProject(
            @PathVariable String projectId,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam ProjectStatus status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            RedirectAttributes redirectAttributes
    ) {
        log.info("Actualizando proyecto: {}", projectId);

        try {
            projectService.updateProject(projectId, name, description, status, startDate, endDate);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Proyecto actualizado exitosamente");
            return "redirect:/lider/proyecto/" + projectId;
        } catch (Exception e) {
            log.error("Error al actualizar proyecto: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error al actualizar el proyecto: " + e.getMessage());
            return "redirect:/lider/proyecto/" + projectId + "/editar";
        }
    }

    // ==================== GESTIÓN DE COLABORADORES ====================

    /**
     * Muestra la vista de gestión de colaboradores
     *
     * RUTA: GET /lider/proyecto/{projectId}/colaboradores
     *
     * @param projectId ID del proyecto
     * @param model Modelo
     * @return Vista de gestión de colaboradores
     */
    @GetMapping("/proyecto/{projectId}/colaboradores")
    public String manageCollaborators(
            @PathVariable String projectId,
            Model model
    ) {
        log.debug("Mostrando gestión de colaboradores para proyecto: {}", projectId);

        try {
            Project project = projectService.getProjectById(projectId);
            List<User> currentCollaborators = projectService.getProjectCollaborators(projectId);
            List<UserSimpleDto> availableUsers = userService.findCollaborators();

            model.addAttribute("project", project);
            model.addAttribute("currentCollaborators", currentCollaborators);
            model.addAttribute("availableUsers", availableUsers);
            return "lider/gestionar-colaboradores";
        } catch (Exception e) {
            log.error("Error al cargar colaboradores: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "error/404";
        }
    }

    /**
     * Agrega un colaborador al proyecto
     *
     * RUTA: POST /lider/proyecto/{projectId}/colaboradores/agregar
     *
     * @param projectId ID del proyecto
     * @param userId ID del usuario a agregar
     * @param redirectAttributes Atributos de redirección
     * @return Redirección
     */
    @PostMapping("/proyecto/{projectId}/colaboradores/agregar")
    public String addCollaborator(
            @PathVariable String projectId,
            @RequestParam String userId,
            RedirectAttributes redirectAttributes
    ) {
        log.info("Agregando colaborador {} al proyecto {}", userId, projectId);

        try {
            projectService.addCollaboratorToProject(projectId, userId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Colaborador agregado exitosamente");
        } catch (Exception e) {
            log.error("Error al agregar colaborador: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error: " + e.getMessage());
        }

        return "redirect:/lider/proyecto/" + projectId + "/colaboradores";
    }

    /**
     * Remueve un colaborador del proyecto
     *
     * RUTA: POST /lider/proyecto/{projectId}/colaboradores/{userId}/remover
     *
     * @param projectId ID del proyecto
     * @param userId ID del usuario a remover
     * @param redirectAttributes Atributos de redirección
     * @return Redirección
     */
    @PostMapping("/proyecto/{projectId}/colaboradores/{userId}/remover")
    public String removeCollaborator(
            @PathVariable String projectId,
            @PathVariable String userId,
            RedirectAttributes redirectAttributes
    ) {
        log.info("Removiendo colaborador {} del proyecto {}", userId, projectId);

        try {
            projectService.removeCollaboratorFromProject(projectId, userId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Colaborador removido exitosamente");
        } catch (Exception e) {
            log.error("Error al remover colaborador: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error: " + e.getMessage());
        }

        return "redirect:/lider/proyecto/" + projectId + "/colaboradores";
    }

    // ==================== UTILIDADES Y SINCRONIZACIÓN ====================

    /**
     * Sincroniza los colaboradores de todos los proyectos basándose en tareas existentes
     *
     * RUTA: GET /lider/sync-collaborators
     *
     * PROPÓSITO:
     * Este endpoint permite sincronizar los colaboradores de proyectos que tienen tareas
     * asignadas pero no tienen los colaboradores añadidos al campo collaboratorIds.
     *
     * Es útil después de migraciones o cuando hay datos inconsistentes.
     *
     * @param redirectAttributes Atributos de redirección
     * @return Redirección al dashboard con mensaje de éxito
     */
    @GetMapping("/sync-collaborators")
    public String syncCollaborators(RedirectAttributes redirectAttributes) {
        log.info("Ejecutando sincronización de colaboradores para todos los proyectos");

        try {
            int totalAdded = projectService.syncAllProjectCollaborators();
            redirectAttributes.addFlashAttribute("successMessage",
                    "Sincronización completada. " + totalAdded + " colaboradores añadidos a proyectos.");
            log.info("Sincronización exitosa: {} colaboradores añadidos", totalAdded);
        } catch (Exception e) {
            log.error("Error durante la sincronización: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error durante la sincronización: " + e.getMessage());
        }

        return "redirect:/lider/dashboard";
    }

    /**
     * NOTA PARA TU PROFESOR:
     *
     * PATRÓN @AuthenticationPrincipal:
     * - Permite obtener el usuario autenticado directamente en el método
     * - Spring Security inyecta automáticamente el UserDetails
     * - Más limpio que usar SecurityContextHolder manualmente
     *
     * Ejemplo de uso:
     * ```
     * @PostMapping("/action")
     * public String action(@AuthenticationPrincipal UserDetails user) {
     *     String email = user.getUsername();  // Email del usuario logueado
     *     // ...
     * }
     * ```
     *
     * SEGURIDAD EN MÚLTIPLES CAPAS:
     * 1. @PreAuthorize: Verifica rol LIDER/ADMIN
     * 2. Servicio: Verifica que el usuario tiene acceso al proyecto
     * 3. Vista: Muestra/oculta elementos según permisos
     *
     * NUEVAS FUNCIONALIDADES IMPLEMENTADAS:
     * - Archivar/Desarchivar proyectos
     * - Editar información del proyecto
     * - Gestionar colaboradores (agregar/remover)
     */
}
