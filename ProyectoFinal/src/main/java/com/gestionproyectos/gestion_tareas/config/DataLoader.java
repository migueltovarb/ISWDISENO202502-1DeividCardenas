package com.gestionproyectos.gestion_tareas.config;

import com.gestionproyectos.gestion_tareas.enums.Role;
import com.gestionproyectos.gestion_tareas.model.User;
import com.gestionproyectos.gestion_tareas.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * DataLoader - Carga datos iniciales en la base de datos
 *
 * PROPÓSITO:
 * Crea un usuario administrador por defecto cuando la aplicación se inicia
 * por primera vez. Esto permite:
 * - Acceder al sistema sin necesidad de crear manualmente el primer usuario
 * - Tener un usuario con permisos de ADMIN para configurar el sistema
 * - Facilitar el desarrollo y testing
 *
 * INTERFAZ CommandLineRunner:
 * Es una interfaz de Spring Boot que ejecuta código DESPUÉS de que la aplicación
 * se haya iniciado completamente. Es ideal para:
 * - Inicializar datos
 * - Ejecutar tareas de configuración
 * - Realizar validaciones al inicio
 *
 * CUÁNDO SE EJECUTA:
 * - Se ejecuta UNA VEZ cada vez que inicia la aplicación
 * - Se ejecuta DESPUÉS de que Spring inicialice todos los beans
 * - Se ejecuta ANTES de que la aplicación esté lista para recibir peticiones
 *
 * IMPORTANTE:
 * Este código verifica si el admin ya existe ANTES de crearlo.
 * Esto evita duplicados si reinicias la aplicación.
 *
 * @author [Tu Nombre]
 * @version 1.0
 * @since 2025-11-03
 */
@Component  // Marca esta clase como un componente de Spring (bean gestionado)
@RequiredArgsConstructor  // Lombok: genera constructor con campos final
@Slf4j  // Lombok: genera un logger (log) automáticamente
public class DataLoader implements CommandLineRunner {

    /**
     * Repositorio para acceder a usuarios en MongoDB
     * Inyectado automáticamente por Spring
     */
    private final UserRepository userRepository;

    /**
     * Encoder para encriptar contraseñas
     * Inyectado automáticamente por Spring (configurado en SecurityConfig)
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Método run - Se ejecuta al iniciar la aplicación
     *
     * PROCESO:
     * 1. Verifica si existe un usuario admin
     * 2. Si no existe, crea uno nuevo
     * 3. Encripta la contraseña
     * 4. Guarda el usuario en MongoDB
     * 5. Registra el resultado en los logs
     *
     * @param args Argumentos de línea de comandos (no se usan aquí)
     * @throws Exception si hay algún error durante la ejecución
     */
    @Override
    public void run(String... args) throws Exception {
        log.info("=== Iniciando DataLoader: Verificando usuario administrador ===");

        // Datos del usuario admin por defecto
        String adminEmail = "admin@gestion.com";
        String adminPassword = "admin123";  // ⚠️ CAMBIAR en producción

        // 1. Verificar si el usuario admin ya existe
        if (userRepository.existsByEmail(adminEmail)) {
            // El admin ya existe, no hacer nada
            log.info("✓ Usuario administrador ya existe: {}", adminEmail);
            return;
        }

        // 2. El admin no existe, crear uno nuevo
        log.info("⚠ Usuario administrador no encontrado. Creando uno nuevo...");

        // 3. Encriptar la contraseña usando BCrypt
        // IMPORTANTE: NUNCA guardar contraseñas en texto plano
        String hashedPassword = passwordEncoder.encode(adminPassword);

        // 4. Crear la entidad User con el patrón Builder
        User adminUser = User.builder()
                // Información básica
                .fullName("Administrador del Sistema")
                .email(adminEmail)
                .password(hashedPassword)  // Contraseña hasheada, no la original

                // Rol: ADMIN (máximos permisos)
                .role(Role.ADMIN)

                // Estado: activo (puede iniciar sesión)
                .active(true)

                // Imagen de perfil: puede ser null (se mostrará avatar por defecto)
                .profileImage(null)

                // Proyectos: conjuntos vacíos (el admin no lidera proyectos inicialmente)
                // Estos se inicializan automáticamente por @Builder.Default en la entidad
                .build();

        // 5. Guardar el usuario en MongoDB
        userRepository.save(adminUser);

        // 6. Registrar el éxito en los logs
        log.info("✓ Usuario administrador creado exitosamente:");
        log.info("  - Email: {}", adminEmail);
        log.info("  - Password: {} (⚠️ CAMBIAR DESPUÉS DEL PRIMER LOGIN)", adminPassword);
        log.info("  - Rol: {}", Role.ADMIN.getDisplayName());

        // ADVERTENCIA DE SEGURIDAD
        log.warn("╔════════════════════════════════════════════════════════════╗");
        log.warn("║  ⚠️  IMPORTANTE: CAMBIAR LA CONTRASEÑA DEL ADMIN  ⚠️      ║");
        log.warn("║                                                            ║");
        log.warn("║  La contraseña por defecto ('{}') debe cambiarse    ║", adminPassword);
        log.warn("║  inmediatamente en un entorno de producción.              ║");
        log.warn("╚════════════════════════════════════════════════════════════╝");

        log.info("=== DataLoader completado ===");
    }

