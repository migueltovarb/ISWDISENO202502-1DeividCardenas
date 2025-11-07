package com.gestionproyectos.gestion_tareas.repository;

import com.gestionproyectos.gestion_tareas.enums.TaskPriority;
import com.gestionproyectos.gestion_tareas.enums.TaskStatus;
import com.gestionproyectos.gestion_tareas.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * TaskRepository - Repositorio para operaciones de base de datos con Task
 *
 * Proporciona consultas especializadas para tareas, incluyendo:
 * - Búsquedas por proyecto, usuario asignado y creador
 * - Filtros por estado, prioridad y fechas
 * - Consultas para tableros Kanban
 * - Identificación de tareas vencidas o bloqueadas
 *
 * ORGANIZACIÓN DE CONSULTAS:
 * Las consultas están agrupadas por funcionalidad para facilitar el mantenimiento:
 * 1. Consultas por proyecto
 * 2. Consultas por usuario asignado
 * 3. Consultas por estado y prioridad
 * 4. Consultas por fechas
 * 5. Consultas combinadas
 * 6. Estadísticas
 *
 * @author [Tu Nombre]
 * @version 1.0
 * @since 2025-11-03
 */
@Repository
public interface TaskRepository extends MongoRepository<Task, String> {

    // ==================== CONSULTAS POR PROYECTO ====================

    /**
     * Busca todas las tareas de un proyecto
     *
     * MÉTODO DE CONSULTA DERIVADO:
     * db.tasks.find({ projectId: projectId })
     *
     * Uso principal:
     * - Vista de proyecto: mostrar todas sus tareas
     * - Cálculo de progreso del proyecto
     * - Tableros Kanban del proyecto
     *
     * @param projectId ID del proyecto
     * @return List<Task> - Todas las tareas del proyecto
     */
    List<Task> findByProjectId(String projectId);

    /**
     * Busca tareas de un proyecto con un estado específico
     *
     * MÉTODO DE CONSULTA DERIVADO:
     * db.tasks.find({ projectId: projectId, status: status })
     *
     * Ejemplos de uso:
     * - Tareas PENDIENTES de un proyecto (backlog)
     * - Tareas COMPLETADAS (para calcular progreso)
     * - Tareas BLOQUEADAS (requieren atención)
     *
     * @param projectId ID del proyecto
     * @param status Estado de la tarea
     * @return List<Task> - Tareas que cumplen ambos criterios
     */
    List<Task> findByProjectIdAndStatus(String projectId, TaskStatus status);

    /**
     * Busca tareas de un proyecto con una prioridad específica
     *
     * MÉTODO DE CONSULTA DERIVADO:
     * db.tasks.find({ projectId: projectId, priority: priority })
     *
     * Útil para:
     * - Filtrar tareas CRITICAS de un proyecto
     * - Organizar backlog por prioridad
     *
     * @param projectId ID del proyecto
     * @param priority Prioridad de la tarea
     * @return List<Task> - Tareas con esa prioridad
     */
    List<Task> findByProjectIdAndPriority(String projectId, TaskPriority priority);

    /**
     * Busca tareas de un proyecto ordenadas por prioridad (mayor a menor)
     *
     * MÉTODO DE CONSULTA DERIVADO:
     * db.tasks.find({ projectId: projectId }).sort({ priority: -1 })
     *
     * Para mostrar las tareas más importantes primero.
     *
     * NOTA: El ordenamiento es por el valor ordinal del enum (CRITICA=4, ALTA=3, etc.)
     *
     * @param projectId ID del proyecto
     * @return List<Task> - Tareas ordenadas por prioridad descendente
     */
    List<Task> findByProjectIdOrderByPriorityDesc(String projectId);

    // ==================== CONSULTAS POR USUARIO ASIGNADO ====================

    /**
     * Busca todas las tareas asignadas a un usuario
     *
     * MÉTODO DE CONSULTA DERIVADO:
     * db.tasks.find({ assignedToId: userId })
     *
     * Uso principal:
     * - Dashboard del colaborador: "Mis Tareas"
     * - Vista de carga de trabajo del usuario
     *
     * @param userId ID del usuario asignado
     * @return List<Task> - Tareas asignadas a ese usuario
     */
    List<Task> findByAssignedToId(String userId);

