package com.gestionproyectos.gestion_tareas.repository;

import com.gestionproyectos.gestion_tareas.enums.Role;
import com.gestionproyectos.gestion_tareas.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * UserRepository - Repositorio para operaciones de base de datos con User
 *
 * Esta interfaz extiende MongoRepository, que proporciona operaciones CRUD básicas
 * y métodos de consulta automáticos para MongoDB.
 *
 * PATRÓN DE DISEÑO: Repository Pattern
 * - Abstrae el acceso a datos de la lógica de negocio
 * - Proporciona una interfaz limpia para operaciones de persistencia
 * - Facilita testing mediante mocks
 * - Desacopla la capa de dominio de la implementación de BD
 *
 * SPRING DATA MONGODB:
 * MongoRepository<User, String> proporciona automáticamente:
 * - save(User) - Guarda o actualiza un usuario
 * - findById(String) - Busca por ID
 * - findAll() - Obtiene todos los usuarios
 * - deleteById(String) - Elimina por ID
 * - count() - Cuenta total de usuarios
 * - existsById(String) - Verifica si existe
 *
 * QUERY METHODS:
 * Spring Data genera automáticamente la implementación basándose en el nombre del método.
 * Ejemplo: findByEmail → SELECT * FROM users WHERE email = ?
 *
 * IMPORTANTE:
 * - No necesitas implementar esta interfaz, Spring lo hace automáticamente
 * - Los métodos de consulta se generan en tiempo de ejecución
 * - Puedes usar @Query para consultas complejas personalizadas
 *
 * @author [Tu Nombre]
 * @version 1.0
 * @since 2025-11-03
 */
