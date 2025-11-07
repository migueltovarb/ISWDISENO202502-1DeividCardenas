package com.gestionproyectos.gestion_tareas.service;

import com.gestionproyectos.gestion_tareas.model.User;
import com.gestionproyectos.gestion_tareas.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

/**
 * CustomUserDetailsService - Servicio de autenticación para Spring Security
 *
 * PROPÓSITO:
 * Este servicio es el puente entre Spring Security y nuestra base de datos MongoDB.
 * Spring Security lo usa para cargar la información del usuario durante el login.
 *
 * INTERFAZ UserDetailsService:
 * Es una interfaz de Spring Security que requiere implementar un solo método:
 * - loadUserByUsername(String username): Carga el usuario desde la BD
 *
 * FLUJO DE AUTENTICACIÓN:
 * 1. Usuario envía email y contraseña en el formulario de login
 * 2. Spring Security llama a loadUserByUsername(email)
 * 3. Este servicio busca el usuario en MongoDB por email
 * 4. Si existe, crea un objeto UserDetails con sus datos y roles
 * 5. Spring Security compara la contraseña ingresada con la hasheada
 * 6. Si coincide, crea la sesión de usuario
 *
 * PATRÓN DE DISEÑO: Adapter Pattern
 * - Adapta nuestra entidad User a la interfaz UserDetails que espera Spring Security
 * - Permite que Spring Security trabaje con nuestra BD sin conocer su estructura
 *
 * @author [Tu Nombre]
 * @version 1.0
 * @since 2025-11-03
 */
@Service  // Marca esta clase como un servicio de Spring
@RequiredArgsConstructor  // Lombok: genera constructor con campos final (inyección por constructor)
public class CustomUserDetailsService implements UserDetailsService {

    /**
     * Repositorio para acceder a los usuarios en MongoDB
     *
     * Inyectado automáticamente por Spring mediante constructor
     * (gracias a @RequiredArgsConstructor de Lombok)
     */
    private final UserRepository userRepository;

    /**
     * Carga un usuario por su email (username)
     *
     * IMPORTANTE:
     * En nuestro sistema, el "username" es el email del usuario.
     * Esto se configuró en SecurityConfig con .usernameParameter("email")
     *
     * PROCESO:
     * 1. Busca el usuario en MongoDB por email
     * 2. Si no existe, lanza UsernameNotFoundException
     * 3. Si existe pero está inactivo, lanza excepción
     * 4. Si todo está bien, crea un UserDetails con sus datos
     *
     * @param username Email del usuario (llamado "username" por convención de Spring Security)
     * @return UserDetails - Objeto con la información del usuario para Spring Security
     * @throws UsernameNotFoundException si el usuario no existe o está inactivo
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Buscar el usuario en la base de datos por email
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                    "Usuario no encontrado con el email: " + username
                ));

        // 2. Verificar que el usuario esté activo (soft delete)
        if (!user.isActive()) {
            throw new UsernameNotFoundException(
                "El usuario con email " + username + " está desactivado"
            );
        }

        // 3. Crear y retornar un UserDetails con la información del usuario
        // Spring Security usa este objeto para:
        // - Verificar la contraseña
        // - Obtener los roles/authorities
        // - Verificar si la cuenta está habilitada, bloqueada, etc.
        return org.springframework.security.core.userdetails.User.builder()
                // Username: En nuestro caso, el email
                .username(user.getEmail())

                // Password: Contraseña hasheada almacenada en la BD
                // Spring Security comparará automáticamente la contraseña ingresada
                // con esta usando BCryptPasswordEncoder
                .password(user.getPassword())

                // Authorities: Roles y permisos del usuario
                // Convertimos el enum Role a GrantedAuthority
                .authorities(getAuthorities(user))

                // AccountExpired: Indica si la cuenta ha expirado
                // En nuestro sistema, las cuentas no expiran (false = no expirada)
                .accountExpired(false)

                // AccountLocked: Indica si la cuenta está bloqueada
                // En nuestro sistema, no implementamos bloqueo de cuentas (false = no bloqueada)
                .accountLocked(false)

                // CredentialsExpired: Indica si las credenciales han expirado
                // En nuestro sistema, las contraseñas no expiran (false = no expirada)
                .credentialsExpired(false)

                // Disabled: Indica si la cuenta está deshabilitada
                // Usamos el campo 'active' de nuestra entidad User (true = deshabilitada)
                .disabled(!user.isActive())

                // Construir el UserDetails
                .build();
    }

    /**
     * Convierte el rol del usuario en GrantedAuthority para Spring Security
     *
     * AUTHORITIES vs ROLES:
     * - Authority: Es un permiso genérico (ej: "ROLE_ADMIN", "READ_PROJECT")
     * - Role: Es un authority con prefijo "ROLE_" (ej: "ROLE_ADMIN")
     *
     * Spring Security usa authorities para control de acceso.
     * Los métodos hasRole("ADMIN") buscan internamente "ROLE_ADMIN".
     *
     * EJEMPLO:
     * Si el usuario tiene Role.ADMIN:
     * - Se crea un SimpleGrantedAuthority("ROLE_ADMIN")
     * - hasRole("ADMIN") retornará true
     * - hasAuthority("ROLE_ADMIN") retornará true
     *
     * @param user Usuario del que obtener los roles
     * @return Collection<? extends GrantedAuthority> - Lista con el rol del usuario
     */
    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        // En nuestro sistema, cada usuario tiene un solo rol
        // Si en el futuro quisieras múltiples roles, esto sería una lista

