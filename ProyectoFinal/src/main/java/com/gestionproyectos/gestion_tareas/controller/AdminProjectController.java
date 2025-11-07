package com.gestionproyectos.gestion_tareas.controller;

import com.gestionproyectos.gestion_tareas.dto.ProjectCreationDto;
import com.gestionproyectos.gestion_tareas.dto.ProjectUpdateDto;
import com.gestionproyectos.gestion_tareas.enums.ProjectStatus;
import com.gestionproyectos.gestion_tareas.model.Project;
import com.gestionproyectos.gestion_tareas.service.ProjectService;
import com.gestionproyectos.gestion_tareas.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * AdminProjectController - Gestión completa de proyectos
 *
 * RESPONSABILIDADES:
 * - Crear nuevos proyectos y asignar líderes
 * - Listar proyectos con búsqueda y filtros
 * - Editar información de proyectos
 * - Cambiar estado de proyectos
 * - Reasignar líderes de proyectos
 *
 * REFACTORIZACIÓN:
 * Extraído de AdminController.java para cumplir con SRP.
 * Este controlador se enfoca exclusivamente en operaciones CRUD de proyectos
 * desde la perspectiva administrativa.
 *
 * DIFERENCIA CON LiderController:
 * - AdminProjectController: Administrador puede gestionar TODOS los proyectos
 * - LiderController: Líder solo gestiona SUS proyectos asignados
 *
 * RUTAS:
 * - GET  /admin/proyectos                    → Listar con filtros
 * - GET  /admin/crear-proyecto               → Formulario de creación
 * - POST /admin/crear-proyecto               → Procesar creación
 * - GET  /admin/proyectos/{id}/editar        → Formulario de edición
 * - POST /admin/proyectos/{id}/editar        → Procesar edición
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
public class AdminProjectController {

    private final ProjectService projectService;
    private final UserService userService;

    // ==================== LISTAR PROYECTOS ====================