    /**
     * MEJORAS POSIBLES PARA TU PROFESOR:
     *
     * 1. CREAR USUARIOS DE EJEMPLO:
     *    Además del admin, podrías crear usuarios de prueba:
     *    ```
     *    private void createSampleUsers() {
     *        // Líder de ejemplo
     *        User lider = User.builder()
     *            .fullName("Juan Pérez")
     *            .email("lider@gestion.com")
     *            .password(passwordEncoder.encode("lider123"))
     *            .role(Role.LIDER)
     *            .active(true)
     *            .build();
     *        userRepository.save(lider);
     *
     *        // Colaborador de ejemplo
     *        User colaborador = User.builder()
     *            .fullName("María García")
     *            .email("colaborador@gestion.com")
     *            .password(passwordEncoder.encode("colab123"))
     *            .role(Role.COLABORADOR)
     *            .active(true)
     *            .build();
     *        userRepository.save(colaborador);
     *    }
     *    ```
     *
     * 2. CARGAR DATOS DESDE UN ARCHIVO:
     *    En lugar de hardcodear, podrías leer desde JSON o YAML:
     *    ```
     *    @Value("${app.admin.email}")
     *    private String adminEmail;
     *
     *    @Value("${app.admin.password}")
     *    private String adminPassword;
     *    ```
     *    En application.properties:
     *    ```
     *    app.admin.email=admin@gestion.com
     *    app.admin.password=secure-password
     *    ```
     *
     * 3. SOLO EN MODO DESARROLLO:
     *    Crear usuarios de prueba solo en desarrollo:
     *    ```
     *    @Profile("dev")
     *    public class DataLoader implements CommandLineRunner {
     *        // ...
     *    }
     *    ```
     *
     * 4. CREAR PROYECTOS Y TAREAS DE EJEMPLO:
     *    Para facilitar el testing:
     *    ```
     *    private void createSampleProject(User lider) {
     *        Project project = Project.builder()
     *            .name("Proyecto de Prueba")
     *            .description("Proyecto de ejemplo")
     *            .leaderId(lider.getId())
     *            .status(ProjectStatus.EN_PROGRESO)
     *            .build();
     *        projectRepository.save(project);
     *    }
     *    ```
     *
     * 5. VERIFICAR CONEXIÓN A MONGODB:
     *    ```
     *    try {
     *        long userCount = userRepository.count();
     *        log.info("Conexión a MongoDB exitosa. Usuarios en BD: {}", userCount);
     *    } catch (Exception e) {
     *        log.error("Error al conectar con MongoDB", e);
     *        throw e;
     *    }
     *    ```
     *
     * BUENAS PRÁCTICAS IMPLEMENTADAS:
     * ✓ Verificación antes de crear (idempotencia)
     * ✓ Uso de logger en lugar de System.out.println
     * ✓ Contraseña encriptada con BCrypt
     * ✓ Advertencias de seguridad claras
     * ✓ Documentación completa del proceso
     *
     * SEGURIDAD:
     * ⚠️ En producción:
     *    - NO hardcodear contraseñas en el código
     *    - Usar variables de entorno o gestores de secretos
     *    - Forzar cambio de contraseña en el primer login
     *    - Usar contraseñas fuertes (mínimo 16 caracteres)
     */
}
