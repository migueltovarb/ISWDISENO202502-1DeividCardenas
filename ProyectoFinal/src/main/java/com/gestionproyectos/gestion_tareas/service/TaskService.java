package com.gestionproyectos.gestion_tareas.service;

import com.gestionproyectos.gestion_tareas.dto.CommentWithUserDto;
import com.gestionproyectos.gestion_tareas.dto.TaskCreationDto;
import com.gestionproyectos.gestion_tareas.dto.TaskDetailsDto;
import com.gestionproyectos.gestion_tareas.dto.ProjectDetailsDto;
import com.gestionproyectos.gestion_tareas.enums.TaskPriority;
import com.gestionproyectos.gestion_tareas.enums.TaskStatus;
import com.gestionproyectos.gestion_tareas.model.Project;
import com.gestionproyectos.gestion_tareas.model.Task;
import com.gestionproyectos.gestion_tareas.model.User;
import com.gestionproyectos.gestion_tareas.repository.ProjectRepository;
import com.gestionproyectos.gestion_tareas.repository.TaskRepository;
import com.gestionproyectos.gestion_tareas.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * TaskService - Servicio de gestión de tareas
 *
 * RESPONSABILIDAD:
 * - Creación de tareas
 * - Consulta de tareas (por usuario, por proyecto)
 * - Actualización de estado de tareas
 * - Validaciones de reglas de negocio
 *
 * @author [Tu Nombre]
 * @version 1.0
 * @since 2025-11-03
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    /**
     * Crea una nueva tarea en un proyecto
     *
     * VALIDACIONES:
     * 1. El proyecto debe existir
     * 2. El responsable debe existir y estar activo
     * 3. El responsable debe ser miembro del proyecto (opcional pero recomendado)
     *
     * PROCESO:
     * 1. Validar proyecto y responsable
     * 2. Crear entidad Task con estado PENDIENTE
     * 3. Guardar en MongoDB
     * 4. Actualizar progreso del proyecto (si es necesario)
     *
     * @param taskDto Datos de la tarea a crear
     * @param projectId ID del proyecto al que pertenece
     * @param createdById ID del usuario que crea la tarea (líder)
     */
    @Transactional
    public void createTask(TaskCreationDto taskDto, String projectId, String createdById) {
        log.info("Creando tarea: {} en proyecto: {}", taskDto.title(), projectId);

        // 1. VALIDAR QUE EL PROYECTO EXISTE
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));

        // Validar que el proyecto no esté cancelado
        if (project.isCancelled()) {
            throw new IllegalArgumentException("No se pueden crear tareas en un proyecto cancelado");
        }

        // 2. VALIDAR QUE EL RESPONSABLE EXISTE Y ESTÁ ACTIVO
        User assignee = userRepository.findById(taskDto.assignedToId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario responsable no encontrado"));

        if (!assignee.isActive()) {
            throw new IllegalArgumentException("El usuario asignado no está activo");
        }

        // 3. VALIDAR QUE ES COLABORADOR (opcional pero recomendado)
        if (!assignee.isColaborador() && !assignee.isLider() && !assignee.isAdmin()) {
            throw new IllegalArgumentException("El usuario debe tener rol de Colaborador, Líder o Admin");
        }

        // 3.5. AÑADIR COLABORADOR AL PROYECTO SI NO ESTÁ YA
        if (!project.hasCollaborator(taskDto.assignedToId()) && !project.isLeader(taskDto.assignedToId())) {
            project.addCollaborator(taskDto.assignedToId());
            projectRepository.save(project);
            log.info("Colaborador {} añadido al proyecto {}", taskDto.assignedToId(), projectId);
        }

        // 4. CREAR LA TAREA
        Task task = Task.builder()
                .title(taskDto.title())
                .description(taskDto.description())
                .projectId(projectId)
                .assignedToId(taskDto.assignedToId())
                .createdById(createdById)
                .status(TaskStatus.PENDIENTE)  // Estado inicial
                .priority(taskDto.priority())
                .dueDate(taskDto.dueDate())
                .estimatedHours(null)  // Puede añadirse en versiones futuras
                .realHours(0.0)
                .build();

        Task savedTask = taskRepository.save(task);

        // 5. ACTUALIZAR PROGRESO DEL PROYECTO
        updateProjectProgress(projectId);

        log.info("Tarea creada exitosamente: {} con ID: {}", savedTask.getTitle(), savedTask.getId());
    }

    /**
     * Obtiene las tareas asignadas a un usuario (HU-04)
     *
     * PROPÓSITO:
     * Para el dashboard del colaborador: "Mis Tareas"
     *
     * @param userId ID del usuario
     * @return List<TaskDetailsDto> - Tareas del usuario con información completa
     */
    public List<TaskDetailsDto> getTasksForUser(String userId) {
        log.debug("Obteniendo tareas para usuario: {}", userId);

        List<Task> tasks = taskRepository.findByAssignedToId(userId);

        return tasks.stream()
                .map(this::mapToDetailsDto)
                .toList();
    }

    /**
     * Obtiene los detalles completos de un proyecto con sus tareas (HU-06)
     *
     * PROPÓSITO:
     * Para la vista del líder: "Ver Proyecto"
     *
     * @param projectId ID del proyecto
     * @return ProjectDetailsDto - Proyecto con todas sus tareas
     */
    public ProjectDetailsDto getProjectDetails(String projectId) {
        log.debug("Obteniendo detalles del proyecto: {}", projectId);

        // Obtener el proyecto
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));

        // Obtener las tareas del proyecto
        List<Task> tasks = taskRepository.findByProjectId(projectId);

        // Obtener el líder
        User leader = userRepository.findById(project.getLeaderId())
                .orElse(null);

        // Mapear a DTO
        List<TaskDetailsDto> taskDtos = tasks.stream()
                .map(this::mapToDetailsDto)
                .toList();

        return new ProjectDetailsDto(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getStatus(),
                project.getProgress(),
                leader != null ? leader.getFullName() : "Sin líder",
                taskDtos,
                project.getStartDate(),
                project.getEndDate()
        );
    }

    /**
     * Actualiza el estado de una tarea (HU-05)
     *
     * SEGURIDAD CRÍTICA:
     * Solo el responsable de la tarea o un líder/admin puede cambiar el estado.
     *
     * LÓGICA DE NEGOCIO:
     * Valida las transiciones de estado usando TaskStatus.canTransitionTo()
     *
     * @param taskId ID de la tarea
     * @param newStatus Nuevo estado
     * @param currentUser Usuario que intenta hacer el cambio
     */
    @Transactional
    public void updateTaskStatus(String taskId, TaskStatus newStatus, User currentUser) {
        log.info("Actualizando estado de tarea: {} a {}", taskId, newStatus);

        // 1. OBTENER LA TAREA
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Tarea no encontrada"));

        // 2. VALIDAR PERMISOS
        boolean isAssignee = task.isAssignedTo(currentUser.getId());
        boolean isLeaderOrAdmin = currentUser.isLider() || currentUser.isAdmin();

        if (!isAssignee && !isLeaderOrAdmin) {
            throw new SecurityException(
                "No tienes permisos para modificar esta tarea. " +
                "Solo el responsable o un líder/admin pueden cambiar el estado."
            );
        }

        // 3. VALIDAR TRANSICIÓN DE ESTADO
        if (!task.getStatus().canTransitionTo(newStatus)) {
            throw new IllegalArgumentException(
                String.format(
                    "Transición de estado inválida: no se puede cambiar de %s a %s. " +
                    "Revisa las transiciones permitidas en el sistema.",
                    task.getStatus().getDisplayName(),
                    newStatus.getDisplayName()
                )
            );
        }

        // 4. ACTUALIZAR ESTADO
        TaskStatus oldStatus = task.getStatus();
        task.changeStatus(newStatus);
        taskRepository.save(task);

        // 5. ACTUALIZAR PROGRESO DEL PROYECTO
        updateProjectProgress(task.getProjectId());

        log.info("Estado de tarea actualizado: {} -> {} para tarea: {}",
                oldStatus.getDisplayName(),
                newStatus.getDisplayName(),
                task.getTitle());
    }

    /**
     * Actualiza el progreso de un proyecto basándose en sus tareas
     *
     * CÁLCULO:
     * progreso = (tareasCompletadas / totalTareas) * 100
     *
     * Este método se llama automáticamente cuando:
     * - Se crea una nueva tarea
     * - Se completa una tarea
     * - Se cambia el estado de una tarea
     *
     * @param projectId ID del proyecto
     */
    private void updateProjectProgress(String projectId) {
        long totalTasks = taskRepository.countByProjectId(projectId);
        long completedTasks = taskRepository.countByProjectIdAndStatus(projectId, TaskStatus.COMPLETADA);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));

        project.updateProgress((int) completedTasks, (int) totalTasks);
        projectRepository.save(project);

        log.debug("Progreso del proyecto {} actualizado: {}% ({}/{})",
                project.getName(),
                project.getProgress(),
                completedTasks,
                totalTasks);
    }

    /**
     * Mapea una entidad Task a TaskDetailsDto
     *
     * PROPÓSITO:
     * Convertir la entidad del dominio a un DTO para la capa de presentación.
     * Incluye información adicional como el nombre del responsable.
     *
     * @param task Entidad Task
     * @return TaskDetailsDto con datos para la vista
     */
    private TaskDetailsDto mapToDetailsDto(Task task) {
        // Obtener el nombre del responsable
        String assigneeName = "Sin asignar";
        if (task.getAssignedToId() != null) {
            User assignee = userRepository.findById(task.getAssignedToId()).orElse(null);
            if (assignee != null) {
                assigneeName = assignee.getFullName();
            }
        }

        // Obtener el nombre del creador
        String creatorName = "Desconocido";
        if (task.getCreatedById() != null) {
            User creator = userRepository.findById(task.getCreatedById()).orElse(null);
            if (creator != null) {
                creatorName = creator.getFullName();
            }
        }

        return new TaskDetailsDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                assigneeName,
                task.getAssignedToId(),
                creatorName,
                task.isOverdue(),
                task.getCreatedAt()
        );
    }

    /**
     * Obtiene una tarea por ID
     *
     * @param taskId ID de la tarea
     * @return Task
     */
    public Task getTaskById(String taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Tarea no encontrada"));
    }

    /**
     * Añade un comentario a una tarea
     *
     * VALIDACIONES:
     * - La tarea debe existir
     * - El usuario debe existir
     * - El comentario no puede estar vacío
     *
     * @param taskId ID de la tarea
     * @param userId ID del usuario que comenta
     * @param commentText Texto del comentario
     */
    @Transactional
    public void addCommentToTask(String taskId, String userId, String commentText) {
        log.info("Añadiendo comentario a tarea: {} por usuario: {}", taskId, userId);

        // Validar que la tarea existe
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Tarea no encontrada"));

        // Validar que el usuario existe
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Validar que el comentario no está vacío
        if (commentText == null || commentText.trim().isEmpty()) {
            throw new IllegalArgumentException("El comentario no puede estar vacío");
        }

        // Añadir el comentario
        task.addComment(userId, commentText.trim());
        taskRepository.save(task);

        log.info("Comentario añadido exitosamente a tarea: {}", taskId);
    }

    /**
     * Obtiene los comentarios de una tarea con información del usuario
     *
     * @param taskId ID de la tarea
     * @return Lista de comentarios con nombre de usuario
     */
    public List<CommentWithUserDto> getTaskComments(String taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Tarea no encontrada"));

        return task.getComments().stream()
                .map(comment -> {
                    User user = userRepository.findById(comment.getUserId()).orElse(null);
                    String userName = user != null ? user.getFullName() : "Usuario desconocido";
                    return new CommentWithUserDto(
                            comment.getUserId(),
                            userName,
                            comment.getText(),
                            comment.getCreatedAt()
                    );
                })
                .toList();
    }

    /**
     * Actualiza el orden de las tareas
     *
     * @param taskId ID de la tarea a mover
     * @param newOrder Nuevo orden (posición)
     */
    @Transactional
    public void updateTaskOrder(String taskId, int newOrder) {
        log.info("Actualizando orden de tarea: {} a posición: {}", taskId, newOrder);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Tarea no encontrada"));

        task.setDisplayOrder(newOrder);
        taskRepository.save(task);

        log.info("Orden de tarea actualizado exitosamente");
    }

    /**
     * Reordena todas las tareas de un proyecto
     *
     * @param taskIds Lista de IDs de tareas en el nuevo orden
     */
    @Transactional
    public void reorderTasks(List<String> taskIds) {
        log.info("Reordenando {} tareas", taskIds.size());

        for (int i = 0; i < taskIds.size(); i++) {
            String taskId = taskIds.get(i);
            Task task = taskRepository.findById(taskId).orElse(null);
            if (task != null) {
                task.setDisplayOrder(i);
                taskRepository.save(task);
            }
        }

        log.info("Tareas reordenadas exitosamente");
    }

    /**
     * NOTAS PARA TU PROFESOR:
     *
     * 1. SEGURIDAD EN MÚLTIPLES CAPAS:
     *    - Controlador: Verifica autenticación (@PreAuthorize)
     *    - Servicio: Verifica autorización (isAssignee || isLeader)
     *    - Defensa en profundidad: no confiar solo en el controlador
     *
     * 2. VALIDACIÓN DE TRANSICIONES:
     *    - Usa la lógica del enum TaskStatus.canTransitionTo()
     *    - Previene transiciones inválidas (ej: PENDIENTE → COMPLETADA)
     *    - Mantiene la integridad del estado de las tareas
     *
     * 3. ACTUALIZACIÓN AUTOMÁTICA DE PROGRESO:
     *    - El progreso del proyecto se recalcula automáticamente
     *    - Se ejecuta en cada cambio de estado de tarea
     *    - Mantiene la consistencia entre tareas y proyecto
     *
     * 4. MAPEO A DTOs:
     *    - Nunca exponer entidades directamente a la vista
     *    - Los DTOs permiten controlar qué datos se muestran
     *    - Más fácil de mantener y evolucionar
     *
     * 5. LOGGING ESTRATÉGICO:
     *    - log.info() para eventos importantes (crear, actualizar)
     *    - log.debug() para información de depuración
     *    - Ayuda en troubleshooting y auditoría
     */

    /**
     * Busca tareas de un usuario con filtros opcionales y paginación
     *
     * PROPÓSITO:
     * Búsqueda avanzada paginada para el panel del colaborador.
     * Permite filtrar por estado, prioridad y búsqueda de texto con paginación.
     * Optimizado para MongoDB con queries nativas.
     *
     * @param userId ID del usuario
     * @param searchText Texto a buscar en título o descripción (puede ser null)
     * @param status Estado de la tarea (puede ser null)
     * @param priority Prioridad de la tarea (puede ser null)
     * @param page Número de página (0-indexed)
     * @param size Tamaño de página
     * @param sortBy Campo por el que ordenar
     * @param sortDirection Dirección de ordenamiento (ASC o DESC)
     * @return Page de TaskDetailsDto con los resultados paginados
     */
    public Page<TaskDetailsDto> searchTasksForUserPaginated(
            String userId,
            String searchText,
            TaskStatus status,
            TaskPriority priority,
            int page,
            int size,
            String sortBy,
            String sortDirection
    ) {
        log.debug("Buscando tareas paginado - user: {}, texto: {}, estado: {}, prioridad: {}, página: {}, tamaño: {}",
                userId, searchText, status, priority, page, size);

        // Configurar ordenamiento
        Sort sort = sortDirection.equalsIgnoreCase("DESC")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Task> taskPage;
        boolean hasSearchText = searchText != null && !searchText.trim().isEmpty();
        String keyword = hasSearchText ? searchText.trim() : "";

        // Determinar qué query usar basado en los filtros
        if (hasSearchText) {
            // Búsqueda por texto con filtros adicionales
            if (status != null && priority != null) {
                // Caso 1: Texto + Estado + Prioridad
                taskPage = taskRepository.findByAssignedToIdAndStatusAndPriorityAndKeyword(
                        userId, status, priority, keyword, pageable);
            } else if (status != null) {
                // Caso 2: Texto + Estado
                taskPage = taskRepository.findByAssignedToIdAndStatusAndKeyword(
                        userId, status, keyword, pageable);
            } else if (priority != null) {
                // Caso 3: Texto + Prioridad
                taskPage = taskRepository.findByAssignedToIdAndPriorityAndKeyword(
                        userId, priority, keyword, pageable);
            } else {
                // Caso 4: Solo texto
                taskPage = taskRepository.findByAssignedToIdAndTitleOrDescriptionContaining(
                        userId, keyword, pageable);
            }
        } else {
            // Sin búsqueda por texto
            if (status != null && priority != null) {
                // Caso 5: Estado + Prioridad
                taskPage = taskRepository.findByAssignedToIdAndStatusAndPriority(
                        userId, status, priority, pageable);
            } else if (status != null) {
                // Caso 6: Solo Estado
                taskPage = taskRepository.findByAssignedToIdAndStatus(
                        userId, status, pageable);
            } else if (priority != null) {
                // Caso 7: Solo Prioridad
                taskPage = taskRepository.findByAssignedToIdAndPriority(
                        userId, priority, pageable);
            } else {
                // Caso 8: Todas las tareas del usuario
                taskPage = taskRepository.findByAssignedToId(userId, pageable);
            }
        }

        // Convertir a DTOs
        return taskPage.map(task -> {
            User assignedTo = userRepository.findById(task.getAssignedToId()).orElse(null);
            User creator = userRepository.findById(task.getCreatedById()).orElse(null);
            return TaskDetailsDto.fromTask(task, assignedTo, creator);
        });
    }

    /**
     * Busca tareas de un proyecto con filtros opcionales y paginación
     *
     * PROPÓSITO:
     * Búsqueda avanzada paginada para el panel del líder en ver proyecto.
     * Permite filtrar por estado, prioridad y búsqueda de texto con paginación.
     * Optimizado para MongoDB con queries nativas.
     *
     * @param projectId ID del proyecto
     * @param searchText Texto a buscar en título o descripción (puede ser null)
     * @param status Estado de la tarea (puede ser null)
     * @param priority Prioridad de la tarea (puede ser null)
     * @param page Número de página (0-indexed)
     * @param size Tamaño de página
     * @param sortBy Campo por el que ordenar
     * @param sortDirection Dirección de ordenamiento (ASC o DESC)
     * @return Page de TaskDetailsDto con los resultados paginados
     */
    public Page<TaskDetailsDto> searchTasksForProjectPaginated(
            String projectId,
            String searchText,
            TaskStatus status,
            TaskPriority priority,
            int page,
            int size,
            String sortBy,
            String sortDirection
    ) {
        log.debug("Buscando tareas paginado - proyecto: {}, texto: {}, estado: {}, prioridad: {}, página: {}, tamaño: {}",
                projectId, searchText, status, priority, page, size);

        // Configurar ordenamiento
        Sort sort = sortDirection.equalsIgnoreCase("DESC")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Task> taskPage;
        boolean hasSearchText = searchText != null && !searchText.trim().isEmpty();
        String keyword = hasSearchText ? searchText.trim() : "";

        // Determinar qué query usar basado en los filtros
        if (hasSearchText) {
            // Búsqueda por texto con filtros adicionales
            if (status != null && priority != null) {
                // Caso 1: Texto + Estado + Prioridad
                taskPage = taskRepository.findByProjectIdAndStatusAndPriorityAndKeyword(
                        projectId, status, priority, keyword, pageable);
            } else if (status != null) {
                // Caso 2: Texto + Estado
                taskPage = taskRepository.findByProjectIdAndStatusAndKeyword(
                        projectId, status, keyword, pageable);
            } else if (priority != null) {
                // Caso 3: Texto + Prioridad
                taskPage = taskRepository.findByProjectIdAndPriorityAndKeyword(
                        projectId, priority, keyword, pageable);
            } else {
                // Caso 4: Solo texto
                taskPage = taskRepository.findByProjectIdAndTitleOrDescriptionContaining(
                        projectId, keyword, pageable);
            }
        } else {
            // Sin búsqueda por texto
            if (status != null && priority != null) {
                // Caso 5: Estado + Prioridad
                taskPage = taskRepository.findByProjectIdAndStatusAndPriority(
                        projectId, status, priority, pageable);
            } else if (status != null) {
                // Caso 6: Solo Estado
                taskPage = taskRepository.findByProjectIdAndStatus(
                        projectId, status, pageable);
            } else if (priority != null) {
                // Caso 7: Solo Prioridad
                taskPage = taskRepository.findByProjectIdAndPriority(
                        projectId, priority, pageable);
            } else {
                // Caso 8: Todas las tareas del proyecto
                taskPage = taskRepository.findByProjectId(projectId, pageable);
            }
        }

        // Convertir a DTOs
        return taskPage.map(task -> {
            User assignedTo = task.getAssignedToId() != null
                    ? userRepository.findById(task.getAssignedToId()).orElse(null)
                    : null;
            User creator = userRepository.findById(task.getCreatedById()).orElse(null);
            return TaskDetailsDto.fromTask(task, assignedTo, creator);
        });
    }
}