    /**
     * Busca tareas asignadas a un usuario con un estado específico
     *
     * MÉTODO DE CONSULTA DERIVADO:
     * db.tasks.find({ assignedToId: userId, status: status })
     *
     * Ejemplos comunes:
     * - Tareas EN_PROGRESO del usuario (trabajo actual)
     * - Tareas PENDIENTES del usuario (pendientes de iniciar)
     * - Tareas COMPLETADAS del usuario (para métricas)
     *
     * @param userId ID del usuario
     * @param status Estado de la tarea
     * @return List<Task> - Tareas asignadas con ese estado
     */
    List<Task> findByAssignedToIdAndStatus(String userId, TaskStatus status);

    /**
     * Busca tareas asignadas a un usuario en un proyecto específico
     *
     * MÉTODO DE CONSULTA DERIVADO:
     * db.tasks.find({ assignedToId: userId, projectId: projectId })
     *
     * Útil para:
     * - Vista del colaborador en un proyecto específico
     * - Reportes de contribución por proyecto
     *
     * @param userId ID del usuario
     * @param projectId ID del proyecto
     * @return List<Task> - Tareas del usuario en ese proyecto
     */
    List<Task> findByAssignedToIdAndProjectId(String userId, String projectId);

    /**
     * Busca tareas asignadas a un usuario ordenadas por prioridad
     *
     * MÉTODO DE CONSULTA DERIVADO:
     * db.tasks.find({ assignedToId: userId }).sort({ priority: -1 })
     *
     * Para que el usuario vea primero sus tareas más importantes.
     *
     * @param userId ID del usuario
     * @return List<Task> - Tareas ordenadas por prioridad descendente
     */
    List<Task> findByAssignedToIdOrderByPriorityDesc(String userId);

    /**
     * Busca tareas asignadas a un usuario ordenadas por fecha de vencimiento
     *
     * MÉTODO DE CONSULTA DERIVADO:
     * db.tasks.find({ assignedToId: userId }).sort({ dueDate: 1 })
     *
     * Muestra primero las tareas que vencen más pronto.
     *
     * @param userId ID del usuario
     * @return List<Task> - Tareas ordenadas por fecha de vencimiento ascendente
     */
    List<Task> findByAssignedToIdOrderByDueDateAsc(String userId);

    // ==================== CONSULTAS POR CREADOR ====================

    /**
     * Busca tareas creadas por un usuario específico
     *
     * MÉTODO DE CONSULTA DERIVADO:
     * db.tasks.find({ createdById: userId })
     *
     * Útil para:
     * - Métricas: tareas creadas por líder
     * - Auditoría: rastrear quién creó cada tarea
     *
     * @param userId ID del usuario creador
     * @return List<Task> - Tareas creadas por ese usuario
     */
    List<Task> findByCreatedById(String userId);

    /**
     * Busca tareas creadas por un usuario en un proyecto
     *
     * @param userId ID del usuario creador
     * @param projectId ID del proyecto
     * @return List<Task> - Tareas creadas por ese usuario en el proyecto
     */
    List<Task> findByCreatedByIdAndProjectId(String userId, String projectId);

    // ==================== CONSULTAS POR ESTADO ====================

    /**
     * Busca tareas por estado
     *
     * MÉTODO DE CONSULTA DERIVADO:
     * db.tasks.find({ status: status })
     *
     * Para dashboards administrativos y reportes globales.
     *
     * @param status Estado de la tarea
     * @return List<Task> - Tareas con ese estado
     */
    List<Task> findByStatus(TaskStatus status);

    /**
     * Busca tareas bloqueadas
     *
     * MÉTODO DE CONSULTA DERIVADO:
     * db.tasks.find({ status: "BLOQUEADA" })
     *
     * Shortcut para identificar tareas que requieren desbloqueo.
     *
     * @return List<Task> - Tareas bloqueadas en todo el sistema
     */
    default List<Task> findBlockedTasks() {
        return findByStatus(TaskStatus.BLOQUEADA);
    }

