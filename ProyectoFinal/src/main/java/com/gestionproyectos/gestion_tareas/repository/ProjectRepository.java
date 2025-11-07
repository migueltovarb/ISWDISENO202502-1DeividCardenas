package com.gestionproyectos.gestion_tareas.repository;

import com.gestionproyectos.gestion_tareas.enums.ProjectStatus;
import com.gestionproyectos.gestion_tareas.model.Project;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * ProjectRepository - Repositorio para operaciones de base de datos con Project
 *
 * Proporciona métodos de consulta específicos para proyectos, incluyendo:
 * - Búsquedas por líder y colaboradores
 * - Filtros por estado y fechas
 * - Búsquedas por tags
 * - Proyectos archivados vs activos
 *
 * ESTRATEGIA DE CONSULTAS:
 * - Métodos derivados para consultas simples (Spring genera automáticamente)
 * - @Query para consultas complejas que involucran arrays o múltiples condiciones
 *
 * @author [Tu Nombre]
 * @version 1.0
 * @since 2025-11-03
 */
@Repository
public interface ProjectRepository extends MongoRepository<Project, String> {

    // ==================== CONSULTAS POR LÍDER ====================

    /**
     * Busca todos los proyectos de un líder específico
     *
     * MÉTODO DE CONSULTA DERIVADO:
     * db.projects.find({ leaderId: leaderId })
     *
     * Uso principal:
     * - Dashboard del líder: mostrar "Mis Proyectos"
     * - Validaciones: verificar si un usuario tiene proyectos asignados
     *
     * @param leaderId ID del usuario líder
     * @return List<Project> - Proyectos que lidera ese usuario
     */
    List<Project> findByLeaderId(String leaderId);

    /**
     * Busca proyectos no archivados de un líder
     *
     * MÉTODO DE CONSULTA DERIVADO:
     * db.projects.find({ leaderId: leaderId, archived: false })
     *
     * Filtro más común para el dashboard del líder.
     * Excluye proyectos archivados para mostrar solo los relevantes.
     *
     * @param leaderId ID del usuario líder
     * @param archived Estado de archivado
     * @return List<Project> - Proyectos activos del líder
     */
    List<Project> findByLeaderIdAndArchived(String leaderId, boolean archived);

    /**
     * Busca proyectos de un líder con un estado específico
     *
     * MÉTODO DE CONSULTA DERIVADO:
     * db.projects.find({ leaderId: leaderId, status: status })
     *
     * Ejemplos de uso:
     * - Proyectos EN_PROGRESO del líder
     * - Proyectos COMPLETADOS del líder
     * - Proyectos en PLANIFICACION del líder
     *
     * @param leaderId ID del usuario líder
     * @param status Estado del proyecto
     * @return List<Project> - Proyectos que cumplen ambos criterios
     */
    List<Project> findByLeaderIdAndStatus(String leaderId, ProjectStatus status);

    // ==================== CONSULTAS POR COLABORADORES ====================

    /**
     * Busca proyectos donde un usuario es colaborador
     *
     * CONSULTA PERSONALIZADA con @Query:
     * db.projects.find({ collaboratorIds: userId })
     *
     * En MongoDB, cuando buscas un valor en un array, usas el valor directamente.
     * Si el array contiene ese valor, el documento coincide.
     *
     * Uso:
     * - Dashboard del colaborador: "Proyectos en los que participo"
     * - Verificar acceso de un colaborador a un proyecto
     *
     * @param userId ID del usuario colaborador
     * @return List<Project> - Proyectos donde colabora
     */
    @Query("{ 'collaboratorIds': ?0 }")
    List<Project> findByCollaboratorIdsContaining(String userId);