@Repository  // Indica que esta es una clase de acceso a datos (para inyección de dependencias)
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Busca un usuario por su email
     *
     * MÉTODO DE CONSULTA DERIVADO:
     * Spring Data traduce el nombre del método a una consulta MongoDB:
     * db.users.findOne({ email: email })
     *
     * IMPORTANTE para Spring Security:
     * Este método es fundamental para la autenticación, ya que el email
     * se usa como username en el sistema.
     *
     * USO:
     * Optional<User> user = userRepository.findByEmail("juan@example.com");
     * if (user.isPresent()) {
     *     // Usuario encontrado
     * }
     *
     * @param email Email del usuario a buscar (case-sensitive)
     * @return Optional<User> - Usuario si existe, Optional.empty() si no
     */
    Optional<User> findByEmail(String email);

    /**
     * Busca un usuario por email ignorando mayúsculas/minúsculas
     *
     * MÉTODO DE CONSULTA DERIVADO:
     * db.users.findOne({ email: { $regex: "^email$", $options: "i" } })
     *
     * Útil para búsquedas donde el usuario puede ingresar el email
     * con diferentes combinaciones de mayúsculas/minúsculas.
     *
     * Ejemplo:
     * - "Juan@Example.COM"
     * - "juan@example.com"
     * - "JUAN@EXAMPLE.COM"
     * Todos encontrarán al mismo usuario.
     *
     * @param email Email del usuario (case-insensitive)
     * @return Optional<User> - Usuario si existe
     */
    Optional<User> findByEmailIgnoreCase(String email);

    /**
     * Verifica si existe un usuario con un email específico
     *
     * MÉTODO DE CONSULTA DERIVADO:
     * db.users.findOne({ email: email }, { _id: 1 })
     *
     * Más eficiente que findByEmail() cuando solo necesitas saber si existe,
     * porque no carga todos los campos del documento.
     *
     * USO TÍPICO: Validación de registro
     * if (userRepository.existsByEmail(email)) {
     *     throw new Exception("El email ya está registrado");
     * }
     *
     * @param email Email a verificar
     * @return boolean - true si existe un usuario con ese email
     */
    boolean existsByEmail(String email);

    /**
     * Busca todos los usuarios con un rol específico
     *
     * MÉTODO DE CONSULTA DERIVADO:
     * db.users.find({ role: role })
     *
     * Ejemplos de uso:
     * - Obtener todos los administradores: findByRole(Role.ADMIN)
     * - Obtener todos los líderes: findByRole(Role.LIDER)
     * - Obtener todos los colaboradores: findByRole(Role.COLABORADOR)
     *
     * Útil para:
     * - Panel de administración (listar admins)
     * - Asignación de líderes a proyectos
     * - Reportes por tipo de usuario
     *
     * @param role Rol a buscar
     * @return List<User> - Lista de usuarios con ese rol
     */
    List<User> findByRole(Role role);

    /**
     * Busca todos los usuarios activos
     *
     * MÉTODO DE CONSULTA DERIVADO:
     * db.users.find({ active: true })
     *
     * Filtra usuarios que no han sido desactivados (soft delete).
     * Debe usarse en la mayoría de listados para no mostrar usuarios eliminados.
     *
     * @param active Estado de activación (generalmente true)
     * @return List<User> - Lista de usuarios activos
     */
    List<User> findByActive(boolean active);

    /**
     * Busca usuarios activos con un rol específico
     *
     * MÉTODO DE CONSULTA DERIVADO:
     * db.users.find({ active: true, role: role })
     *
     * Combina dos filtros: estado activo + rol específico.
     * Es la consulta más común para listados de usuarios por rol.
     *
     * Ejemplo:
     * // Obtener líderes activos para asignar a un nuevo proyecto
     * List<User> activeLideres = userRepository.findByActiveAndRole(true, Role.LIDER);
     *
     * @param active Estado de activación
     * @param role Rol del usuario
     * @return List<User> - Lista de usuarios activos con ese rol
     */
    List<User> findByActiveAndRole(boolean active, Role role);

    /**
     * Busca usuarios cuyo nombre completo contenga un texto
     *
     * MÉTODO DE CONSULTA DERIVADO:
     * db.users.find({ fullName: { $regex: ".*name.*", $options: "i" } })
     *
     * Búsqueda case-insensitive que encuentra coincidencias parciales.
     *
     * Ejemplo:
     * findByFullNameContainingIgnoreCase("Juan")
     * Encontrará: "Juan Pérez", "María Juana", "Pedro San Juan"
     *
     * Útil para:
     * - Barra de búsqueda de usuarios
     * - Autocompletado en asignación de tareas
     * - Filtros en tablas de usuarios
     *
     * @param name Texto a buscar en el nombre
     * @return List<User> - Usuarios cuyo nombre contiene el texto
     */
    List<User> findByFullNameContainingIgnoreCase(String name);

    /**
     * Busca usuarios por IDs específicos
     *
     * MÉTODO DE CONSULTA DERIVADO:
     * db.users.find({ _id: { $in: [id1, id2, id3] } })
     *
     * Útil para cargar múltiples usuarios de una vez, por ejemplo:
     * - Todos los colaboradores de un proyecto
     * - Usuarios asignados a un conjunto de tareas
     * - Miembros de un equipo
     *
     * Ejemplo:
     * Set<String> collaboratorIds = project.getCollaboratorIds();
     * List<User> collaborators = userRepository.findByIdIn(new ArrayList<>(collaboratorIds));
     *
     * @param ids Lista de IDs de usuarios
     * @return List<User> - Usuarios que coincidan con alguno de los IDs
     */
    List<User> findByIdIn(List<String> ids);

    /**
     * Busca usuarios que lideran un proyecto específico
     *
     * CONSULTA PERSONALIZADA con @Query:
     * Busca usuarios cuyo array 'leadProjectIds' contiene el projectId especificado.
     *
     * En MongoDB: db.users.find({ leadProjectIds: projectId })
     *
     * Nota: Aunque generalmente un proyecto tiene un solo líder,
     * este método retorna una lista por si en el futuro se permite co-liderazgo.
     *
     * Uso:
     * List<User> lideres = userRepository.findByLeadProjectIdsContaining(projectId);
     *
     * @param projectId ID del proyecto
     * @return List<User> - Usuarios que lideran ese proyecto
     */
    @Query("{ 'leadProjectIds': ?0 }")
    List<User> findByLeadProjectIdsContaining(String projectId);

    /**
     * Busca usuarios que colaboran en un proyecto específico
     *
     * CONSULTA PERSONALIZADA con @Query:
     * Busca usuarios cuyo array 'collaboratorProjectIds' contiene el projectId.
     *
     * En MongoDB: db.users.find({ collaboratorProjectIds: projectId })
     *
     * Útil para:
     * - Mostrar el equipo de un proyecto
     * - Listar colaboradores para asignación de tareas
     * - Enviar notificaciones a todos los miembros
     *
     * @param projectId ID del proyecto
     * @return List<User> - Usuarios colaboradores de ese proyecto
     */
    @Query("{ 'collaboratorProjectIds': ?0 }")
    List<User> findByCollaboratorProjectIdsContaining(String projectId);

    /**
     * Busca todos los usuarios relacionados con un proyecto
     * (líder o colaborador)
     *
     * CONSULTA PERSONALIZADA con @Query:
     * Usa el operador $or para buscar en ambos arrays.
     *
     * En MongoDB:
     * db.users.find({
     *   $or: [
     *     { leadProjectIds: projectId },
     *     { collaboratorProjectIds: projectId }
     *   ]
     * })
     *
     * Útil para:
     * - Obtener todos los miembros de un proyecto (líder + colaboradores)
     * - Control de acceso (verificar si un usuario tiene acceso al proyecto)
     * - Notificaciones a todo el equipo
     *
     * @param projectId ID del proyecto
     * @return List<User> - Todos los usuarios relacionados con el proyecto
     */
    @Query("{ $or: [ { 'leadProjectIds': ?0 }, { 'collaboratorProjectIds': ?0 } ] }")
    List<User> findAllByProjectId(String projectId);

    /**
     * Cuenta cuántos usuarios tienen un rol específico
     *
     * MÉTODO DE CONSULTA DERIVADO:
     * db.users.count({ role: role })
     *
     * Útil para:
     * - Estadísticas del sistema (ej: "5 administradores, 20 líderes, 50 colaboradores")
     * - Reportes de distribución de roles
     * - Dashboard administrativo
     *
     * @param role Rol a contar
     * @return long - Número de usuarios con ese rol
     */
    long countByRole(Role role);

    /**
     * Cuenta cuántos usuarios activos hay en el sistema
     *
     * MÉTODO DE CONSULTA DERIVADO:
     * db.users.count({ active: true })
     *
     * @param active Estado de activación
     * @return long - Número de usuarios activos
     */
    long countByActive(boolean active);

    /**
     * Busca usuarios ordenados por fecha de creación (más recientes primero)
     *
     * MÉTODO DE CONSULTA DERIVADO:
     * db.users.find({ active: active }).sort({ createdAt: -1 })
     *
     * Útil para:
     * - Mostrar usuarios recién registrados
     * - Dashboard de administración
     * - Reportes de crecimiento de usuarios
     *
     * @param active Estado de activación
     * @return List<User> - Usuarios ordenados por fecha de creación descendente
     */
    List<User> findByActiveOrderByCreatedAtDesc(boolean active);

    /**
     * Busca usuarios por email o nombre completo (búsqueda combinada)
     *
     * MÉTODO DE CONSULTA DERIVADO:
     * Busca en ambos campos de manera case-insensitive
     *
     * Útil para:
     * - Barra de búsqueda general de usuarios
     * - Filtros avanzados en panel de admin
     *
     * @param email Texto a buscar en email
     * @param fullName Texto a buscar en nombre
     * @return List<User> - Usuarios que coincidan con email o nombre
     */
    List<User> findByEmailContainingIgnoreCaseOrFullNameContainingIgnoreCase(String email, String fullName);

    /**
     * Busca todos los usuarios ordenados por nombre
     *
     * MÉTODO DE CONSULTA DERIVADO:
     * db.users.find().sort({ fullName: 1 })
     *
     * @return List<User> - Usuarios ordenados alfabéticamente por nombre
     */
    List<User> findAllByOrderByFullNameAsc();

    /**
     * Cuenta usuarios activos por rol
     *
     * MÉTODO DE CONSULTA DERIVADO:
     * db.users.count({ active: true, role: role })
     *
     * @param active Estado de activación
     * @param role Rol a contar
     * @return long - Número de usuarios activos con ese rol
     */
    long countByActiveAndRole(boolean active, Role role);

    // ==================== MÉTODOS CON PAGINACIÓN ====================

    /**
     * Busca usuarios con paginación y ordenamiento
     *
     * MÉTODO PAGINADO:
     * Retorna una Page<User> que contiene:
     * - Los usuarios de la página actual
     * - Número total de páginas
     * - Número total de elementos
     * - Si hay página siguiente/anterior
     *
     * Uso:
     * PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("fullName").ascending());
     * Page<User> page = userRepository.findAllBy(pageRequest);
     *
     * @param pageable Configuración de paginación y ordenamiento
     * @return Page<User> - Página con usuarios
     */
    Page<User> findAllBy(Pageable pageable);

    /**
     * Busca usuarios activos con paginación
     *
     * @param active Estado de activación
     * @param pageable Configuración de paginación
     * @return Page<User> - Página con usuarios activos
     */
    Page<User> findByActive(boolean active, Pageable pageable);

    /**
     * Busca usuarios por rol con paginación
     *
     * @param role Rol a buscar
     * @param pageable Configuración de paginación
     * @return Page<User> - Página con usuarios del rol especificado
     */
    Page<User> findByRole(Role role, Pageable pageable);

    /**
     * Busca usuarios activos por rol con paginación
     *
     * @param active Estado de activación
     * @param role Rol a buscar
     * @param pageable Configuración de paginación
     * @return Page<User> - Página con usuarios activos del rol especificado
     */
    Page<User> findByActiveAndRole(boolean active, Role role, Pageable pageable);

    /**
     * Busca usuarios por email o nombre con paginación
     *
     * @param email Texto a buscar en email
     * @param fullName Texto a buscar en nombre
     * @param pageable Configuración de paginación
     * @return Page<User> - Página con usuarios que coincidan
     */
    Page<User> findByEmailContainingIgnoreCaseOrFullNameContainingIgnoreCase(
            String email, String fullName, Pageable pageable);

    /**
     * Busca usuarios activos por email o nombre con paginación
     *
     * @param active Estado de activación
     * @param email Texto a buscar en email
     * @param fullName Texto a buscar en nombre
     * @param pageable Configuración de paginación
     * @return Page<User> - Página con usuarios activos que coincidan
     */
    @Query("{ 'active': ?0, $or: [ { 'email': { $regex: ?1, $options: 'i' } }, { 'fullName': { $regex: ?2, $options: 'i' } } ] }")
    Page<User> findByActiveAndEmailOrFullNameContaining(
            boolean active, String email, String fullName, Pageable pageable);

    /**
     * Busca usuarios por rol, email o nombre con paginación
     *
     * @param role Rol a filtrar
     * @param email Texto a buscar en email
     * @param fullName Texto a buscar en nombre
     * @param pageable Configuración de paginación
     * @return Page<User> - Página con usuarios que coincidan
     */
    @Query("{ 'role': ?0, $or: [ { 'email': { $regex: ?1, $options: 'i' } }, { 'fullName': { $regex: ?2, $options: 'i' } } ] }")
    Page<User> findByRoleAndEmailOrFullNameContaining(
            Role role, String email, String fullName, Pageable pageable);

    /**
     * Busca usuarios activos por rol, email o nombre con paginación
     *
     * @param active Estado de activación
     * @param role Rol a filtrar
     * @param email Texto a buscar en email
     * @param fullName Texto a buscar en nombre
     * @param pageable Configuración de paginación
     * @return Page<User> - Página con usuarios activos que coincidan
     */
    @Query("{ 'active': ?0, 'role': ?1, $or: [ { 'email': { $regex: ?2, $options: 'i' } }, { 'fullName': { $regex: ?3, $options: 'i' } } ] }")
    Page<User> findByActiveAndRoleAndEmailOrFullNameContaining(
            boolean active, Role role, String email, String fullName, Pageable pageable);
}