    /**
     * Busca tareas bloqueadas de un proyecto
     *
     * @param projectId ID del proyecto
     * @return List<Task> - Tareas bloqueadas del proyecto
     */
    default List<Task> findBlockedTasksByProject(String projectId) {
        return findByProjectIdAndStatus(projectId, TaskStatus.BLOQUEADA);
    }

    // ==================== CONSULTAS POR PRIORIDAD ====================

    /**
     * Busca tareas por prioridad
     *
     * MÉTODO DE CONSULTA DERIVADO:
     * db.tasks.find({ priority: priority })
     *
     * @param priority Prioridad de la tarea
     * @return List<Task> - Tareas con esa prioridad
     */
    List<Task> findByPriority(TaskPriority priority);

    /**
     * Busca tareas críticas
     *
     * Shortcut para encontrar todas las tareas de máxima prioridad.
     *
     * @return List<Task> - Tareas con prioridad CRITICA
     */
    default List<Task> findCriticalTasks() {
        return findByPriority(TaskPriority.CRITICA);
    }

    /**
     * Busca tareas críticas de un usuario
     *
     * @param userId ID del usuario
     * @return List<Task> - Tareas críticas asignadas al usuario
     */
    default List<Task> findCriticalTasksByUser(String userId) {
        return findByAssignedToIdAndPriority(userId, TaskPriority.CRITICA);
    }

    /**
     * Busca tareas asignadas a un usuario con una prioridad específica
     *
     * @param userId ID del usuario
     * @param priority Prioridad
     * @return List<Task> - Tareas que cumplen ambos criterios
     */
    List<Task> findByAssignedToIdAndPriority(String userId, TaskPriority priority);

    // ==================== CONSULTAS POR FECHAS ====================

    /**
     * Busca tareas que vencen antes de una fecha
     *
     * MÉTODO DE CONSULTA DERIVADO:
     * db.tasks.find({ dueDate: { $lte: date } })
     *
     * @param date Fecha límite
     * @return List<Task> - Tareas que vencen antes o en esa fecha
     */
    List<Task> findByDueDateBefore(LocalDate date);

    /**
     * Busca tareas que vencen después de una fecha
     *
     * @param date Fecha de inicio
     * @return List<Task> - Tareas que vencen después o en esa fecha
     */
    List<Task> findByDueDateAfter(LocalDate date);

    /**
     * Busca tareas que vencen en un rango de fechas
     *
     * @param startDate Fecha inicial
     * @param endDate Fecha final
     * @return List<Task> - Tareas en ese rango
     */
    List<Task> findByDueDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Busca tareas vencidas (fecha pasada + no completadas)
     *
     * CONSULTA PERSONALIZADA con @Query:
     * db.tasks.find({
     *   dueDate: { $lt: currentDate },
     *   status: { $ne: "COMPLETADA" }
     * })
     *
     * Identifica tareas atrasadas que requieren atención.
     *
     * @param currentDate Fecha actual
     * @return List<Task> - Tareas vencidas
     */
    @Query("{ 'dueDate': { $lt: ?0 }, 'status': { $ne: 'COMPLETADA' } }")
    List<Task> findOverdueTasks(LocalDate currentDate);

    /**
     * Busca tareas vencidas de un usuario
     *
     * @param userId ID del usuario
     * @param currentDate Fecha actual
     * @return List<Task> - Tareas vencidas del usuario
     */
    @Query("{ 'assignedToId': ?0, 'dueDate': { $lt: ?1 }, 'status': { $ne: 'COMPLETADA' } }")
    List<Task> findOverdueTasksByUser(String userId, LocalDate currentDate);

    /**
     * Busca tareas vencidas de un proyecto
     *
     * @param projectId ID del proyecto
     * @param currentDate Fecha actual
     * @return List<Task> - Tareas vencidas del proyecto
     */
    @Query("{ 'projectId': ?0, 'dueDate': { $lt: ?1 }, 'status': { $ne: 'COMPLETADA' } }")
    List<Task> findOverdueTasksByProject(String projectId, LocalDate currentDate);