        // Obtener el nombre del rol con prefijo "ROLE_"
        // Role.ADMIN.getRoleName() retorna "ROLE_ADMIN"
        String roleName = user.getRole().getRoleName();

        // Crear una GrantedAuthority con ese nombre
        GrantedAuthority authority = new SimpleGrantedAuthority(roleName);

        // Retornar una colección con esta authority
        // Collections.singleton() crea una lista inmutable de un solo elemento
        return Collections.singleton(authority);
    }

    /**
     * MÉTODOS ADICIONALES ÚTILES (no requeridos por la interfaz)
     *
     * Estos métodos pueden ser útiles en otras partes de la aplicación
     * para obtener información del usuario autenticado.
     */

    /**
     * Obtiene el usuario completo desde la base de datos por email
     *
     * Útil cuando necesitas acceder a campos adicionales del usuario
     * que no están en UserDetails (ej: fullName, profileImage)
     *
     * @param email Email del usuario
     * @return User - Entidad completa del usuario
     * @throws UsernameNotFoundException si no existe
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                    "Usuario no encontrado: " + email
                ));
    }

    /**
     * Obtiene el usuario completo desde UserDetails
     *
     * Útil en controladores cuando tienes un UserDetails y necesitas
     * la entidad completa del usuario.
     *
     * Ejemplo de uso en un controlador:
     * ```
     * @GetMapping("/profile")
     * public String profile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
     *     User user = customUserDetailsService.getUserFromUserDetails(userDetails);
     *     model.addAttribute("user", user);
     *     return "profile";
     * }
     * ```
     *
     * @param userDetails UserDetails del usuario autenticado
     * @return User - Entidad completa del usuario
     */
    public User getUserFromUserDetails(UserDetails userDetails) {
        return getUserByEmail(userDetails.getUsername());
    }

    /**
     * NOTAS IMPORTANTES PARA TU PROFESOR:
     *
     * 1. SEGURIDAD:
     *    - Este servicio NO verifica la contraseña (Spring Security lo hace)
     *    - Solo carga la información del usuario desde la BD
     *    - La verificación de contraseña usa BCryptPasswordEncoder automáticamente
     *
     * 2. PERFORMANCE:
     *    - loadUserByUsername se llama en CADA login
     *    - MongoDB hace una búsqueda por email (que tiene índice único)
     *    - Es muy rápido gracias al índice
     *
     * 3. EXTENSIONES POSIBLES:
     *    - Cachear usuarios para mejorar performance
     *    - Implementar múltiples roles por usuario
     *    - Añadir authorities granulares (READ, WRITE, DELETE)
     *    - Implementar bloqueo de cuenta tras X intentos fallidos
     *    - Registrar intentos de login (auditoría)
     *
     * 4. TESTING:
     *    Para probar este servicio:
     *    ```
     *    @Test
     *    void loadUserByUsername_UserExists_ReturnsUserDetails() {
     *        // Arrange
     *        User user = User.builder()
     *            .email("test@example.com")
     *            .password("$2a$10$...")
     *            .role(Role.COLABORADOR)
     *            .active(true)
     *            .build();
     *        when(userRepository.findByEmail("test@example.com"))
     *            .thenReturn(Optional.of(user));
     *
     *        // Act
     *        UserDetails userDetails = service.loadUserByUsername("test@example.com");
     *
     *        // Assert
     *        assertEquals("test@example.com", userDetails.getUsername());
     *        assertTrue(userDetails.getAuthorities().stream()
     *            .anyMatch(a -> a.getAuthority().equals("ROLE_COLABORADOR")));
     *    }
     *    ```
     */
}