    /**
     * Lista proyectos con búsqueda y filtros
     *
     * FILTROS DISPONIBLES:
     * - search: Busca en nombre y descripción del proyecto
     * - status: Filtra por estado (PLANIFICACION, EN_PROGRESO, COMPLETADO, etc.)
     * - includeArchived: Si true, incluye proyectos archivados
     *
     * DIFERENCIA CON LÍDER:
     * El administrador ve TODOS los proyectos del sistema,
     * mientras que el líder solo ve los proyectos donde es líder.
     *
     * OPTIMIZACIÓN FUTURA:
     * Se podría añadir paginación como en el listado de usuarios
     * si el número de proyectos crece significativamente.
     *
     * @param search Texto de búsqueda
     * @param status Estado del proyecto
     * @param includeArchived Si true, incluye archivados
     * @param model Modelo para la vista
     * @return Vista del listado de proyectos
     */
    @GetMapping("/proyectos")
    public String listProjects(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) ProjectStatus status,
            @RequestParam(required = false, defaultValue = "false") Boolean includeArchived,
            Model model) {

        log.debug("Listando proyectos - búsqueda: {}, estado: {}, incluir archivados: {}",
                search, status, includeArchived);

        List<Project> projects = projectService.searchProjects(search, status, includeArchived);

        model.addAttribute("projects", projects);
        model.addAttribute("search", search);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("includeArchived", includeArchived);
        model.addAttribute("statuses", ProjectStatus.values());

        log.debug("Se encontraron {} proyectos", projects.size());

        return "admin/proyectos";
    }

    // ==================== CREAR PROYECTO ====================

    /**
     * Muestra formulario de creación de proyecto
     *
     * CAMPOS REQUERIDOS:
     * - Nombre del proyecto
     * - Descripción
     * - Líder (se selecciona de usuarios con rol LIDER)
     *
     * VALIDACIONES:
     * - El líder debe tener rol LIDER (verificado en servicio)
     * - El nombre del proyecto debe ser único (verificado en servicio)
     *
     * @param model Modelo con DTO vacío y lista de líderes disponibles
     * @return Vista del formulario
     */
    @GetMapping("/crear-proyecto")
    public String showCreateProjectForm(Model model) {
        log.debug("Mostrando formulario de creación de proyecto");

        model.addAttribute("projectDto", new ProjectCreationDto("", "", ""));
        model.addAttribute("lideres", userService.getAvailableLideres());

        return "admin/crear-proyecto";
    }

    /**
     * Procesa la creación de un nuevo proyecto
     *
     * FLUJO:
     * 1. Validar DTO (nombre, descripción, leaderId)
     * 2. Si hay errores → volver al formulario
     * 3. ProjectService.createProject()
     *    - Verifica que el líder existe y tiene rol LIDER
     *    - Crea el proyecto con estado PLANIFICACION
     *    - Asocia el líder al proyecto
     * 4. Redirigir con mensaje de éxito
     *
     * ESTADO INICIAL:
     * El proyecto se crea en estado PLANIFICACION por defecto.
     *
     * @param projectDto DTO con datos del proyecto
     * @param result Resultado de validación
     * @param model Modelo para la vista
     * @param redirectAttributes Para mensajes flash
     * @return Redirección o vista con errores
     */
    @PostMapping("/crear-proyecto")
    public String createProject(
            @Valid @ModelAttribute("projectDto") ProjectCreationDto projectDto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        log.info("Procesando creación de proyecto: {}", projectDto.name());

        if (result.hasErrors()) {
            log.warn("Errores de validación al crear proyecto: {}", result.getAllErrors());
            model.addAttribute("lideres", userService.getAvailableLideres());
            return "admin/crear-proyecto";
        }

        try {
            projectService.createProject(projectDto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Proyecto creado exitosamente: " + projectDto.name());
            log.info("Proyecto creado exitosamente: {}", projectDto.name());
            return "redirect:/admin/crear-proyecto";
        } catch (IllegalArgumentException e) {
            log.error("Error al crear proyecto: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("lideres", userService.getAvailableLideres());
            return "admin/crear-proyecto";
        }
    }

    // ==================== EDITAR PROYECTO ====================

    /**
     * Muestra formulario de edición de proyecto
     *
     * CAMPOS EDITABLES:
     * - Nombre del proyecto
     * - Descripción
     * - Líder (se puede reasignar a otro líder)
     * - Estado (PLANIFICACION, EN_PROGRESO, PAUSADO, COMPLETADO, CANCELADO)
     *
     * TRANSICIONES DE ESTADO:
     * El enum ProjectStatus define qué transiciones son válidas:
     * - PLANIFICACION → EN_PROGRESO
     * - EN_PROGRESO → PAUSADO, COMPLETADO, CANCELADO
     * - PAUSADO → EN_PROGRESO, CANCELADO
     * - COMPLETADO/CANCELADO → (estados finales)
     *
     * @param projectId ID del proyecto a editar
     * @param model Modelo con datos del proyecto
     * @return Vista del formulario de edición
     */
    @GetMapping("/proyectos/{projectId}/editar")
    public String showEditProjectForm(@PathVariable String projectId, Model model) {
        log.debug("Mostrando formulario de edición para proyecto: {}", projectId);

        Project project = projectService.getProjectById(projectId);
        ProjectUpdateDto projectDto = new ProjectUpdateDto(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getLeaderId(),
                project.getStatus()
        );

        model.addAttribute("projectDto", projectDto);
        model.addAttribute("project", project);
        model.addAttribute("lideres", userService.getAvailableLideres());
        model.addAttribute("statuses", ProjectStatus.values());

        return "admin/editar-proyecto";
    }

    /**
     * Procesa la edición de un proyecto
     *
     * VALIDACIONES DE NEGOCIO:
     * - El nuevo líder debe tener rol LIDER
     * - La transición de estado debe ser válida según ProjectStatus
     * - No se puede cambiar de COMPLETADO/CANCELADO a otro estado
     *
     * REASIGNACIÓN DE LÍDER:
     * Si se cambia el líder:
     * 1. Se actualiza project.leaderId
     * 2. Se actualiza el Set de proyectos liderados del antiguo líder
     * 3. Se actualiza el Set de proyectos liderados del nuevo líder
     *
     * @param projectId ID del proyecto
     * @param projectDto DTO con datos actualizados
     * @param result Resultado de validación
     * @param model Modelo para la vista
     * @param redirectAttributes Para mensajes flash
     * @return Redirección o vista con errores
     */
    @PostMapping("/proyectos/{projectId}/editar")
    public String editProject(
            @PathVariable String projectId,
            @Valid @ModelAttribute("projectDto") ProjectUpdateDto projectDto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        log.info("Procesando edición de proyecto: {}", projectId);

        if (result.hasErrors()) {
            log.warn("Errores de validación al editar proyecto: {}", result.getAllErrors());
            Project project = projectService.getProjectById(projectId);
            model.addAttribute("project", project);
            model.addAttribute("lideres", userService.getAvailableLideres());
            model.addAttribute("statuses", ProjectStatus.values());
            return "admin/editar-proyecto";
        }

        try {
            projectService.updateProject(projectDto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Proyecto actualizado exitosamente");
            log.info("Proyecto actualizado exitosamente: {}", projectId);
            return "redirect:/admin/proyectos";
        } catch (IllegalArgumentException e) {
            log.error("Error al editar proyecto: {}", e.getMessage());
            Project project = projectService.getProjectById(projectId);
            model.addAttribute("project", project);
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("lideres", userService.getAvailableLideres());
            model.addAttribute("statuses", ProjectStatus.values());
            return "admin/editar-proyecto";
        }
    }

    /**
     * MEJORAS FUTURAS:
     *
     * 1. ARCHIVAR/DESARCHIVAR PROYECTOS:
     *    ```
     *    @PostMapping("/proyectos/{id}/archivar")
     *    public String archiveProject(...) { ... }
     *    ```
     *
     * 2. DUPLICAR PROYECTO:
     *    Crear un nuevo proyecto con la misma estructura
     *
     * 3. ESTADÍSTICAS DEL PROYECTO:
     *    Mostrar métricas detalladas:
     *    - Número de tareas por estado
     *    - Progreso del proyecto
     *    - Tareas vencidas
     *    - Colaboradores activos
     *
     * 4. HISTORIAL DE CAMBIOS:
     *    Registrar todos los cambios del proyecto (auditoría)
     *
     * 5. EXPORTAR PROYECTO:
     *    Exportar información del proyecto a PDF/Excel
     *
     * 6. PLANTILLAS:
     *    Crear proyectos desde plantillas predefinidas
     *
     * 7. REASIGNACIÓN MASIVA:
     *    Reasignar múltiples proyectos a un nuevo líder
     */
}