    /**
     * Busca tareas que vencen pronto (en los próximos N días)
     *
     * @param startDate Fecha actual
     * @param endDate Fecha límite (ej: hoy + 3 días)
     * @return List<Task> - Tareas próximas a vencer
     */
    @Query("{ 'dueDate': { $gte: ?0, $lte: ?1 }, 'status': { $ne: 'COMPLETADA' } }")
    List<Task> findTasksDueSoon(LocalDate startDate, LocalDate endDate);

    /**
     * Busca tareas de un usuario que vencen pronto
     *
     * @param userId ID del usuario
     * @param startDate Fecha actual
     * @param endDate Fecha límite
     * @return List<Task> - Tareas del usuario próximas a vencer
     */
    @Query("{ 'assignedToId': ?0, 'dueDate': { $gte: ?1, $lte: ?2 }, 'status': { $ne: 'COMPLETADA' } }")
    List<Task> findTasksDueSoonByUser(String userId, LocalDate startDate, LocalDate endDate);

    // ==================== CONSULTAS POR TAGS ====================

    /**
     * Busca tareas que contienen un tag específico
     *
     * CONSULTA PERSONALIZADA con @Query:
     * db.tasks.find({ tags: tag })
     *
     * @param tag Tag a buscar
     * @return List<Task> - Tareas con ese tag
     */
    @Query("{ 'tags': ?0 }")
    List<Task> findByTagsContaining(String tag);

    /**
     * Busca tareas de un proyecto con un tag específico
     *
     * @param projectId ID del proyecto
     * @param tag Tag a buscar
     * @return List<Task> - Tareas del proyecto con ese tag
     */
    @Query("{ 'projectId': ?0, 'tags': ?1 }")
    List<Task> findByProjectIdAndTag(String projectId, String tag);

    // ==================== BÚSQUEDAS DE TEXTO ====================

    /**
     * Busca tareas cuyo título contenga un texto
     *
     * MÉTODO DE CONSULTA DERIVADO:
     * db.tasks.find({ title: { $regex: ".*text.*", $options: "i" } })
     *
     * Búsqueda case-insensitive.
     *
     * @param text Texto a buscar
     * @return List<Task> - Tareas cuyo título contiene el texto
     */
    List<Task> findByTitleContainingIgnoreCase(String text);

    /**
     * Busca tareas por título o descripción
     *
     * CONSULTA PERSONALIZADA con @Query:
     * Búsqueda más amplia que busca en título Y descripción.
     *
     * @param keyword Palabra clave
     * @return List<Task> - Tareas que coinciden
     */
    @Query("{ $or: [ { 'title': { $regex: ?0, $options: 'i' } }, { 'description': { $regex: ?0, $options: 'i' } } ] }")
    List<Task> findByTitleOrDescriptionContaining(String keyword);

    // ==================== TAREAS SIN ASIGNAR ====================

    /**
     * Busca tareas sin asignar de un proyecto
     *
     * CONSULTA PERSONALIZADA con @Query:
     * db.tasks.find({ projectId: projectId, assignedToId: null })
     *
     * Tareas en el backlog que necesitan ser asignadas.
     *
     * @param projectId ID del proyecto
     * @return List<Task> - Tareas sin asignar
     */
    @Query("{ 'projectId': ?0, 'assignedToId': null }")
    List<Task> findUnassignedTasksByProject(String projectId);

    /**
     * Busca todas las tareas sin asignar
     *
     * @return List<Task> - Tareas sin asignar en todo el sistema
     */
    @Query("{ 'assignedToId': null }")
    List<Task> findUnassignedTasks();

    // ==================== ESTADÍSTICAS Y CONTEOS ====================

    /**
     * Cuenta tareas de un proyecto
     *
     * @param projectId ID del proyecto
     * @return long - Número total de tareas del proyecto
     */
    long countByProjectId(String projectId);

    /**
     * Cuenta tareas de un proyecto por estado
     *
     * Para calcular el progreso del proyecto.
     *
     * @param projectId ID del proyecto
     * @param status Estado
     * @return long - Número de tareas con ese estado
     */
    long countByProjectIdAndStatus(String projectId, TaskStatus status);

