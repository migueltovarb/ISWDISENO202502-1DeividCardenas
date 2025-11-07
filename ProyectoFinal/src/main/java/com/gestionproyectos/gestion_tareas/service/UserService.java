package com.gestionproyectos.gestion_tareas.service;

import com.gestionproyectos.gestion_tareas.dto.UserRegistrationDto;
import com.gestionproyectos.gestion_tareas.enums.Role;
import com.gestionproyectos.gestion_tareas.model.User;
import com.gestionproyectos.gestion_tareas.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * UserService - Servicio de gestión de usuarios
 *
 * RESPONSABILIDAD:
 * Contiene toda la lógica de negocio relacionada con usuarios:
 * - Registro de nuevos usuarios
 * - Validaciones de reglas de negocio
 * - Consultas de usuarios
 * - Actualización de información
 *
 * PATRÓN SERVICE LAYER:
 * - Separa la lógica de negocio del controlador
 * - El controlador maneja HTTP, el servicio maneja lógica
 * - Facilita testing (puedes probar la lógica sin HTTP)
 * - Permite reutilizar lógica en múltiples controladores
 *
 * @Transactional:
 * Marca los métodos como transaccionales.
 * Si algo falla, se hace rollback de todos los cambios en la BD.
 * Aunque MongoDB no tiene transacciones multi-documento por defecto,
 * es buena práctica añadirlo para consistencia.
 *
 * @author [Tu Nombre]
 * @version 1.0
 * @since 2025-11-03
 */
@Service  // Marca esta clase como servicio de Spring
@RequiredArgsConstructor  // Lombok: constructor con campos final
@Slf4j  // Lombok: logger automático
public class UserService {

    /**
     * Repositorio para acceso a datos de usuarios
     * Inyectado automáticamente por Spring
     */
    private final UserRepository userRepository;

    /**
     * Encoder para encriptar contraseñas
     * Inyectado desde SecurityConfig
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Registra un nuevo usuario en el sistema
     *
     * PROCESO:
     * 1. Validar que el email no exista (email debe ser único)
     * 2. Encriptar la contraseña con BCrypt
     * 3. Crear la entidad User con los datos del DTO
     * 4. Guardar en MongoDB
     * 5. Registrar en logs
     *
     * VALIDACIONES:
     * - Email único (lanza excepción si ya existe)
     * - Formato de email (validado en DTO)
     * - Contraseña mínima (validado en DTO)
     * - Rol válido (validado en DTO)
     *
     * CACHÉ:
     * Invalida múltiples cachés porque afecta:
     * - Lista de todos los usuarios
     * - Lista de usuarios por rol (si es LIDER o COLABORADOR)
     *
     * @param dto Datos del usuario a registrar
     * @throws IllegalArgumentException si el email ya está registrado
     */
    @Transactional  // Si falla algo, se hace rollback
    @Caching(evict = {
        @CacheEvict(value = "usersByRole", allEntries = true),
        @CacheEvict(value = "availableLideres", allEntries = true),
        @CacheEvict(value = "availableColaboradores", allEntries = true)
    })
    public void registerUser(UserRegistrationDto dto) {
        log.info("Intentando registrar nuevo usuario: {}", dto.email());

        // 1. VALIDAR QUE EL EMAIL NO EXISTA
        // Esta es una validación de REGLA DE NEGOCIO
        // No se puede hacer con anotaciones de validación porque requiere acceso a BD
        if (userRepository.existsByEmail(dto.email())) {
            log.warn("Intento de registro con email duplicado: {}", dto.email());
            throw new IllegalArgumentException(
                "El email " + dto.email() + " ya está registrado en el sistema"
            );
        }

        // 2. ENCRIPTAR LA CONTRASEÑA
        // IMPORTANTE: NUNCA guardar contraseñas en texto plano
        // BCrypt genera un hash unidireccional + salt
        String hashedPassword = passwordEncoder.encode(dto.password());
        log.debug("Contraseña encriptada correctamente");

        // 3. CREAR LA ENTIDAD USER
        // Usamos el patrón Builder para claridad
        User newUser = User.builder()
                .fullName(dto.fullName())
                .email(dto.email())  // Ya viene en minúsculas desde el DTO
                .password(hashedPassword)  // Contraseña hasheada, NO la original
                .role(dto.role())
                .active(true)  // Usuario activo por defecto
                .profileImage(null)  // Sin imagen de perfil inicialmente
                .build();

        // 4. GUARDAR EN MONGODB
        User savedUser = userRepository.save(newUser);

        // 5. LOG DE ÉXITO
        log.info("Usuario registrado exitosamente: {} con rol {}",
                savedUser.getEmail(),
                savedUser.getRole().getDisplayName());
    }

