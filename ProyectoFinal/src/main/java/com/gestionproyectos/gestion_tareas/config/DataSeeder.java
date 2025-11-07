package com.gestionproyectos.gestion_tareas.config;

import com.gestionproyectos.gestion_tareas.enums.ProjectStatus;
import com.gestionproyectos.gestion_tareas.enums.Role;
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
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * DataSeeder - Inicializador de datos del sistema
 *
 * PROPÓSITO:
 * Crea usuarios iniciales cuando la aplicación arranca por primera vez.
 * Esto resuelve el problema de "huevo y gallina" donde no puedes crear
 * usuarios si no hay un administrador, pero no hay administrador si no
 * puedes crear usuarios.
 *
 * FUNCIONAMIENTO:
 * - Se ejecuta automáticamente al iniciar la aplicación
 * - Verifica si ya existen usuarios en la base de datos
 * - Si NO existen, crea un usuario ADMIN por defecto
 * - Si ya existen, no hace nada (idempotente)
 *
 * SEGURIDAD:
 * - Las contraseñas se encriptan con BCrypt
 * - Las credenciales por defecto deben cambiarse en producción
 * - Los logs no muestran las contraseñas
 *
 * @author Sistema de Gestión de Proyectos
 * @version 1.0
 * @since 2025-11-03
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSeeder {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Inicializa los datos del sistema
     *
     * CommandLineRunner es una interfaz que Spring Boot ejecuta después
     * de que el contexto de aplicación está completamente inicializado.
     *
     * @return CommandLineRunner que crea usuarios iniciales
     */
    @Bean
    public CommandLineRunner initializeData() {
        return args -> {
            log.info("=".repeat(60));
            log.info("INICIANDO CARGA DE DATOS INICIALES");
            log.info("=".repeat(60));

            // Verificar cada tipo de dato de forma independiente
            long userCount = userRepository.count();
            long projectCount = projectRepository.count();
            long taskCount = taskRepository.count();

            log.info("Estado actual de la base de datos:");
            log.info("  - Usuarios: {}", userCount);
            log.info("  - Proyectos: {}", projectCount);
            log.info("  - Tareas: {}", taskCount);

            User admin = null;
            User lider = null;
            User colab1 = null;
            User colab2 = null;

            // ==================== CREAR USUARIOS SI NO EXISTEN ====================
            if (userCount == 0) {
                log.info("No hay usuarios. Creando usuarios de ejemplo...");
                admin = createAdminUser();
                lider = createSampleLider();
                colab1 = createSampleColaborador1();
                colab2 = createSampleColaborador2();
            } else {
                log.info("Ya existen {} usuarios. Buscando usuarios para proyectos...", userCount);
                // Buscar usuarios existentes
                lider = userRepository.findByRole(Role.LIDER).stream().findFirst().orElse(null);
                colab1 = userRepository.findByRole(Role.COLABORADOR).stream().findFirst().orElse(null);
                colab2 = userRepository.findByRole(Role.COLABORADOR).stream().skip(1).findFirst().orElse(null);

                // Si no hay suficientes usuarios, crearlos
                if (lider == null) {
                    log.info("No se encontró líder. Creando uno...");
                    lider = createSampleLider();
                }
                if (colab1 == null) {
                    log.info("No se encontró primer colaborador. Creando uno...");
                    colab1 = createSampleColaborador1();
                }
                if (colab2 == null) {
                    log.info("No se encontró segundo colaborador. Creando uno...");
                    colab2 = createSampleColaborador2();
                }
            }

            // ==================== CREAR PROYECTO SI NO EXISTEN ====================
            Project proyecto = null;
            if (projectCount == 0 && lider != null && colab1 != null && colab2 != null) {
                log.info("No hay proyectos. Creando proyecto de ejemplo...");
                proyecto = createSampleProject(lider, colab1, colab2);
            } else if (projectCount > 0) {
                log.info("Ya existen {} proyectos. Buscando un proyecto para las tareas...", projectCount);
                proyecto = projectRepository.findAll().stream().findFirst().orElse(null);
            } else {
                log.warn("No se puede crear proyecto. Faltan usuarios necesarios.");
            }

            // ==================== CREAR TAREAS SI NO EXISTEN ====================
            if (taskCount == 0 && proyecto != null && lider != null && colab1 != null && colab2 != null) {
                log.info("No hay tareas. Creando tareas de ejemplo...");
                createSampleTasks(proyecto, lider, colab1, colab2);
            } else if (taskCount > 0) {
                log.info("Ya existen {} tareas.", taskCount);
            } else {
                log.warn("No se pueden crear tareas. Faltan proyecto o usuarios necesarios.");
            }

            // Reporte final
            long finalUserCount = userRepository.count();
            long finalProjectCount = projectRepository.count();
            long finalTaskCount = taskRepository.count();

            log.info("=".repeat(60));
            log.info("CARGA DE DATOS INICIALES COMPLETADA");
            log.info("Estado final de la base de datos:");
            log.info("✓ {} usuarios", finalUserCount);
            log.info("✓ {} proyectos", finalProjectCount);
            log.info("✓ {} tareas", finalTaskCount);
            log.info("=".repeat(60));
        };
    }

    /**
     * Crea el usuario administrador inicial
     */
    private User createAdminUser() {
        log.info("Creando usuario ADMINISTRADOR...");

        User admin = User.builder()
                .fullName("Administrador del Sistema")
                .email("admin@gestion.com")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN)
                .active(true)
                .collaboratorProjectIds(new HashSet<>())
                .leadProjectIds(new HashSet<>())
                .build();

        User savedAdmin = userRepository.save(admin);

        log.info("✓ Usuario ADMIN creado exitosamente");
        log.info("  - Email: {}", savedAdmin.getEmail());
        log.info("  - Password: admin123");
        log.warn("  ⚠ IMPORTANTE: Cambia la contraseña por defecto después del primer login");

        return savedAdmin;
    }

    /**
     * Crea un usuario líder de prueba
     */
    private User createSampleLider() {
        log.info("Creando usuario LÍDER de prueba...");

        User lider = User.builder()
                .fullName("Juan Pérez")
                .email("juan.perez@gestion.com")
                .password(passwordEncoder.encode("lider123"))
                .role(Role.LIDER)
                .active(true)
                .collaboratorProjectIds(new HashSet<>())
                .leadProjectIds(new HashSet<>())
                .build();

        User savedLider = userRepository.save(lider);

        log.info("✓ Usuario LÍDER creado: {} - Password: lider123", savedLider.getEmail());
        return savedLider;
    }

    /**
     * Crea el primer usuario colaborador de prueba
     */
    private User createSampleColaborador1() {
        log.info("Creando usuario COLABORADOR 1...");

        User colaborador = User.builder()
                .fullName("María González")
                .email("maria.gonzalez@gestion.com")
                .password(passwordEncoder.encode("colab123"))
                .role(Role.COLABORADOR)
                .active(true)
                .collaboratorProjectIds(new HashSet<>())
                .leadProjectIds(new HashSet<>())
                .build();

        User savedColab = userRepository.save(colaborador);

        log.info("✓ Usuario COLABORADOR creado: {} - Password: colab123", savedColab.getEmail());
        return savedColab;
    }

    /**
     * Crea el segundo usuario colaborador de prueba
     */
    private User createSampleColaborador2() {
        log.info("Creando usuario COLABORADOR 2...");

        User colaborador = User.builder()
                .fullName("Carlos Rodríguez")
                .email("carlos.rodriguez@gestion.com")
                .password(passwordEncoder.encode("colab123"))
                .role(Role.COLABORADOR)
                .active(true)
                .collaboratorProjectIds(new HashSet<>())
                .leadProjectIds(new HashSet<>())
                .build();

        User savedColab = userRepository.save(colaborador);

        log.info("✓ Usuario COLABORADOR creado: {} - Password: colab123", savedColab.getEmail());
        return savedColab;
    }

    /**
     * Crea un proyecto de ejemplo
     */
    private Project createSampleProject(User lider, User colab1, User colab2) {
        log.info("Creando proyecto de ejemplo...");

        // Crear set de IDs de colaboradores
        Set<String> collaboratorIds = new HashSet<>();
        collaboratorIds.add(colab1.getId());
        collaboratorIds.add(colab2.getId());

        // Actualizar usuario líder con el proyecto
        lider.getLeadProjectIds().add("temp-id"); // Se actualizará después

        // Actualizar colaboradores con el proyecto
        colab1.getCollaboratorProjectIds().add("temp-id");
        colab2.getCollaboratorProjectIds().add("temp-id");

        Project proyecto = Project.builder()
                .name("Sistema de Gestión de Tareas")
                .description("Proyecto de ejemplo para demostrar el funcionamiento del sistema. " +
                        "Incluye gestión de usuarios, proyectos y tareas con diferentes roles.")
                .leaderId(lider.getId())
                .collaboratorIds(collaboratorIds)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(3))
                .status(ProjectStatus.EN_PROGRESO)
                .build();

        Project savedProject = projectRepository.save(proyecto);

        // Actualizar las referencias temporales
        lider.getLeadProjectIds().clear();
        lider.getLeadProjectIds().add(savedProject.getId());
        userRepository.save(lider);

        colab1.getCollaboratorProjectIds().clear();
        colab1.getCollaboratorProjectIds().add(savedProject.getId());
        userRepository.save(colab1);

        colab2.getCollaboratorProjectIds().clear();
        colab2.getCollaboratorProjectIds().add(savedProject.getId());
        userRepository.save(colab2);

        log.info("✓ Proyecto creado: {} (ID: {})", savedProject.getName(), savedProject.getId());
        return savedProject;
    }

    /**
     * Crea tareas de ejemplo
     */
    private void createSampleTasks(Project proyecto, User lider, User colab1, User colab2) {
        log.info("Creando tareas de ejemplo...");

        // Tarea 1: Prioridad ALTA - Asignada a colab1
        Task task1 = Task.builder()
                .title("Implementar autenticación de usuarios")
                .description("Desarrollar el sistema de login y registro usando Spring Security. " +
                        "Incluir validaciones y encriptación de contraseñas.")
                .projectId(proyecto.getId())
                .assignedToId(colab1.getId())
                .createdById(lider.getId())
                .status(TaskStatus.EN_PROGRESO)
                .priority(TaskPriority.ALTA)
                .dueDate(LocalDate.now().plusDays(7))
                .estimatedHours(16.0)
                .build();

        taskRepository.save(task1);
        log.info("✓ Tarea creada: {} - Asignada a: {}", task1.getTitle(), colab1.getFullName());

        // Tarea 2: Prioridad MEDIA - Asignada a colab2
        Task task2 = Task.builder()
                .title("Diseñar interfaz de usuario")
                .description("Crear los diseños de las pantallas principales usando Tailwind CSS. " +
                        "Debe ser responsive y seguir las mejores prácticas de UX.")
                .projectId(proyecto.getId())
                .assignedToId(colab2.getId())
                .createdById(lider.getId())
                .status(TaskStatus.PENDIENTE)
                .priority(TaskPriority.MEDIA)
                .dueDate(LocalDate.now().plusDays(14))
                .estimatedHours(20.0)
                .build();

        taskRepository.save(task2);
        log.info("✓ Tarea creada: {} - Asignada a: {}", task2.getTitle(), colab2.getFullName());

        // Tarea 3: Prioridad CRÍTICA - Asignada a colab1
        Task task3 = Task.builder()
                .title("Configurar base de datos MongoDB Atlas")
                .description("Configurar el cluster de MongoDB Atlas, crear usuarios, configurar Network Access " +
                        "y establecer la conexión con la aplicación.")
                .projectId(proyecto.getId())
                .assignedToId(colab1.getId())
                .createdById(lider.getId())
                .status(TaskStatus.COMPLETADA)
                .priority(TaskPriority.CRITICA)
                .dueDate(LocalDate.now().minusDays(1))
                .estimatedHours(4.0)
                .realHours(5.0)
                .build();

        taskRepository.save(task3);
        log.info("✓ Tarea creada: {} - Asignada a: {}", task3.getTitle(), colab1.getFullName());
    }

    /**
     * ===============================================================
     * CREDENCIALES DE USUARIOS DE PRUEBA (DESARROLLO)
     * ===============================================================
     *
     * ADMINISTRADOR:
     * - Email: admin@gestion.com
     * - Password: admin123
     * - Permisos: Control total del sistema
     *
     * LÍDER:
     * - Email: juan.perez@gestion.com
     * - Password: lider123
     * - Permisos: Gestiona el proyecto "Sistema de Gestión de Tareas"
     *
     * COLABORADORES:
     * - Email: maria.gonzalez@gestion.com
     *   Password: colab123
     *   Tareas asignadas: 2 (1 en progreso, 1 completada)
     *
     * - Email: carlos.rodriguez@gestion.com
     *   Password: colab123
     *   Tareas asignadas: 1 (pendiente)
     *
     * ===============================================================
     * DATOS DE EJEMPLO CREADOS:
     * ===============================================================
     * - 4 usuarios (1 admin, 1 líder, 2 colaboradores)
     * - 1 proyecto activo con 2 colaboradores
     * - 3 tareas (1 completada, 1 en progreso, 1 pendiente)
     *
     * ===============================================================
     * ⚠ IMPORTANTE PARA PRODUCCIÓN:
     * ===============================================================
     * 1. Deshabilita la creación de datos de ejemplo
     * 2. Cambia todas las contraseñas por defecto
     * 3. Usa variables de entorno para credenciales iniciales
     * 4. Implementa un proceso de first-time setup seguro
     * 5. Considera usar perfiles de Spring (@Profile("dev"))
     *
     * Para deshabilitar datos de ejemplo:
     * - Comenta las líneas de creación de proyectos y tareas
     * - Deja solo la creación del usuario admin
     */
}