    /**
     * Cuenta tareas asignadas a un usuario
     *
     * @param userId ID del usuario
     * @return long - Número de tareas asignadas
     */
    long countByAssignedToId(String userId);

    /**
     * Cuenta tareas asignadas a un usuario por estado
     *
     * @param userId ID del usuario
     * @param status Estado
     * @return long - Número de tareas
     */
    long countByAssignedToIdAndStatus(String userId, TaskStatus status);

    /**
     * Cuenta tareas por estado
     *
     * @param status Estado
     * @return long - Número de tareas con ese estado
     */
    long countByStatus(TaskStatus status);

    /**
     * Cuenta tareas por prioridad
     *
     * @param priority Prioridad
     * @return long - Número de tareas con esa prioridad
     */
    long countByPriority(TaskPriority priority);

    // ==================== CONSULTAS COMBINADAS AVANZADAS ====================

    /**
     * Busca tareas activas (EN_PROGRESO o EN_REVISION) de un usuario
     *
     * CONSULTA PERSONALIZADA con @Query:
     * Tareas en las que el usuario está trabajando actualmente.
     *
     * @param userId ID del usuario
     * @return List<Task> - Tareas activas del usuario
     */
    @Query("{ 'assignedToId': ?0, 'status': { $in: ['EN_PROGRESO', 'EN_REVISION'] } }")
    List<Task> findActiveTasksByUser(String userId);

    /**
     * Busca tareas de alta prioridad vencidas
     *
     * CONSULTA PERSONALIZADA con @Query:
     * Tareas urgentes que requieren atención inmediata.
     *
     * @param currentDate Fecha actual
     * @return List<Task> - Tareas de alta prioridad vencidas
     */
    @Query("{ 'dueDate': { $lt: ?0 }, 'status': { $ne: 'COMPLETADA' }, 'priority': { $in: ['ALTA', 'CRITICA'] } }")
    List<Task> findHighPriorityOverdueTasks(LocalDate currentDate);

    /**
     * Busca tareas de un usuario en un proyecto con un estado específico
     *
     * Consulta combinada para filtros detallados.
     *
     * @param userId ID del usuario
     * @param projectId ID del proyecto
     * @param status Estado
     * @return List<Task> - Tareas que cumplen todos los criterios
     */
    List<Task> findByAssignedToIdAndProjectIdAndStatus(String userId, String projectId, TaskStatus status);

    /**
     * Busca tareas pendientes de revisión (EN_REVISION)
     *
     * Para que los líderes revisen el trabajo de sus colaboradores.
     *
     * @param projectId ID del proyecto
     * @return List<Task> - Tareas pendientes de revisión
     */
    default List<Task> findTasksPendingReview(String projectId) {
        return findByProjectIdAndStatus(projectId, TaskStatus.EN_REVISION);
    }

    /**
     * Elimina todas las tareas de un proyecto
     *
     * IMPORTANTE: Usar con precaución. Se ejecuta cuando se elimina un proyecto.
     * Considerar soft delete en lugar de eliminación física.
     *
     * @param projectId ID del proyecto
     */
    void deleteByProjectId(String projectId);

    // ==================== CONSULTAS CON PAGINACIÓN ====================

    /**
     * Busca tareas asignadas a un usuario con paginación
     *
     * @param userId ID del usuario
     * @param pageable Configuración de paginación y ordenamiento
     * @return Page<Task> - Página con las tareas del usuario
     */
    Page<Task> findByAssignedToId(String userId, Pageable pageable);

    /**
     * Busca tareas asignadas a un usuario por estado con paginación
     *
     * @param userId ID del usuario
     * @param status Estado de la tarea
     * @param pageable Configuración de paginación
     * @return Page<Task> - Página con tareas del usuario con ese estado
     */
    Page<Task> findByAssignedToIdAndStatus(String userId, TaskStatus status, Pageable pageable);

    /**
     * Busca tareas asignadas a un usuario por prioridad con paginación
     *
     * @param userId ID del usuario
     * @param priority Prioridad de la tarea
     * @param pageable Configuración de paginación
     * @return Page<Task> - Página con tareas del usuario con esa prioridad
     */
    Page<Task> findByAssignedToIdAndPriority(String userId, TaskPriority priority, Pageable pageable);