    /**
     * Busca proyectos no archivados donde un usuario es colaborador
     *
     * CONSULTA PERSONALIZADA con @Query:
     * db.projects.find({ collaboratorIds: userId, archived: false })
     *
     * Filtro más común para colaboradores: solo proyectos activos.
     *
     * @param userId ID del usuario colaborador
     * @param archived Estado de archivado
     * @return List<Project> - Proyectos activos donde colabora
     */
    @Query("{ 'collaboratorIds': ?0, 'archived': ?1 }")
    List<Project> findByCollaboratorIdsContainingAndArchived(String userId, boolean archived);

    // ==================== CONSULTAS POR LÍDER O COLABORADOR ====================

    /**
     * Busca proyectos donde un usuario es líder O colaborador
     *
     * CONSULTA PERSONALIZADA con @Query:
     * db.projects.find({
     *   $or: [
     *     { leaderId: userId },
     *     { collaboratorIds: userId }
     *   ]
     * })
     *
     * Útil para:
     * - Dashboard general del usuario: "Todos mis proyectos"
     * - Verificar si un usuario tiene acceso a un proyecto (por cualquier rol)
     * - Listados completos de proyectos del usuario
     *
     * @param userId ID del usuario
     * @return List<Project> - Todos los proyectos donde tiene acceso
     */
    @Query("{ $or: [ { 'leaderId': ?0 }, { 'collaboratorIds': ?0 } ] }")
    List<Project> findAllByUserId(String userId);

    /**
     * Busca proyectos no archivados donde un usuario es líder O colaborador
     *
     * CONSULTA PERSONALIZADA con @Query:
     * Similar a findAllByUserId pero filtra archivados.
     *
     * Es la consulta más usada para dashboards de usuario.
     *
     * @param userId ID del usuario
     * @param archived Estado de archivado
     * @return List<Project> - Proyectos activos donde tiene acceso
     */
    @Query("{ $or: [ { 'leaderId': ?0 }, { 'collaboratorIds': ?0 } ], 'archived': ?1 }")
    List<Project> findAllByUserIdAndArchived(String userId, boolean archived);

    // ==================== CONSULTAS POR ESTADO ====================

    /**
     * Busca proyectos por estado
     *
     * MÉTODO DE CONSULTA DERIVADO:
     * db.projects.find({ status: status })
     *
     * Útil para:
     * - Dashboard de administración: ver todos los proyectos EN_PROGRESO
     * - Reportes: proyectos completados en un período
     * - Filtros en listados
     *
     * @param status Estado del proyecto
     * @return List<Project> - Proyectos con ese estado
     */
    List<Project> findByStatus(ProjectStatus status);

    /**
     * Busca proyectos no archivados con un estado específico
     *
     * MÉTODO DE CONSULTA DERIVADO:
     * db.projects.find({ status: status, archived: false })
     *
     * @param status Estado del proyecto
     * @param archived Estado de archivado
     * @return List<Project> - Proyectos que cumplen ambos criterios
     */
    List<Project> findByStatusAndArchived(ProjectStatus status, boolean archived);

    /**
     * Busca proyectos activos (estado EN_PROGRESO y no archivados)
     *
     * CONSULTA PERSONALIZADA con @Query:
     * db.projects.find({ status: "EN_PROGRESO", archived: false })
     *
     * Esta es una consulta muy común para dashboards y reportes en tiempo real.
     *
     * @return List<Project> - Proyectos activamente en ejecución
     */
    @Query("{ 'status': 'EN_PROGRESO', 'archived': false }")
    List<Project> findActiveProjects();

    // ==================== CONSULTAS POR FECHAS ====================

    /**
     * Busca proyectos que terminan antes de una fecha
     *
     * MÉTODO DE CONSULTA DERIVADO:
     * db.projects.find({ endDate: { $lte: date } })
     *
     * Útil para:
     * - Alertas de proyectos próximos a vencer
     * - Proyectos vencidos (fecha pasada + estado no completado)
     *
     * @param date Fecha límite
     * @return List<Project> - Proyectos que vencen antes o en esa fecha
     */
    List<Project> findByEndDateBefore(LocalDate date);