    /**
     * Obtiene todos los usuarios con rol LIDER
     *
     * PROPÓSITO:
     * Se usa en el formulario de creación de proyectos para seleccionar
     * el líder del proyecto en un <select>.
     *
     * FILTROS:
     * - Solo usuarios con rol LIDER
     * - Solo usuarios activos (no desactivados)
     *
     * CACHÉ:
     * Resultado cacheado por 10 minutos. Se invalida cuando:
     * - Se crea un nuevo líder
     * - Se cambia el rol de un usuario a/desde LIDER
     * - Se activa/desactiva un líder
     *
     * @return List<User> - Lista de líderes activos
     */
    @Cacheable(value = "availableLideres")
    public List<User> getAvailableLideres() {
        log.debug("Obteniendo lista de líderes disponibles");
        return userRepository.findByActiveAndRole(true, Role.LIDER);
    }

    /**
     * Obtiene todos los usuarios con rol COLABORADOR
     *
     * PROPÓSITO:
     * Se usa en el formulario de creación de tareas para asignar
     * un responsable en un <select>.
     *
     * FILTROS:
     * - Solo usuarios con rol COLABORADOR
     * - Solo usuarios activos
     *
     * CACHÉ:
     * Resultado cacheado por 10 minutos.
     *
     * @return List<User> - Lista de colaboradores activos
     */
    @Cacheable(value = "availableColaboradores")
    public List<User> getAvailableColaboradores() {
        log.debug("Obteniendo lista de colaboradores disponibles");
        return userRepository.findByActiveAndRole(true, Role.COLABORADOR);
    }

    /**
     * Obtiene colaboradores en formato DTO simplificado
     *
     * PROPÓSITO:
     * Para poblar selects en formularios, retornando solo los datos necesarios.
     * Más eficiente que retornar entidades User completas.
     *
     * @return List<UserSimpleDto> - Colaboradores activos en formato simple
     */
    public List<com.gestionproyectos.gestion_tareas.dto.UserSimpleDto> findCollaborators() {
        log.debug("Obteniendo colaboradores en formato DTO");
        return getAvailableColaboradores().stream()
                .map(com.gestionproyectos.gestion_tareas.dto.UserSimpleDto::fromUser)
                .toList();
    }

    /**
     * Obtiene todos los colaboradores de un proyecto específico
     *
     * PROPÓSITO:
     * Usado para mostrar el equipo de un proyecto y para asignar tareas
     * solo a miembros del proyecto.
     *
     * @param projectId ID del proyecto
     * @return List<User> - Colaboradores del proyecto
     */
    public List<User> getCollaboratorsByProjectId(String projectId) {
        log.debug("Obteniendo colaboradores del proyecto: {}", projectId);
        return userRepository.findByCollaboratorProjectIdsContaining(projectId);
    }