    /**
     * Busca tareas por usuario, estado y prioridad con paginación
     *
     * @param userId ID del usuario
     * @param status Estado de la tarea
     * @param priority Prioridad de la tarea
     * @param pageable Configuración de paginación
     * @return Page<Task> - Página con tareas que cumplen todos los criterios
     */
    Page<Task> findByAssignedToIdAndStatusAndPriority(String userId, TaskStatus status, TaskPriority priority, Pageable pageable);

    /**
     * Busca tareas del usuario por título (búsqueda de texto) con paginación
     *
     * @param userId ID del usuario
     * @param title Texto a buscar en el título
     * @param pageable Configuración de paginación
     * @return Page<Task> - Página con tareas que coinciden
     */
    Page<Task> findByAssignedToIdAndTitleContainingIgnoreCase(String userId, String title, Pageable pageable);

    /**
     * Busca tareas del usuario por título o descripción con paginación
     *
     * @param userId ID del usuario
     * @param keyword Palabra clave a buscar
     * @param pageable Configuración de paginación
     * @return Page<Task> - Página con tareas que coinciden
     */
    @Query("{ 'assignedToId': ?0, $or: [ { 'title': { $regex: ?1, $options: 'i' } }, { 'description': { $regex: ?1, $options: 'i' } } ] }")
    Page<Task> findByAssignedToIdAndTitleOrDescriptionContaining(String userId, String keyword, Pageable pageable);

    /**
     * Búsqueda combinada: usuario + estado + texto con paginación
     *
     * @param userId ID del usuario
     * @param status Estado de la tarea
     * @param keyword Palabra clave
     * @param pageable Configuración de paginación
     * @return Page<Task> - Página con tareas que coinciden
     */
    @Query("{ 'assignedToId': ?0, 'status': ?1, $or: [ { 'title': { $regex: ?2, $options: 'i' } }, { 'description': { $regex: ?2, $options: 'i' } } ] }")
    Page<Task> findByAssignedToIdAndStatusAndKeyword(String userId, TaskStatus status, String keyword, Pageable pageable);

    /**
     * Búsqueda combinada: usuario + prioridad + texto con paginación
     *
     * @param userId ID del usuario
     * @param priority Prioridad de la tarea
     * @param keyword Palabra clave
     * @param pageable Configuración de paginación
     * @return Page<Task> - Página con tareas que coinciden
     */
    @Query("{ 'assignedToId': ?0, 'priority': ?1, $or: [ { 'title': { $regex: ?2, $options: 'i' } }, { 'description': { $regex: ?2, $options: 'i' } } ] }")
    Page<Task> findByAssignedToIdAndPriorityAndKeyword(String userId, TaskPriority priority, String keyword, Pageable pageable);

    /**
     * Búsqueda combinada completa: usuario + estado + prioridad + texto con paginación
     *
     * @param userId ID del usuario
     * @param status Estado de la tarea
     * @param priority Prioridad de la tarea
     * @param keyword Palabra clave
     * @param pageable Configuración de paginación
     * @return Page<Task> - Página con tareas que coinciden con todos los criterios
     */
    @Query("{ 'assignedToId': ?0, 'status': ?1, 'priority': ?2, $or: [ { 'title': { $regex: ?3, $options: 'i' } }, { 'description': { $regex: ?3, $options: 'i' } } ] }")
    Page<Task> findByAssignedToIdAndStatusAndPriorityAndKeyword(String userId, TaskStatus status, TaskPriority priority, String keyword, Pageable pageable);

    // ==================== CONSULTAS PAGINADAS POR PROYECTO ====================

    /**
     * Busca tareas de un proyecto con paginación
     *
     * @param projectId ID del proyecto
     * @param pageable Configuración de paginación y ordenamiento
     * @return Page<Task> - Página con las tareas del proyecto
     */
    Page<Task> findByProjectId(String projectId, Pageable pageable);