    /**
     * Busca proyectos que terminan después de una fecha
     *
     * MÉTODO DE CONSULTA DERIVADO:
     * db.projects.find({ endDate: { $gte: date } })
     *
     * @param date Fecha de inicio
     * @return List<Project> - Proyectos que vencen después o en esa fecha
     */
    List<Project> findByEndDateAfter(LocalDate date);

    /**
     * Busca proyectos que terminan en un rango de fechas
     *
     * MÉTODO DE CONSULTA DERIVADO:
     * db.projects.find({ endDate: { $gte: startDate, $lte: endDate } })
     *
     * Útil para:
     * - Reportes por período: "Proyectos que vencen este mes"
     * - Planificación: "Proyectos a entregar en el Q1"
     *
     * @param startDate Fecha inicial del rango
     * @param endDate Fecha final del rango
     * @return List<Project> - Proyectos en ese rango
     */
    List<Project> findByEndDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Busca proyectos vencidos que no están completados
     *
     * CONSULTA PERSONALIZADA con @Query:
     * db.projects.find({
     *   endDate: { $lt: currentDate },
     *   status: { $nin: ["COMPLETADO", "CANCELADO"] }
     * })
     *
     * Proyectos con problemas que requieren atención.
     *
     * @param currentDate Fecha actual
     * @return List<Project> - Proyectos retrasados
     */
    @Query("{ 'endDate': { $lt: ?0 }, 'status': { $nin: ['COMPLETADO', 'CANCELADO'] } }")
    List<Project> findOverdueProjects(LocalDate currentDate);

    // ==================== CONSULTAS POR TAGS ====================

    /**
     * Busca proyectos que contienen un tag específico
     *
     * CONSULTA PERSONALIZADA con @Query:
     * db.projects.find({ tags: tag })
     *
     * Útil para:
     * - Filtrar proyectos por categoría
     * - Búsquedas por tecnología: "frontend", "backend"
     * - Búsquedas por cliente o departamento
     *
     * @param tag Tag a buscar
     * @return List<Project> - Proyectos con ese tag
     */
    @Query("{ 'tags': ?0 }")
    List<Project> findByTagsContaining(String tag);

    // ==================== CONSULTAS POR ARCHIVADO ====================

    /**
     * Busca proyectos archivados
     *
     * MÉTODO DE CONSULTA DERIVADO:
     * db.projects.find({ archived: true })
     *
     * Para sección "Proyectos Archivados" en la interfaz.
     *
     * @param archived Estado de archivado
     * @return List<Project> - Proyectos archivados
     */
    List<Project> findByArchived(boolean archived);

    // ==================== BÚSQUEDAS DE TEXTO ====================

    /**
     * Busca proyectos cuyo nombre contenga un texto
     *
     * MÉTODO DE CONSULTA DERIVADO:
     * db.projects.find({ name: { $regex: ".*name.*", $options: "i" } })
     *
     * Búsqueda case-insensitive para barra de búsqueda.
     *
     * @param name Texto a buscar en el nombre
     * @return List<Project> - Proyectos cuyo nombre contiene el texto
     */
    List<Project> findByNameContainingIgnoreCase(String name);

    /**
     * Busca proyectos por nombre o descripción
     *
     * CONSULTA PERSONALIZADA con @Query:
     * db.projects.find({
     *   $or: [
     *     { name: { $regex: ".*keyword.*", $options: "i" } },
     *     { description: { $regex: ".*keyword.*", $options: "i" } }
     *   ]
     * })
     *
     * Búsqueda más amplia que busca en nombre Y descripción.
     *
     * @param keyword Palabra clave a buscar
     * @return List<Project> - Proyectos que coinciden
     */
    @Query("{ $or: [ { 'name': { $regex: ?0, $options: 'i' } }, { 'description': { $regex: ?0, $options: 'i' } } ] }")
    List<Project> findByNameOrDescriptionContaining(String keyword);

    // ==================== ESTADÍSTICAS Y CONTEOS ====================

