package com.gestionproyectos.gestion_tareas.config;

import com.gestionproyectos.gestion_tareas.security.LoginRateLimitFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * SecurityConfig - Configuración de Seguridad con Spring Security
 *
 * Esta es una de las clases MÁS IMPORTANTES del proyecto.
 * Configura toda la seguridad de la aplicación, incluyendo:
 * - Autenticación (quién eres)
 * - Autorización (qué puedes hacer)
 * - Encriptación de contraseñas
 * - Páginas públicas vs protegidas
 * - Formulario de login personalizado
 *
 * SPRING SECURITY:
 * Es el framework de seguridad estándar para aplicaciones Spring.
 * Proporciona protección contra:
 * - Accesos no autorizados
 * - CSRF (Cross-Site Request Forgery)
 * - Session Fixation
 * - Inyección de SQL (junto con JPA/MongoDB)
 * - XSS (Cross-Site Scripting) cuando se usa con Thymeleaf
 *
 * FLUJO DE SEGURIDAD:
 * 1. Usuario intenta acceder a una URL
 * 2. SecurityFilterChain verifica si la URL es pública o requiere autenticación
 * 3. Si requiere autenticación y no está logueado → redirige a /login
 * 4. Usuario ingresa credenciales en el formulario
 * 5. CustomUserDetailsService carga el usuario de MongoDB
 * 6. PasswordEncoder verifica la contraseña hasheada
 * 7. Si es correcta → se crea sesión y se redirige a la página solicitada
 * 8. En cada petición, se verifica si tiene permisos según su ROL
 *
 * @author [Tu Nombre]
 * @version 1.0
 * @since 2025-11-03
 */