    /**
     * Busca tareas de un proyecto por estado con paginación
     *
     * @param projectId ID del proyecto
     * @param status Estado de la tarea
     * @param pageable Configuración de paginación
     * @return Page<Task> - Página con tareas del proyecto con ese estado
     */
    Page<Task> findByProjectIdAndStatus(String projectId, TaskStatus status, Pageable pageable);

    /**
     * Busca tareas de un proyecto por prioridad con paginación
     *
     * @param projectId ID del proyecto
     * @param priority Prioridad de la tarea
     * @param pageable Configuración de paginación
     * @return Page<Task> - Página con tareas del proyecto con esa prioridad
     */
    Page<Task> findByProjectIdAndPriority(String projectId, TaskPriority priority, Pageable pageable);

    /**
     * Busca tareas por proyecto, estado y prioridad con paginación
     *
     * @param projectId ID del proyecto
     * @param status Estado de la tarea
     * @param priority Prioridad de la tarea
     * @param pageable Configuración de paginación
     * @return Page<Task> - Página con tareas que cumplen todos los criterios
     */
    Page<Task> findByProjectIdAndStatusAndPriority(String projectId, TaskStatus status, TaskPriority priority, Pageable pageable);

    /**
     * Busca tareas del proyecto por título (búsqueda de texto) con paginación
     *
     * @param projectId ID del proyecto
     * @param title Texto a buscar en el título
     * @param pageable Configuración de paginación
     * @return Page<Task> - Página con tareas que coinciden
     */
    Page<Task> findByProjectIdAndTitleContainingIgnoreCase(String projectId, String title, Pageable pageable);

    /**
     * Busca tareas del proyecto por título o descripción con paginación
     *
     * @param projectId ID del proyecto
     * @param keyword Palabra clave a buscar
     * @param pageable Configuración de paginación
     * @return Page<Task> - Página con tareas que coinciden
     */
    @Query("{ 'projectId': ?0, $or: [ { 'title': { $regex: ?1, $options: 'i' } }, { 'description': { $regex: ?1, $options: 'i' } } ] }")
    Page<Task> findByProjectIdAndTitleOrDescriptionContaining(String projectId, String keyword, Pageable pageable);

    /**
     * Búsqueda combinada: proyecto + estado + texto con paginación
     *
     * @param projectId ID del proyecto
     * @param status Estado de la tarea
     * @param keyword Palabra clave
     * @param pageable Configuración de paginación
     * @return Page<Task> - Página con tareas que coinciden
     */
    @Query("{ 'projectId': ?0, 'status': ?1, $or: [ { 'title': { $regex: ?2, $options: 'i' } }, { 'description': { $regex: ?2, $options: 'i' } } ] }")
    Page<Task> findByProjectIdAndStatusAndKeyword(String projectId, TaskStatus status, String keyword, Pageable pageable);

    /**
     * Búsqueda combinada: proyecto + prioridad + texto con paginación
     *
     * @param projectId ID del proyecto
     * @param priority Prioridad de la tarea
     * @param keyword Palabra clave
     * @param pageable Configuración de paginación
     * @return Page<Task> - Página con tareas que coinciden
     */
    @Query("{ 'projectId': ?0, 'priority': ?1, $or: [ { 'title': { $regex: ?2, $options: 'i' } }, { 'description': { $regex: ?2, $options: 'i' } } ] }")
    Page<Task> findByProjectIdAndPriorityAndKeyword(String projectId, TaskPriority priority, String keyword, Pageable pageable);

    /**
     * Búsqueda combinada completa: proyecto + estado + prioridad + texto con paginación
     *
     * @param projectId ID del proyecto
     * @param status Estado de la tarea
     * @param priority Prioridad de la tarea
     * @param keyword Palabra clave
     * @param pageable Configuración de paginación
     * @return Page<Task> - Página con tareas que coinciden con todos los criterios
     */
    @Query("{ 'projectId': ?0, 'status': ?1, 'priority': ?2, $or: [ { 'title': { $regex: ?3, $options: 'i' } }, { 'description': { $regex: ?3, $options: 'i' } } ] }")
    Page<Task> findByProjectIdAndStatusAndPriorityAndKeyword(String projectId, TaskStatus status, TaskPriority priority, String keyword, Pageable pageable);
}