    /**
     * Cuenta proyectos por estado
     *
     * MÉTODO DE CONSULTA DERIVADO:
     * db.projects.count({ status: status })
     *
     * Para dashboard de administración.
     *
     * @param status Estado del proyecto
     * @return long - Número de proyectos con ese estado
     */
    long countByStatus(ProjectStatus status);

    /**
     * Cuenta proyectos de un líder
     *
     * MÉTODO DE CONSULTA DERIVADO:
     * db.projects.count({ leaderId: leaderId })
     *
     * @param leaderId ID del líder
     * @return long - Número de proyectos que lidera
     */
    long countByLeaderId(String leaderId);

    /**
     * Cuenta proyectos archivados vs no archivados
     *
     * @param archived Estado de archivado
     * @return long - Número de proyectos
     */
    long countByArchived(boolean archived);

    // ==================== ORDENAMIENTO ====================

    /**
     * Busca proyectos ordenados por fecha de creación (más recientes primero)
     *
     * MÉTODO DE CONSULTA DERIVADO:
     * db.projects.find({ archived: archived }).sort({ createdAt: -1 })
     *
     * @param archived Estado de archivado
     * @return List<Project> - Proyectos ordenados por fecha descendente
     */
    List<Project> findByArchivedOrderByCreatedAtDesc(boolean archived);

    /**
     * Busca proyectos ordenados por fecha de vencimiento (más próximos primero)
     *
     * MÉTODO DE CONSULTA DERIVADO:
     * db.projects.find({ archived: archived }).sort({ endDate: 1 })
     *
     * Útil para priorizar proyectos que vencen pronto.
     *
     * @param archived Estado de archivado
     * @return List<Project> - Proyectos ordenados por fecha de vencimiento
     */
    List<Project> findByArchivedOrderByEndDateAsc(boolean archived);

    /**
     * Busca proyectos de un usuario ordenados por progreso (menor a mayor)
     *
     * CONSULTA PERSONALIZADA con @Query:
     * Proyectos del usuario ordenados por progreso ascendente.
     * Los proyectos con menor progreso aparecen primero.
     *
     * Útil para identificar proyectos que necesitan atención.
     *
     * @param userId ID del usuario (líder o colaborador)
     * @return List<Project> - Proyectos ordenados por progreso ascendente
     */
    @Query(value = "{ $or: [ { 'leaderId': ?0 }, { 'collaboratorIds': ?0 } ] }", sort = "{ 'progress': 1 }")
    List<Project> findAllByUserIdOrderByProgressAsc(String userId);

    // ==================== CONSULTAS AVANZADAS ====================

    /**
     * Busca proyectos de un usuario con progreso menor a un porcentaje
     *
     * CONSULTA PERSONALIZADA con @Query:
     * Identifica proyectos del usuario que están atrasados.
     *
     * @param userId ID del usuario
     * @param maxProgress Progreso máximo (ej: 50 para proyectos con menos del 50%)
     * @return List<Project> - Proyectos con bajo progreso
     */
    @Query("{ $or: [ { 'leaderId': ?0 }, { 'collaboratorIds': ?0 } ], 'progress': { $lt: ?1 }, 'archived': false }")
    List<Project> findByUserIdAndProgressLessThan(String userId, int maxProgress);

    /**
     * Busca proyectos próximos a vencer (en los próximos N días)
     *
     * CONSULTA PERSONALIZADA con @Query:
     * Encuentra proyectos que vencen pronto y no están completados.
     *
     * @param startDate Fecha actual
     * @param endDate Fecha límite (ej: hoy + 7 días)
     * @return List<Project> - Proyectos próximos a vencer
     */
    @Query("{ 'endDate': { $gte: ?0, $lte: ?1 }, 'status': { $nin: ['COMPLETADO', 'CANCELADO'] } }")
    List<Project> findProjectsDueSoon(LocalDate startDate, LocalDate endDate);
}
