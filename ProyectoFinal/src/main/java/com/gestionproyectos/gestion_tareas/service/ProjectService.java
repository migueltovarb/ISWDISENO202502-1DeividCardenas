package com.gestionproyectos.gestion_tareas.service;

import com.gestionproyectos.gestion_tareas.dto.ProjectCreationDto;
import com.gestionproyectos.gestion_tareas.enums.ProjectStatus;
import com.gestionproyectos.gestion_tareas.model.Project;
import com.gestionproyectos.gestion_tareas.model.Task;
import com.gestionproyectos.gestion_tareas.model.User;
import com.gestionproyectos.gestion_tareas.repository.ProjectRepository;
import com.gestionproyectos.gestion_tareas.repository.TaskRepository;
import com.gestionproyectos.gestion_tareas.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servicio de gestión de proyectos
 *
 * RESPONSABILIDADES:
 * - Crear y gestionar proyectos
 * - Mantener relaciones bidireccionales con usuarios
 * - Agregar/remover colaboradores de proyectos
 * - Consultar proyectos por usuario (líder o colaborador)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    /**
     * Crea un nuevo proyecto
     * 1. Valida que el líder existe y tiene rol LIDER o ADMIN
     * 2. Crea el proyecto con estado PLANIFICACION
     * 3. Añade el proyecto a la lista de proyectos liderados del usuario
     */
    @Transactional
    public void createProject(ProjectCreationDto dto) {
        log.info("Creando proyecto: {}", dto.name());

        // Validar que el líder existe
        User leader = userRepository.findById(dto.leaderId())
                .orElseThrow(() -> new IllegalArgumentException("Líder no encontrado"));

        // Validar que tiene rol apropiado
        if (!leader.isLider() && !leader.isAdmin()) {
            throw new IllegalArgumentException("El usuario seleccionado no es un líder válido");
        }

        // Crear proyecto
        Project project = Project.builder()
                .name(dto.name())
                .description(dto.description())
                .leaderId(dto.leaderId())
                .status(ProjectStatus.PLANIFICACION)
                .progress(0)
                .archived(false)
                .build();

        Project savedProject = projectRepository.save(project);

        // Añadir a la lista de proyectos del líder
        leader.addLeadProject(savedProject.getId());
        userRepository.save(leader);

        log.info("Proyecto creado exitosamente: {} con ID {}", savedProject.getName(), savedProject.getId());
    }

    /**
     * Obtiene todos los proyectos de un líder
     */
    public List<Project> getProjectsByLeaderId(String leaderId) {
        return projectRepository.findByLeaderId(leaderId);
    }

    /**
     * Obtiene un proyecto por ID
     */
    public Project getProjectById(String projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));
    }

    /**
     * Obtiene todos los proyectos no archivados
     */
    public List<Project> getAllActiveProjects() {
        return projectRepository.findByArchived(false);
    }

    /**
     * Obtiene todos los proyectos donde el usuario es líder o colaborador
     *
     * PROPÓSITO:
     * Para el dashboard del líder/colaborador: mostrar todos los proyectos
     * en los que participa.
     *
     * IMPORTANTE: Usa findAllByUserId que busca en MongoDB:
     * { $or: [ { leaderId: userId }, { collaboratorIds: userId } ] }
     *
     * @param userId ID del usuario
     * @return List<Project> - Proyectos donde el usuario participa
     */
    public List<Project> getProjectsForUser(String userId) {
        log.debug("Obteniendo proyectos para usuario: {}", userId);

        // Usar el método del repositorio que busca por líder O colaborador
        List<Project> projects = projectRepository.findAllByUserId(userId);

        log.info("Usuario {} tiene {} proyectos", userId, projects.size());
        return projects;
    }

    /**
     * Agrega un colaborador a un proyecto
     *
     * IMPORTANTE: Mantiene las relaciones bidireccionales
     * - Agrega el userId a project.collaboratorIds
     * - Agrega el projectId a user.collaboratorProjectIds
     *
     * @param projectId ID del proyecto
     * @param userId ID del usuario colaborador
     */
    @Transactional
    public void addCollaboratorToProject(String projectId, String userId) {
        log.info("Agregando colaborador {} al proyecto {}", userId, projectId);

        // Obtener proyecto
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));

        // Obtener usuario
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Validar que es colaborador
        if (!user.isColaborador() && !user.isLider()) {
            throw new IllegalArgumentException("El usuario debe tener rol de Colaborador o Líder");
        }

        // Validar que no sea el líder del proyecto
        if (project.isLeader(userId)) {
            throw new IllegalArgumentException("El líder ya está asignado al proyecto");
        }

        // Validar que no esté ya como colaborador
        if (project.hasCollaborator(userId)) {
            log.warn("Usuario {} ya es colaborador del proyecto {}", userId, projectId);
            return;
        }

        // RELACIÓN BIDIRECCIONAL: Actualizar ambos lados
        // Lado 1: Proyecto
        project.addCollaborator(userId);
        projectRepository.save(project);

        // Lado 2: Usuario
        user.addCollaboratorProject(projectId);
        userRepository.save(user);

        log.info("Colaborador {} agregado exitosamente al proyecto {}", userId, projectId);
    }

    /**
     * Remueve un colaborador de un proyecto
     *
     * IMPORTANTE: Mantiene las relaciones bidireccionales
     * - Remueve el userId de project.collaboratorIds
     * - Remueve el projectId de user.collaboratorProjectIds
     *
     * @param projectId ID del proyecto
     * @param userId ID del usuario colaborador
     */
    @Transactional
    public void removeCollaboratorFromProject(String projectId, String userId) {
        log.info("Removiendo colaborador {} del proyecto {}", userId, projectId);

        // Obtener proyecto
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));

        // Obtener usuario
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // RELACIÓN BIDIRECCIONAL: Actualizar ambos lados
        // Lado 1: Proyecto
        project.removeCollaborator(userId);
        projectRepository.save(project);

        // Lado 2: Usuario
        user.removeCollaboratorProject(projectId);
        userRepository.save(user);

        log.info("Colaborador {} removido exitosamente del proyecto {}", userId, projectId);
    }

    /**
     * Obtiene los colaboradores de un proyecto
     *
     * @param projectId ID del proyecto
     * @return List<User> - Lista de usuarios colaboradores
     */
    public List<User> getProjectCollaborators(String projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));

        if (project.getCollaboratorIds().isEmpty()) {
            return new ArrayList<>();
        }

        return userRepository.findByIdIn(new ArrayList<>(project.getCollaboratorIds()));
    }

    /**
     * Alterna el estado de archivado de un proyecto
     *
     * @param projectId ID del proyecto
     */
    @Transactional
    public void toggleArchiveProject(String projectId) {
        log.info("Toggling archive status for project: {}", projectId);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));

        project.setArchived(!project.isArchived());
        projectRepository.save(project);

        log.info("Proyecto {} archivado: {}", projectId, project.isArchived());
    }

    /**
     * Actualiza la información de un proyecto
     *
     * @param projectId ID del proyecto
     * @param name Nuevo nombre
     * @param description Nueva descripción
     * @param status Nuevo estado
     * @param startDate Nueva fecha de inicio (puede ser null)
     * @param endDate Nueva fecha de fin (puede ser null)
     */
    @Transactional
    public void updateProject(String projectId, String name, String description,
                              com.gestionproyectos.gestion_tareas.enums.ProjectStatus status,
                              String startDate, String endDate) {
        log.info("Actualizando proyecto: {}", projectId);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));

        // Actualizar campos básicos
        project.setName(name);
        project.setDescription(description);
        project.setStatus(status);

        // Actualizar fechas si se proporcionan
        if (startDate != null && !startDate.isEmpty()) {
            project.setStartDate(java.time.LocalDate.parse(startDate));
        }

        if (endDate != null && !endDate.isEmpty()) {
            project.setEndDate(java.time.LocalDate.parse(endDate));
        }

        projectRepository.save(project);

        log.info("Proyecto {} actualizado exitosamente", projectId);
    }

    /**
     * Actualiza un proyecto usando DTO
     *
     * PROPÓSITO:
     * Método para actualizar proyectos desde el panel de administración.
     * Maneja el cambio de líder si es necesario.
     *
     * @param dto DTO con los datos actualizados
     * @throws IllegalArgumentException si hay validaciones fallidas
     */
    @Transactional
    public void updateProject(com.gestionproyectos.gestion_tareas.dto.ProjectUpdateDto dto) {
        log.info("Actualizando proyecto con DTO: {}", dto.id());

        Project project = projectRepository.findById(dto.id())
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));

        String oldLeaderId = project.getLeaderId();
        String newLeaderId = dto.leaderId();

        // Si cambia el líder, actualizar relaciones bidireccionales
        if (!oldLeaderId.equals(newLeaderId)) {
            log.info("Cambiando líder del proyecto {} de {} a {}",
                    dto.id(), oldLeaderId, newLeaderId);

            // Remover proyecto del líder anterior
            User oldLeader = userRepository.findById(oldLeaderId)
                    .orElseThrow(() -> new IllegalArgumentException("Líder anterior no encontrado"));
            oldLeader.removeLeadProject(dto.id());
            userRepository.save(oldLeader);

            // Agregar proyecto al nuevo líder
            User newLeader = userRepository.findById(newLeaderId)
                    .orElseThrow(() -> new IllegalArgumentException("Nuevo líder no encontrado"));

            if (!newLeader.isLider() && !newLeader.isAdmin()) {
                throw new IllegalArgumentException("El usuario seleccionado no es un líder válido");
            }

            newLeader.addLeadProject(dto.id());
            userRepository.save(newLeader);

            // Actualizar líder en el proyecto
            project.setLeaderId(newLeaderId);
        }

        // Actualizar campos del proyecto
        project.setName(dto.name());
        project.setDescription(dto.description());
        project.setStatus(dto.status());

        projectRepository.save(project);
        log.info("Proyecto {} actualizado exitosamente", dto.id());
    }

    /**
     * Obtiene todos los proyectos del sistema
     *
     * PROPÓSITO:
     * Para el panel de administración - lista completa de proyectos
     *
     * @return List<Project> - Todos los proyectos (archivados y no archivados)
     */
    public List<Project> getAllProjects() {
        log.debug("Obteniendo todos los proyectos");
        return projectRepository.findAll();
    }

    /**
     * Busca proyectos con filtros opcionales
     *
     * @param searchText Texto a buscar en nombre o descripción (puede ser null)
     * @param status Estado a filtrar (puede ser null)
     * @param includeArchived Si true, incluye proyectos archivados
     * @return List<Project> con los resultados
     */
    public List<Project> searchProjects(String searchText, ProjectStatus status, boolean includeArchived) {
        log.debug("Buscando proyectos - texto: {}, estado: {}, incluir archivados: {}",
                searchText, status, includeArchived);

        List<Project> projects;

        // Caso 1: Búsqueda por texto
        if (searchText != null && !searchText.trim().isEmpty()) {
            projects = projectRepository.findByNameOrDescriptionContaining(searchText.trim());
        }
        // Caso 2: Filtro por estado
        else if (status != null) {
            projects = projectRepository.findByStatus(status);
        }
        // Caso 3: Todos los proyectos
        else {
            projects = projectRepository.findAll();
        }

        // Filtrar archivados si no se quieren incluir
        if (!includeArchived) {
            projects = projects.stream()
                    .filter(p -> !p.isArchived())
                    .toList();
        }

        // Aplicar filtro de estado si se especificó y hubo búsqueda por texto
        if (searchText != null && !searchText.trim().isEmpty() && status != null) {
            projects = projects.stream()
                    .filter(p -> p.getStatus() == status)
                    .toList();
        }

        return projects;
    }

    /**
     * Cuenta proyectos por estado
     *
     * @param status Estado del proyecto
     * @return long - Cantidad de proyectos con ese estado
     */
    public long countByStatus(ProjectStatus status) {
        return projectRepository.countByStatus(status);
    }

    /**
     * Cuenta proyectos activos (no archivados)
     *
     * @return long - Cantidad de proyectos activos
     */
    public long countActiveProjects() {
        return projectRepository.countByArchived(false);
    }

    /**
     * Sincroniza los colaboradores de un proyecto basándose en las tareas existentes
     *
     * PROPÓSITO:
     * Este método es útil para actualizar proyectos existentes que tienen tareas
     * pero no tienen los colaboradores añadidos a project.collaboratorIds
     *
     * FUNCIONALIDAD:
     * 1. Obtiene todas las tareas del proyecto
     * 2. Extrae los IDs únicos de los usuarios asignados (assignedToId)
     * 3. Añade esos usuarios a project.collaboratorIds (excluyendo al líder)
     * 4. Actualiza también user.collaboratorProjectIds para mantener relación bidireccional
     *
     * @param projectId ID del proyecto a sincronizar
     * @return int - Número de colaboradores añadidos
     */
    @Transactional
    public int syncCollaboratorsFromTasks(String projectId) {
        log.info("Sincronizando colaboradores del proyecto {} basándose en tareas existentes", projectId);

        // Obtener el proyecto
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));

        // Obtener todas las tareas del proyecto
        List<Task> tasks = taskRepository.findByProjectId(projectId);

        if (tasks.isEmpty()) {
            log.info("Proyecto {} no tiene tareas asignadas", projectId);
            return 0;
        }

        // Extraer IDs únicos de usuarios asignados a tareas
        Set<String> assignedUserIds = tasks.stream()
                .map(Task::getAssignedToId)
                .filter(userId -> userId != null && !userId.trim().isEmpty())
                .collect(Collectors.toSet());

        int addedCount = 0;

        // Añadir cada usuario como colaborador si no lo es ya y no es el líder
        for (String userId : assignedUserIds) {
            // Saltar si es el líder
            if (project.isLeader(userId)) {
                log.debug("Usuario {} es el líder del proyecto, no se añade como colaborador", userId);
                continue;
            }

            // Saltar si ya es colaborador
            if (project.hasCollaborator(userId)) {
                log.debug("Usuario {} ya es colaborador del proyecto {}", userId, projectId);
                continue;
            }

            // Obtener el usuario
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                log.warn("Usuario {} no encontrado, se omite", userId);
                continue;
            }

            // RELACIÓN BIDIRECCIONAL: Actualizar ambos lados
            // Lado 1: Proyecto
            project.addCollaborator(userId);

            // Lado 2: Usuario
            user.addCollaboratorProject(projectId);
            userRepository.save(user);

            addedCount++;
            log.info("Colaborador {} añadido al proyecto {}", userId, projectId);
        }

        // Guardar el proyecto con los colaboradores actualizados
        if (addedCount > 0) {
            projectRepository.save(project);
            log.info("Proyecto {} actualizado con {} nuevos colaboradores", projectId, addedCount);
        }

        return addedCount;
    }

    /**
     * Sincroniza los colaboradores de TODOS los proyectos basándose en las tareas existentes
     *
     * PROPÓSITO:
     * Script de migración para actualizar todos los proyectos del sistema
     * que tienen tareas pero no tienen colaboradores sincronizados
     *
     * @return int - Número total de colaboradores añadidos en todos los proyectos
     */
    @Transactional
    public int syncAllProjectCollaborators() {
        log.info("Iniciando sincronización masiva de colaboradores para todos los proyectos");

        List<Project> allProjects = projectRepository.findAll();
        int totalAdded = 0;

        for (Project project : allProjects) {
            try {
                int added = syncCollaboratorsFromTasks(project.getId());
                totalAdded += added;
            } catch (Exception e) {
                log.error("Error sincronizando proyecto {}: {}", project.getId(), e.getMessage());
            }
        }

        log.info("Sincronización masiva completada. Total de colaboradores añadidos: {}", totalAdded);
        return totalAdded;
    }
}