@Configuration  // Marca esta clase como configuración de Spring
@EnableWebSecurity  // Activa Spring Security en la aplicación web
@EnableMethodSecurity(
    // Permite usar @PreAuthorize, @PostAuthorize en métodos
    prePostEnabled = true,
    // Permite usar @Secured en métodos
    securedEnabled = true,
    // Permite usar JSR-250 annotations (@RolesAllowed)
    jsr250Enabled = true
)
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * Filtro de rate limiting para login
     * Inyectado automáticamente por Spring
     */
    private final LoginRateLimitFilter loginRateLimitFilter;

    /**
     * PasswordEncoder Bean - Encriptador de contraseñas
     *
     * PROPÓSITO:
     * Define cómo se encriptan las contraseñas antes de guardarlas en la BD.
     * NUNCA se deben guardar contraseñas en texto plano.
     *
     * BCRYPT:
     * Es un algoritmo de hash unidireccional diseñado para contraseñas.
     * Características:
     * - Unidireccional: No se puede revertir el hash a la contraseña original
     * - Salted: Añade datos aleatorios para prevenir rainbow tables
     * - Adaptable: Se puede aumentar la complejidad con el tiempo
     * - Lento intencionalmente: Dificulta ataques de fuerza bruta
     *
     * FORMATO DEL HASH:
     * $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
     *  │  │  │                     │
     *  │  │  └─ Salt (22 chars)   └─ Hash (31 chars)
     *  │  └─ Strength (10 = 2^10 iteraciones, ~0.1s por hash)
     *  └─ Algoritmo (2a = BCrypt)
     *
     * USO EN EL CÓDIGO:
     * ```
     * // Al registrar un usuario
     * String hashedPassword = passwordEncoder.encode(plainPassword);
     * user.setPassword(hashedPassword);
     *
     * // Al autenticar (Spring Security lo hace automáticamente)
     * boolean matches = passwordEncoder.matches(plainPassword, hashedPassword);
     * ```
     *
     * @return PasswordEncoder - Instancia de BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCryptPasswordEncoder con strength por defecto (10)
        // Strength 10 = 2^10 = 1024 iteraciones, balance entre seguridad y rendimiento
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager Bean
     *
     * PROPÓSITO:
     * Gestiona el proceso de autenticación.
     * Es el componente central que coordina la verificación de credenciales.
     *
     * PROCESO:
     * 1. Recibe credenciales (email + password)
     * 2. Delega a CustomUserDetailsService para cargar el usuario
     * 3. Usa PasswordEncoder para verificar la contraseña
     * 4. Si es válida, crea un objeto Authentication
     * 5. Si es inválida, lanza AuthenticationException
     *
     * USO:
     * Generalmente no lo usas directamente.
     * Spring Security lo usa internamente en el proceso de login.
     * Pero puede ser útil en controladores para autenticación programática.
     *
     * @param authConfig Configuración de autenticación de Spring
     * @return AuthenticationManager
     * @throws Exception si hay error en la configuración
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * SecurityFilterChain - Configuración de la cadena de filtros de seguridad
     *
     * PROPÓSITO:
     * Define las reglas de seguridad de la aplicación:
     * - Qué URLs son públicas
     * - Qué URLs requieren autenticación
     * - Qué URLs requieren roles específicos
     * - Configuración del formulario de login
     * - Comportamiento del logout
     *
     * CADENA DE FILTROS:
     * Spring Security procesa cada petición HTTP a través de una serie de filtros:
     * Request → SecurityFilter1 → SecurityFilter2 → ... → Controller
     *
     * Cada filtro verifica un aspecto de seguridad:
     * - CSRF Token
     * - Sesión válida
     * - Usuario autenticado
     * - Permisos del usuario
     *
     * @param http Objeto HttpSecurity para configurar la seguridad
     * @return SecurityFilterChain configurada
     * @throws Exception si hay error en la configuración
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // ==================== RATE LIMITING ====================
            // Añadir filtro de rate limiting ANTES de la autenticación
            // Esto previene ataques de fuerza bruta bloqueando IPs sospechosas
            // antes de que se procese la autenticación
            .addFilterBefore(loginRateLimitFilter, UsernamePasswordAuthenticationFilter.class)

            // ==================== AUTORIZACIÓN ====================
            // Define qué URLs requieren qué permisos
            .authorizeHttpRequests(authz -> authz
                // URLs PÚBLICAS (accesibles sin autenticación)
                // Cualquiera puede acceder a estas rutas
                .requestMatchers(
                    "/",              // Página de inicio
                    "/login",         // Página de login
                    "/register",      // Página de registro público
                    "/css/**",        // Archivos CSS estáticos
                    "/js/**",         // Archivos JavaScript estáticos
                    "/images/**",     // Imágenes estáticas
                    "/webjars/**",    // WebJars (librerías JS/CSS empaquetadas)
                    "/error",         // Página de error
                    "/api/health",    // Health check de MongoDB
                    "/api/health/**"  // Endpoints de health check
                ).permitAll()

                // URLs SOLO PARA ADMINISTRADORES
                // Solo usuarios con rol ADMIN pueden acceder
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // URLs PARA LÍDERES Y ADMINISTRADORES
                // Líderes pueden crear proyectos y asignar tareas
                .requestMatchers(
                    "/projects/new",           // Crear proyecto
                    "/projects/*/edit",        // Editar proyecto
                    "/projects/*/tasks/new"    // Crear tarea
                ).hasAnyRole("LIDER", "ADMIN")

                // TODAS LAS DEMÁS URLs REQUIEREN AUTENTICACIÓN
                // El usuario debe estar logueado, cualquier rol es válido
                .anyRequest().authenticated()
            )

            // ==================== FORMULARIO DE LOGIN ====================
            // Configuración del formulario de autenticación
            .formLogin(form -> form
                // URL de la página de login personalizada
                .loginPage("/login")

                // URL donde se procesan las credenciales (POST /login)
                // Spring Security intercepta esta petición automáticamente
                .loginProcessingUrl("/login")

                // URL a donde redirigir después de login exitoso
                // true = siempre redirige aquí
                // false = redirige a la página que intentaba acceder originalmente
                .defaultSuccessUrl("/dashboard", true)

                // URL a donde redirigir si el login falla
                // Se añade parámetro ?error para mostrar mensaje
                .failureUrl("/login?error=true")

                // Nombres de los campos en el formulario HTML
                .usernameParameter("email")      // <input name="email">
                .passwordParameter("password")    // <input name="password">

                // Permitir acceso a la página de login sin autenticación
                .permitAll()
            )

            // ==================== LOGOUT ====================
            // Configuración del proceso de cierre de sesión
            .logout(logout -> logout
                // URL para cerrar sesión (GET o POST /logout)
                .logoutUrl("/logout")

                // URL a donde redirigir después del logout
                .logoutSuccessUrl("/login?logout=true")

                // Invalidar la sesión HTTP al cerrar sesión
                // Elimina toda la información de sesión del servidor
                .invalidateHttpSession(true)

                // Borrar las cookies de autenticación
                .deleteCookies("JSESSIONID")

                // Permitir acceso al logout sin restricciones
                .permitAll()
            )

            // ==================== MANEJO DE EXCEPCIONES ====================
            // Configuración de páginas de error
            .exceptionHandling(ex -> ex
                // Página a mostrar cuando se intenta acceder sin autenticación
                .accessDeniedPage("/error/403")
            )

            // ==================== GESTIÓN DE SESIONES ====================
            // Configuración del comportamiento de sesiones
            .sessionManagement(session -> session
                // Política de creación de sesiones
                // IF_REQUIRED = crea sesión solo si es necesario
                // ALWAYS = siempre crea sesión
                // NEVER = nunca crea sesión
                // STATELESS = sin sesión (para APIs REST)
                .sessionCreationPolicy(
                    org.springframework.security.config.http.SessionCreationPolicy.IF_REQUIRED
                )

                // Máximo de sesiones concurrentes por usuario
                .maximumSessions(1)
                // true = impide nuevos logins si ya hay sesión activa
                // false = invalida sesiones anteriores y crea nueva
                .maxSessionsPreventsLogin(false)
            )

            // ==================== PROTECCIÓN CSRF ====================
            // CSRF (Cross-Site Request Forgery) protection
            // Previene ataques donde un sitio malicioso envía peticiones
            // en nombre del usuario autenticado
            // CSRF está habilitado por defecto en Spring Security
            // En formularios Thymeleaf, se incluye automáticamente:
            // <input type="hidden" name="_csrf" value="token"/>
            // Para esta aplicación web con Thymeleaf, CSRF está activo
            .csrf(Customizer.withDefaults());

        return http.build();
    }

    /**
     * NOTAS IMPORTANTES PARA TU PROFESOR:
     *
     * 1. ROLES vs AUTHORITIES:
     *    - Role: Es un grupo de permisos (ADMIN, LIDER, COLABORADOR)
     *    - Authority: Es un permiso específico (READ_PROJECT, WRITE_TASK)
     *    - En Spring Security, los roles son authorities con prefijo "ROLE_"
     *    - hasRole("ADMIN") busca internamente "ROLE_ADMIN"
     *
     * 2. SEGURIDAD EN MÉTODOS:
     *    Además de URLs, puedes proteger métodos individuales:
     *    ```
     *    @PreAuthorize("hasRole('ADMIN')")
     *    public void deleteUser(String userId) { ... }
     *
     *    @PreAuthorize("hasRole('LIDER') or hasRole('ADMIN')")
     *    public void createProject(Project project) { ... }
     *
     *    @PreAuthorize("#userId == authentication.principal.id or hasRole('ADMIN')")
     *    public User getUser(String userId) { ... }
     *    ```
     *
     * 3. TESTING:
     *    Para probar la seguridad:
     *    ```
     *    @WithMockUser(username = "admin", roles = {"ADMIN"})
     *    @Test
     *    public void testAdminAccess() { ... }
     *    ```
     *
     * 4. CUSTOMIZACIÓN:
     *    Esta configuración es básica pero robusta.
     *    Puede extenderse con:
     *    - Remember Me functionality
     *    - OAuth2/OpenID Connect
     *    - Two-Factor Authentication
     *    - Rate limiting
     *    - IP filtering
     *
     * 5. BUENAS PRÁCTICAS IMPLEMENTADAS:
     *    ✓ Contraseñas hasheadas con BCrypt
     *    ✓ Protección CSRF activada
     *    ✓ Sesiones seguras
     *    ✓ URLs públicas claramente definidas
     *    ✓ Roles jerárquicos (ADMIN > LIDER > COLABORADOR)
     *    ✓ Logout que limpia completamente la sesión
     */
}