    /**
     * Obtiene un usuario por su ID
     *
     * CACHÉ:
     * Este método es uno de los más consultados en la aplicación.
     * Se cachea el resultado usando el userId como key.
     * Reduce consultas a MongoDB significativamente.
     *
     * @param userId ID del usuario
     * @return User - Usuario encontrado
     * @throws IllegalArgumentException si el usuario no existe
     */
    @Cacheable(value = "users", key = "#userId")
    public User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Usuario no encontrado con ID: " + userId
                ));
    }

    /**
     * Obtiene un usuario por su email
     *
     * @param email Email del usuario
     * @return User - Usuario encontrado
     * @throws IllegalArgumentException si el usuario no existe
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Usuario no encontrado con email: " + email
                ));
    }

    /**
     * Obtiene todos los usuarios activos
     *
     * PROPÓSITO:
     * Para el panel de administración, mostrar todos los usuarios del sistema.
     *
     * @return List<User> - Todos los usuarios activos
     */
    public List<User> getAllActiveUsers() {
        log.debug("Obteniendo todos los usuarios activos");
        return userRepository.findByActive(true);
    }

    /**
     * Desactiva un usuario (soft delete)
     *
     * IMPORTANTE:
     * No eliminamos físicamente el usuario de la BD.
     * Solo lo marcamos como inactivo (active = false).
     *
     * VENTAJAS DEL SOFT DELETE:
     * - Mantiene la integridad referencial (proyectos, tareas)
     * - Permite auditoría (saber quién hizo qué)
     * - Posibilidad de reactivar la cuenta
     * - Cumple con regulaciones de retención de datos
     *
     * CACHÉ:
     * Invalida el caché del usuario y las listas que lo contienen.
     *
     * @param userId ID del usuario a desactivar
     * @throws IllegalArgumentException si el usuario no existe
     */
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "users", key = "#userId"),
        @CacheEvict(value = "availableLideres", allEntries = true),
        @CacheEvict(value = "availableColaboradores", allEntries = true)
    })
    public void deactivateUser(String userId) {
        log.info("Desactivando usuario: {}", userId);

        User user = getUserById(userId);

        // Verificar que no sea el último admin
        if (user.isAdmin()) {
            long adminCount = userRepository.countByRole(Role.ADMIN);
            if (adminCount <= 1) {
                throw new IllegalArgumentException(
                    "No se puede desactivar el único administrador del sistema"
                );
            }
        }

        user.deactivate();
        userRepository.save(user);

        log.info("Usuario desactivado exitosamente: {}", user.getEmail());
    }

    /**
     * Reactiva un usuario previamente desactivado
     *
     * CACHÉ:
     * Invalida el caché del usuario y las listas de usuarios disponibles.
     *
     * @param userId ID del usuario a reactivar
     * @throws IllegalArgumentException si el usuario no existe
     */
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "users", key = "#userId"),
        @CacheEvict(value = "availableLideres", allEntries = true),
        @CacheEvict(value = "availableColaboradores", allEntries = true)
    })
    public void activateUser(String userId) {
        log.info("Reactivando usuario: {}", userId);

        User user = getUserById(userId);
        user.activate();
        userRepository.save(user);

        log.info("Usuario reactivado exitosamente: {}", user.getEmail());
    }

    /**
     * Elimina permanentemente un usuario del sistema
     *
     * ATENCIÓN: Esta es una eliminación PERMANENTE (hard delete)
     * El usuario y todos sus datos asociados serán eliminados de la base de datos.
     *
     * VALIDACIONES:
     * - No permitir eliminar el último administrador del sistema
     * - Verificar que el usuario no tenga proyectos activos como líder
     * - Se eliminan todos los datos del usuario permanentemente
     *
     * RECOMENDACIÓN:
     * - Usar desactivateUser() en lugar de deleteUser() para mantener el historial
     * - Solo usar deleteUser() cuando sea estrictamente necesario
     *
     * @param userId ID del usuario a eliminar
     * @throws IllegalArgumentException si el usuario no existe o no se puede eliminar
     */
    @Transactional
    public void deleteUser(String userId) {
        log.warn("Intentando eliminar permanentemente usuario: {}", userId);

        User user = getUserById(userId);

        // Verificar que no sea el último admin
        if (user.isAdmin()) {
            long adminCount = userRepository.countByRole(Role.ADMIN);
            if (adminCount <= 1) {
                throw new IllegalArgumentException(
                    "No se puede eliminar el único administrador del sistema"
                );
            }
        }

        // Verificar que no tenga proyectos activos como líder
        if (user.isLider() && user.getLeadProjectIds() != null && !user.getLeadProjectIds().isEmpty()) {
            throw new IllegalArgumentException(
                "No se puede eliminar un usuario que es líder de " + user.getLeadProjectIds().size() +
                " proyecto(s). Por favor, reasigne los proyectos primero."
            );
        }

        // Eliminar permanentemente
        userRepository.delete(user);

        log.warn("Usuario eliminado permanentemente: {} ({})", user.getEmail(), userId);
    }

    /**
     * Verifica si un usuario existe por email
     *
     * @param email Email a verificar
     * @return boolean - true si existe, false si no
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Cuenta el número de usuarios por rol
     *
     * PROPÓSITO:
     * Para dashboards administrativos y estadísticas.
     *
     * @param role Rol a contar
     * @return long - Número de usuarios con ese rol
     */
    public long countByRole(Role role) {
        return userRepository.countByRole(role);
    }

    /**
     * Actualiza la información de un usuario
     *
     * REGLAS DE NEGOCIO:
     * - NO se actualiza el email (es el username y debe ser inmutable)
     * - Validar que el rol sea permitido
     * - Si cambia de LIDER a otro rol, verificar que no tenga proyectos activos
     *
     * CACHÉ:
     * Invalida el caché del usuario específico y las listas de roles.
     *
     * @param dto DTO con los datos a actualizar
     * @throws IllegalArgumentException si hay validaciones fallidas
     */
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "users", key = "#dto.id()"),
        @CacheEvict(value = "availableLideres", allEntries = true),
        @CacheEvict(value = "availableColaboradores", allEntries = true)
    })
    public void updateUser(com.gestionproyectos.gestion_tareas.dto.UserUpdateDto dto) {
        log.info("Actualizando usuario: {}", dto.id());

        User user = getUserById(dto.id());
        Role oldRole = user.getRole();

        // Validar cambio de rol si aplica
        if (!oldRole.equals(dto.role())) {
            // Si era LIDER y tiene proyectos activos, no permitir el cambio
            if (user.isLider() && !user.getLeadProjectIds().isEmpty()) {
                throw new IllegalArgumentException(
                    "No se puede cambiar el rol de un líder con proyectos activos. " +
                    "Primero reasigne los proyectos a otro líder."
                );
            }

            log.info("Cambiando rol de {} a {} para usuario {}",
                    oldRole.getDisplayName(), dto.role().getDisplayName(), user.getEmail());
        }

        // Actualizar campos
        user.setFullName(dto.fullName());
        user.setRole(dto.role());

        userRepository.save(user);
        log.info("Usuario actualizado exitosamente: {}", user.getEmail());
    }

    /**
     * Cambia la contraseña de un usuario (solo para admin)
     *
     * PROPÓSITO:
     * Permite al administrador resetear la contraseña de cualquier usuario.
     * NO requiere la contraseña actual (es un reset administrativo).
     *
     * USO TÍPICO:
     * - Usuario olvida su contraseña
     * - Reset por política de seguridad
     * - Primera asignación de contraseña
     *
     * @param dto DTO con userId y nueva contraseña
     * @throws IllegalArgumentException si el usuario no existe
     */
    @Transactional
    public void adminChangePassword(com.gestionproyectos.gestion_tareas.dto.AdminPasswordChangeDto dto) {
        log.info("Admin cambiando contraseña para usuario: {}", dto.userId());

        User user = getUserById(dto.userId());
        String hashedPassword = passwordEncoder.encode(dto.newPassword());

        user.setPassword(hashedPassword);
        userRepository.save(user);

        log.info("Contraseña actualizada por admin para usuario: {}", user.getEmail());
    }

    /**
     * Busca usuarios con filtros opcionales
     *
     * PROPÓSITO:
     * Búsqueda avanzada para el panel de administración.
     * Permite filtrar por rol, estado, y búsqueda de texto.
     *
     * @param searchText Texto a buscar en nombre o email (puede ser null)
     * @param role Rol a filtrar (puede ser null)
     * @param activeOnly Si true, solo muestra usuarios activos
     * @return List de UserListDto con los resultados
     */
    public List<com.gestionproyectos.gestion_tareas.dto.UserListDto> searchUsers(
            String searchText,
            Role role,
            Boolean activeOnly
    ) {
        log.debug("Buscando usuarios - texto: {}, rol: {}, activos: {}",
                searchText, role, activeOnly);

        List<User> users;

        // Caso 1: Búsqueda por texto
        if (searchText != null && !searchText.trim().isEmpty()) {
            users = userRepository.findByEmailContainingIgnoreCaseOrFullNameContainingIgnoreCase(
                    searchText.trim(), searchText.trim());

            // Filtrar por rol si se especificó
            if (role != null) {
                users = users.stream()
                        .filter(u -> u.getRole().equals(role))
                        .toList();
            }

            // Filtrar por estado si se especificó
            if (activeOnly != null && activeOnly) {
                users = users.stream()
                        .filter(User::isActive)
                        .toList();
            }
        }
        // Caso 2: Filtro por rol y estado
        else if (role != null) {
            if (activeOnly != null && activeOnly) {
                users = userRepository.findByActiveAndRole(true, role);
            } else {
                users = userRepository.findByRole(role);
            }
        }
        // Caso 3: Solo filtro por estado activo
        else if (activeOnly != null && activeOnly) {
            users = userRepository.findByActive(true);
        }
        // Caso 4: Todos los usuarios
        else {
            users = userRepository.findAllByOrderByFullNameAsc();
        }

        // Convertir a DTOs
        return users.stream()
                .map(com.gestionproyectos.gestion_tareas.dto.UserListDto::fromUser)
                .toList();
    }

    /**
     * Busca usuarios con filtros opcionales y paginación
     *
     * PROPÓSITO:
     * Búsqueda avanzada paginada para el panel de administración.
     * Permite filtrar por rol, estado, y búsqueda de texto con paginación.
     * Optimizado para MongoDB con queries nativas.
     *
     * @param searchText Texto a buscar en nombre o email (puede ser null)
     * @param role Rol a filtrar (puede ser null)
     * @param activeOnly Si true, solo muestra usuarios activos
     * @param page Número de página (0-indexed)
     * @param size Tamaño de página
     * @param sortBy Campo por el que ordenar
     * @param sortDirection Dirección de ordenamiento (ASC o DESC)
     * @return Page de UserListDto con los resultados paginados
     */
    public Page<com.gestionproyectos.gestion_tareas.dto.UserListDto> searchUsersPaginated(
            String searchText,
            Role role,
            Boolean activeOnly,
            int page,
            int size,
            String sortBy,
            String sortDirection
    ) {
        log.debug("Buscando usuarios paginado - texto: {}, rol: {}, activos: {}, página: {}, tamaño: {}",
                searchText, role, activeOnly, page, size);

        // Configurar ordenamiento
        Sort sort = sortDirection.equalsIgnoreCase("DESC")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> userPage;
        boolean hasSearchText = searchText != null && !searchText.trim().isEmpty();
        String searchTerm = hasSearchText ? searchText.trim() : "";

        // Determinar qué query usar basado en los filtros
        if (hasSearchText) {
            // Búsqueda por texto con filtros adicionales
            if (role != null && activeOnly != null && activeOnly) {
                // Caso 1: Texto + Rol + Activo
                userPage = userRepository.findByActiveAndRoleAndEmailOrFullNameContaining(
                        true, role, searchTerm, searchTerm, pageable);
            } else if (role != null) {
                // Caso 2: Texto + Rol
                userPage = userRepository.findByRoleAndEmailOrFullNameContaining(
                        role, searchTerm, searchTerm, pageable);
            } else if (activeOnly != null && activeOnly) {
                // Caso 3: Texto + Activo
                userPage = userRepository.findByActiveAndEmailOrFullNameContaining(
                        true, searchTerm, searchTerm, pageable);
            } else {
                // Caso 4: Solo texto
                userPage = userRepository.findByEmailContainingIgnoreCaseOrFullNameContainingIgnoreCase(
                        searchTerm, searchTerm, pageable);
            }
        } else {
            // Sin búsqueda por texto
            if (role != null && activeOnly != null && activeOnly) {
                // Caso 5: Rol + Activo
                userPage = userRepository.findByActiveAndRole(true, role, pageable);
            } else if (role != null) {
                // Caso 6: Solo Rol
                userPage = userRepository.findByRole(role, pageable);
            } else if (activeOnly != null && activeOnly) {
                // Caso 7: Solo Activo
                userPage = userRepository.findByActive(true, pageable);
            } else {
                // Caso 8: Todos los usuarios
                userPage = userRepository.findAllBy(pageable);
            }
        }

        // Convertir a DTOs
        return userPage.map(com.gestionproyectos.gestion_tareas.dto.UserListDto::fromUser);
    }

    /**
     * Obtiene todos los usuarios en formato de lista
     *
     * PROPÓSITO:
     * Para el listado principal del panel de administración.
     * Incluye información completa de cada usuario.
     *
     * @return List de UserListDto
     */
    public List<com.gestionproyectos.gestion_tareas.dto.UserListDto> getAllUsersForList() {
        log.debug("Obteniendo todos los usuarios para lista");
        return userRepository.findAllByOrderByFullNameAsc().stream()
                .map(com.gestionproyectos.gestion_tareas.dto.UserListDto::fromUser)
                .toList();
    }

    /**
     * Obtiene usuarios activos en formato de lista
     *
     * @return List de UserListDto con solo usuarios activos
     */
    public List<com.gestionproyectos.gestion_tareas.dto.UserListDto> getActiveUsersForList() {
        log.debug("Obteniendo usuarios activos para lista");
        return userRepository.findByActive(true).stream()
                .map(com.gestionproyectos.gestion_tareas.dto.UserListDto::fromUser)
                .toList();
    }

    /**
     * NOTAS PARA TU PROFESOR:
     *
     * 1. CAPA DE SERVICIO:
     *    - Contiene TODA la lógica de negocio
     *    - El controlador delega al servicio
     *    - El servicio NO conoce HTTP ni Thymeleaf
     *    - Permite reutilizar lógica en API REST, CLI, etc.
     *
     * 2. VALIDACIONES EN DOS CAPAS:
     *    - DTO: Validaciones de formato (email válido, tamaño mínimo)
     *    - Servicio: Validaciones de negocio (email único, último admin)
     *
     * 3. TRANSACCIONES:
     *    - @Transactional garantiza atomicidad
     *    - Si algo falla, se deshacen todos los cambios
     *    - En MongoDB requiere replica set (no disponible en versión gratuita)
     *    - Pero es buena práctica añadirlo para consistencia
     *
     * 4. LOGGING:
     *    - log.info(): Eventos importantes (registro, desactivación)
     *    - log.debug(): Información de depuración
     *    - log.warn(): Advertencias (email duplicado)
     *    - log.error(): Errores graves
     *
     * 5. MANEJO DE ERRORES:
     *    - Lanzar excepciones descriptivas
     *    - El controlador las captura y muestra al usuario
     *    - No usar System.out.println(), usar logger
     *
     * 6. TESTING:
     *    ```
     *    @Test
     *    void registerUser_EmailExists_ThrowsException() {
     *        // Arrange
     *        when(userRepository.existsByEmail("test@example.com"))
     *            .thenReturn(true);
     *
     *        // Act & Assert
     *        assertThrows(IllegalArgumentException.class, () ->
     *            userService.registerUser(dto)
     *        );
     *    }
     *    ```
     */
}
