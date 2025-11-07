# Vista Funcional - Sistema de Gestión de Proyectos y Tareas

**Fecha:** 2025-11-06
**Versión:** 2.0
**Proyecto:** Sistema de Gestión de Proyectos y Tareas

---

## 1. Introducción

### 1.1 Propósito del Documento
Este documento describe la **Vista Funcional** del sistema, detallando los componentes principales, sus responsabilidades, interfaces y las interacciones entre ellos.

### 1.2 Alcance
La vista funcional define:
- Arquitectura de componentes
- Responsabilidades de cada componente
- Interfaces entre componentes
- Flujos de datos y control
- Patrones arquitectónicos aplicados

---

## 2. Arquitectura General

### 2.1 Patrón Arquitectónico Principal
**Arquitectura en Capas (Layered Architecture) + MVC (Model-View-Controller)**

### 2.2 Diagrama de Arquitectura de Alto Nivel

```
┌───────────────────────────────────────────────────────────────────┐
│                      CAPA DE PRESENTACIÓN                         │
│  ┌─────────────────────────────────────────────────────────────┐  │
│  │              Thymeleaf Templates (Views)                    │  │
│  │  - Layouts (base, admin, lider, colaborador)                │  │
│  │  - Fragments (navbars, notifications, components)           │  │
│  │  - Views (auth, admin, lider, colaborador, tareas)          │  │
│  └─────────────────────────────────────────────────────────────┘  │
└────────────────────────────────┬──────────────────────────────────┘
                                 │ HTTP Request/Response
┌────────────────────────────────▼──────────────────────────────────┐
│                      CAPA DE CONTROLADORES                        │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐               │
│  │    Auth      │ │    Admin     │ │    Lider     │               │
│  │  Controller  │ │  Controllers │ │  Controller  │               │
│  └──────────────┘ └──────────────┘ └──────────────┘               │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐               │
│  │    Task      │ │  Dashboard   │ │    Health    │               │
│  │  Controller  │ │  Controller  │ │    Check     │               │
│  └──────────────┘ └──────────────┘ └──────────────┘               │
└────────────────────────────────┬──────────────────────────────────┘
                                 │ Method Calls
┌────────────────────────────────▼──────────────────────────────────┐
│                  CAPA DE SEGURIDAD (Cross-Cutting)                │
│  ┌─────────────────────────────────────────────────────────────┐  │
│  │              Spring Security Components                     │  │
│  │  - SecurityConfig (RBAC Configuration)                      │  │
│  │  - CustomUserDetailsService (User Loading)                  │  │
│  │  - LoginRateLimitFilter (Rate Limiting)                     │  │
│  │  - PasswordEncoder (BCrypt)                                 │  │
│  └─────────────────────────────────────────────────────────────┘  │
└───────────────────────────────────────────────────────────────────┘
                                 │
┌────────────────────────────────▼──────────────────────────────────┐
│                      CAPA DE SERVICIOS (Lógica de Negocio)        │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐               │
│  │     User     │ │   Project    │ │     Task     │               │
│  │   Service    │ │   Service    │ │   Service    │               │
│  └──────────────┘ └──────────────┘ └──────────────┘               │
│  ┌──────────────┐ ┌──────────────┐                                │
│  │   Metrics    │ │  RateLimit   │                                │
│  │   Service    │ │   Service    │                                │
│  └──────────────┘ └──────────────┘                                │
└────────────────────────────────┬──────────────────────────────────┘
                                 │
┌────────────────────────────────▼──────────────────────────────────┐
│                      CAPA DE MAPPERS (Conversión)                 │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐               │
│  │     User     │ │   Project    │ │     Task     │               │
│  │    Mapper    │ │    Mapper    │ │    Mapper    │               │
│  └──────────────┘ └──────────────┘ └──────────────┘               │
└────────────────────────────────┬──────────────────────────────────┘
                                 │
┌────────────────────────────────▼──────────────────────────────────┐
│                         CAPA DE DTOs                              │
│  ┌─────────────────────────────────────────────────────────────┐  │
│  │  UserRegistrationDto, UserUpdateDto, UserListDto,           │  │
│  │  ProjectCreationDto, ProjectUpdateDto, ProjectDetailsDto,   │  │
│  │  TaskCreationDto, TaskDetailsDto, TaskUpdateDto             │  │
│  └─────────────────────────────────────────────────────────────┘  │
└───────────────────────────────────────────────────────────────────┘
                                 │
┌────────────────────────────────▼──────────────────────────────────┐
│                      CAPA DE REPOSITORIOS (Acceso a Datos)        │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐               │
│  │     User     │ │   Project    │ │     Task     │               │
│  │  Repository  │ │  Repository  │ │  Repository  │               │
│  │ (Spring Data)│ │ (Spring Data)│ │ (Spring Data)│               │ 
│  └──────────────┘ └──────────────┘ └──────────────┘               │
└────────────────────────────────┬──────────────────────────────────┘
                                 │ MongoDB Wire Protocol
┌────────────────────────────────▼──────────────────────────────────┐
│                  CAPA DE PERSISTENCIA (MongoDB)                   │
│  ┌─────────────────────────────────────────────────────────────┐  │
│  │                MongoDB Atlas Cloud Database                 │  │
│  │  Collections: users, projects, tasks                        │  │
│  └─────────────────────────────────────────────────────────────┘  │
└───────────────────────────────────────────────────────────────────┘
```

---

## 3. Componentes Principales

### 3.1 Capa de Presentación

#### 3.1.1 Thymeleaf Templates
**Responsabilidades:**
- Renderizar HTML dinámico del lado del servidor
- Aplicar estilos con Tailwind CSS
- Ejecutar lógica de presentación básica
- Gestionar formularios y validación del lado del cliente

**Componentes:**

##### Layouts Reutilizables
| Archivo | Descripción |
|---------|-------------|
| `layouts/base.html` | Layout base con estructura HTML común |
| `layouts/admin-layout.html` | Layout específico para administradores |
| `layouts/lider-layout.html` | Layout específico para líderes |
| `layouts/colaborador-layout.html` | Layout específico para colaboradores |

##### Fragments (Componentes Reutilizables)
| Archivo | Descripción |
|---------|-------------|
| `fragments/admin-navbar.html` | Barra de navegación del admin |
| `fragments/lider-navbar.html` | Barra de navegación del líder |
| `fragments/colaborador-navbar.html` | Barra de navegación del colaborador |
| `fragments/notifications.html` | Sistema de notificaciones (mensajes flash) |
| `fragments/dark-mode.html` | Modo oscuro (opcional) |
| `fragments/loading.html` | Indicador de carga |

##### Views por Módulo
**Autenticación:**
- `auth/login.html` - Formulario de login
- `auth/register.html` - Registro público (opcional)

**Admin:**
- `admin/dashboard.html` - Dashboard con métricas
- `admin/usuarios.html` - Listado de usuarios
- `admin/crear-usuario.html` - Formulario de creación
- `admin/editar-usuario.html` - Formulario de edición
- `admin/proyectos.html` - Listado de proyectos
- `admin/crear-proyecto.html` - Formulario de creación
- `admin/editar-proyecto.html` - Formulario de edición

**Líder:**
- `lider/dashboard.html` - Dashboard del líder
- `lider/proyectos.html` - Proyectos asignados
- `lider/ver-proyecto.html` - Vista detallada del proyecto
- `lider/crear-tarea.html` - Formulario de creación de tarea
- `lider/gestionar-colaboradores.html` - Gestión de colaboradores

**Colaborador:**
- `colaborador/mis-tareas.html` - Tareas asignadas

**Tareas:**
- `tareas/detalle.html` - Detalle completo de tarea

**Interfaces Proporcionadas:**
- Formularios HTML con validación
- Tablas de datos
- Dashboards con métricas
- Notificaciones de éxito/error

**Dependencias:**
- Tailwind CSS 3.4 (compilado)
- JavaScript vanilla para interactividad

---

### 3.2 Capa de Controladores

#### 3.2.1 AuthController
**Ubicación:** `com.gestionproyectos.gestion_tareas.controller.AuthController`

**Responsabilidades:**
- Gestionar proceso de login
- Gestionar proceso de registro
- Redirigir usuarios según su rol
- Mostrar errores de autenticación

**Endpoints:**
```java
GET  /login           // Formulario de login
POST /login           // Procesado por Spring Security
GET  /login?error     // Login fallido
GET  /login?logout    // Logout exitoso
GET  /register        // Formulario de registro (opcional)
POST /register        // Procesado por AuthController
GET  /                // Página de inicio (redirige a /login)
```

**Interacciones:**
- **Entrada:** Peticiones HTTP con credenciales
- **Salida:** Vistas Thymeleaf
- **Servicios Usados:** `UserService`
- **Seguridad:** Spring Security integrada

---

#### 3.2.2 AdminDashboardController
**Ubicación:** `com.gestionproyectos.gestion_tareas.controller.AdminDashboardController`

**Responsabilidades:**
- Mostrar dashboard de administrador
- Proveer métricas globales del sistema
- Centralizar navegación administrativa

**Endpoints:**
```java
GET /admin/dashboard  // Dashboard principal del admin
```

**Interacciones:**
- **Servicios Usados:** `MetricsService`, `UserService`, `ProjectService`
- **Datos Proporcionados:**
  - Total de usuarios
  - Total de proyectos
  - Total de tareas
  - Estadísticas de estado de tareas

---

#### 3.2.3 AdminUserController
**Ubicación:** `com.gestionproyectos.gestion_tareas.controller.AdminUserController`

**Responsabilidades:**
- Gestionar CRUD de usuarios
- Validar datos de usuarios
- Controlar acceso solo a administradores

**Endpoints:**
```java
GET  /admin/usuarios              // Listado de usuarios
GET  /admin/crear-usuario         // Formulario de creación
POST /admin/crear-usuario         // Crear nuevo usuario
GET  /admin/editar-usuario/{id}   // Formulario de edición
POST /admin/editar-usuario/{id}   // Actualizar usuario
POST /admin/eliminar-usuario/{id} // Eliminar usuario (soft delete)
```

**Interacciones:**
- **Servicios Usados:** `UserService`
- **DTOs Usados:** `UserRegistrationDto`, `UserUpdateDto`, `UserListDto`
- **Validaciones:** Email único, roles válidos, contraseñas encriptadas

**Seguridad:**
```java
@PreAuthorize("hasRole('ADMIN')")
```

---

#### 3.2.4 AdminProjectController
**Ubicación:** `com.gestionproyectos.gestion_tareas.controller.AdminProjectController`

**Responsabilidades:**
- Gestionar CRUD de proyectos (desde perspectiva admin)
- Asignar líderes a proyectos
- Validar datos de proyectos

**Endpoints:**
```java
GET  /admin/proyectos               // Listado de proyectos
GET  /admin/crear-proyecto          // Formulario de creación
POST /admin/crear-proyecto          // Crear nuevo proyecto
GET  /admin/editar-proyecto/{id}    // Formulario de edición
POST /admin/editar-proyecto/{id}    // Actualizar proyecto
POST /admin/eliminar-proyecto/{id}  // Eliminar proyecto (soft delete)
```

**Interacciones:**
- **Servicios Usados:** `ProjectService`, `UserService`
- **DTOs Usados:** `ProjectCreationDto`, `ProjectUpdateDto`, `ProjectDetailsDto`
- **Validaciones:** Líder válido, nombre de proyecto único

**Seguridad:**
```java
@PreAuthorize("hasRole('ADMIN')")
```

---

#### 3.2.5 LiderController
**Ubicación:** `com.gestionproyectos.gestion_tareas.controller.LiderController`

**Responsabilidades:**
- Gestionar proyectos del líder
- Crear tareas en proyectos asignados
- Ver todas las tareas del proyecto
- Gestionar colaboradores

**Endpoints:**
```java
GET  /lider/dashboard                           // Dashboard del líder
GET  /lider/proyectos                           // Proyectos asignados
GET  /lider/proyecto/{projectId}                // Vista detallada del proyecto
GET  /lider/proyecto/{projectId}/nueva-tarea    // Formulario de creación de tarea
POST /lider/proyecto/{projectId}/nueva-tarea    // Crear nueva tarea
GET  /lider/proyecto/{projectId}/editar         // Editar proyecto
POST /lider/proyecto/{projectId}/editar         // Actualizar proyecto
GET  /lider/proyecto/{projectId}/gestionar-colaboradores // Gestionar colaboradores
POST /lider/proyecto/{projectId}/agregar-colaborador     // Agregar colaborador
```

**Interacciones:**
- **Servicios Usados:** `ProjectService`, `TaskService`, `UserService`
- **DTOs Usados:** `TaskCreationDto`, `ProjectDetailsDto`, `TaskDetailsDto`
- **Validaciones:** Solo líder del proyecto puede acceder

**Seguridad:**
```java
@PreAuthorize("hasAnyRole('LIDER', 'ADMIN')")
```

---

#### 3.2.6 TaskController
**Ubicación:** `com.gestionproyectos.gestion_tareas.controller.TaskController`

**Responsabilidades:**
- Actualizar estado de tareas
- Mostrar detalles de tareas
- Validar transiciones de estado

**Endpoints:**
```java
GET  /tareas/{taskId}/detalle                    // Detalle de tarea
POST /tareas/{taskId}/actualizar-estado          // Actualizar estado
POST /tareas/{taskId}/actualizar                 // Actualizar datos de tarea
POST /tareas/{taskId}/eliminar                   // Eliminar tarea
```

**Interacciones:**
- **Servicios Usados:** `TaskService`, `UserService`
- **DTOs Usados:** `TaskDetailsDto`, `TaskUpdateDto`
- **Validaciones:** Solo responsable o líder puede actualizar

**Seguridad:**
```java
@PreAuthorize("hasAnyRole('COLABORADOR', 'LIDER', 'ADMIN')")
```

**Lógica de Negocio:**
- Validación de transiciones de estado:
  - PENDIENTE → EN_PROGRESO
  - EN_PROGRESO → EN_REVISION, BLOQUEADA, COMPLETADA
  - EN_REVISION → EN_PROGRESO, COMPLETADA
  - BLOQUEADA → EN_PROGRESO
  - COMPLETADA → (estado final)

---

#### 3.2.7 DashboardController
**Ubicación:** `com.gestionproyectos.gestion_tareas.controller.DashboardController`

**Responsabilidades:**
- Redirigir a dashboard según rol
- Mostrar vista de tareas del colaborador
- Proporcionar vista unificada

**Endpoints:**
```java
GET /dashboard      // Redirige según rol
GET /mis-tareas     // Tareas del usuario actual
```

**Interacciones:**
- **Servicios Usados:** `TaskService`, `UserService`
- **DTOs Usados:** `TaskDetailsDto`
- **Filtros:** Solo tareas asignadas al usuario actual

**Seguridad:**
```java
@PreAuthorize("isAuthenticated()")
```

---

#### 3.2.8 HealthCheckController
**Ubicación:** `com.gestionproyectos.gestion_tareas.controller.HealthCheckController`

**Responsabilidades:**
- Proporcionar endpoint de salud para monitoreo
- Verificar conexión a MongoDB
- Retornar estado del sistema

**Endpoints:**
```java
GET /health         // Estado de salud del sistema
GET /api/health     // Estado de salud (JSON)
```

**Respuesta:**
```json
{
  "status": "UP",
  "components": {
    "mongo": {
      "status": "UP"
    }
  }
}
```

---

### 3.3 Capa de Seguridad

#### 3.3.1 SecurityConfig
**Ubicación:** `com.gestionproyectos.gestion_tareas.config.SecurityConfig`

**Responsabilidades:**
- Configurar Spring Security
- Definir reglas de autorización por URL
- Configurar autenticación basada en sesiones
- Habilitar CSRF protection
- Configurar encriptación de contraseñas

**Configuración de Rutas:**
```java
/login, /register, /css/**, /js/**  → Acceso público
/admin/**                            → Solo ADMIN
/lider/**                            → LIDER o ADMIN
/mis-tareas                          → Autenticado
/**                                  → Requiere autenticación
```

**PasswordEncoder:**
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);  // Strength 12
}
```

**Redirecciones después del Login:**
- ADMIN → `/admin/dashboard`
- LIDER → `/lider/dashboard`
- COLABORADOR → `/mis-tareas`

---

#### 3.3.2 CustomUserDetailsService
**Ubicación:** `com.gestionproyectos.gestion_tareas.service.CustomUserDetailsService`

**Responsabilidades:**
- Cargar usuarios desde MongoDB para Spring Security
- Convertir `User` a `UserDetails`
- Proporcionar roles y permisos

**Interfaz Implementada:**
```java
public class CustomUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            getAuthorities(user.getRole())
        );
    }
}
```

---

#### 3.3.3 LoginRateLimitFilter
**Ubicación:** `com.gestionproyectos.gestion_tareas.security.LoginRateLimitFilter`

**Responsabilidades:**
- Prevenir ataques de fuerza bruta en login
- Limitar intentos de login por IP
- Bloquear temporalmente IPs sospechosas

**Configuración:**
- **Límite:** 5 intentos por IP
- **Ventana:** 15 minutos
- **Bloqueo:** 30 minutos después de exceder el límite

**Implementación:**
```java
public class LoginRateLimitFilter extends OncePerRequestFilter {
    private final RateLimitService rateLimitService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, ...) {
        if (request.getRequestURI().equals("/login")
            && request.getMethod().equals("POST")) {

            String clientIP = getClientIP(request);

            if (!rateLimitService.allowRequest(clientIP)) {
                response.setStatus(429);  // Too Many Requests
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
```

---

### 3.4 Capa de Servicios

#### 3.4.1 UserService
**Ubicación:** `com.gestionproyectos.gestion_tareas.service.UserService`

**Responsabilidades:**
- Gestionar lógica de negocio de usuarios
- Validar datos de usuarios
- Encriptar contraseñas
- Verificar unicidad de emails
- Proporcionar usuarios por rol

**Métodos Principales:**
```java
public interface UserService {
    User registerUser(UserRegistrationDto dto);
    User updateUser(String userId, UserUpdateDto dto);
    User getUserById(String id);
    User getUserByEmail(String email);
    List<UserListDto> getAllUsers();
    List<UserSimpleDto> getAvailableLideres();
    List<UserSimpleDto> findCollaborators();
    void deleteUser(String userId);
    boolean emailExists(String email);
}
```

**Validaciones:**
- Email único en el sistema
- Rol válido (ADMIN, LIDER, COLABORADOR)
- Contraseña mínima de 6 caracteres
- Nombre completo obligatorio

**Dependencias:**
- `UserRepository`
- `PasswordEncoder`
- `UserMapper`

---

#### 3.4.2 ProjectService
**Ubicación:** `com.gestionproyectos.gestion_tareas.service.ProjectService`

**Responsabilidades:**
- Gestionar lógica de negocio de proyectos
- Validar datos de proyectos
- Asignar líderes
- Gestionar colaboradores
- Calcular progreso

**Métodos Principales:**
```java
public interface ProjectService {
    Project createProject(ProjectCreationDto dto);
    Project updateProject(String projectId, ProjectUpdateDto dto);
    ProjectDetailsDto getProjectById(String id);
    List<ProjectDetailsDto> getAllProjects();
    List<ProjectDetailsDto> getProjectsByLeaderId(String leaderId);
    void deleteProject(String projectId);
    void addCollaborator(String projectId, String collaboratorId);
    void removeCollaborator(String projectId, String collaboratorId);
    double calculateProgress(String projectId);
}
```

**Validaciones:**
- Líder debe existir y tener rol LIDER
- Nombre de proyecto no vacío
- Estado válido (PLANNING, IN_PROGRESS, COMPLETED)

**Dependencias:**
- `ProjectRepository`
- `UserRepository`
- `TaskRepository`
- `ProjectMapper`

---

#### 3.4.3 TaskService
**Ubicación:** `com.gestionproyectos.gestion_tareas.service.TaskService`

**Responsabilidades:**
- Gestionar lógica de negocio de tareas
- Validar asignaciones
- Validar transiciones de estado
- Calcular métricas de tareas

**Métodos Principales:**
```java
public interface TaskService {
    Task createTask(TaskCreationDto dto, String projectId, String creatorId);
    Task updateTask(String taskId, TaskUpdateDto dto);
    TaskDetailsDto getTaskById(String id);
    List<TaskDetailsDto> getTasksByProjectId(String projectId);
    List<TaskDetailsDto> getTasksForUser(String userId);
    void updateTaskStatus(String taskId, TaskStatus newStatus, User currentUser);
    void deleteTask(String taskId);
    ProjectDetailsDto getProjectDetails(String projectId);
}
```

**Validaciones:**
- Responsable debe existir y ser COLABORADOR
- Prioridad válida (BAJA, MEDIA, ALTA, CRÍTICA)
- Transición de estado válida
- Solo responsable o líder puede actualizar
- Fecha de vencimiento en el futuro

**Lógica de Transiciones:**
```java
public enum TaskStatus {
    PENDIENTE,
    EN_PROGRESO,
    BLOQUEADA,
    EN_REVISION,
    COMPLETADA;

    public boolean canTransitionTo(TaskStatus newStatus) {
        return switch (this) {
            case PENDIENTE -> newStatus == EN_PROGRESO;
            case EN_PROGRESO -> newStatus == EN_REVISION
                             || newStatus == BLOQUEADA
                             || newStatus == COMPLETADA;
            case EN_REVISION -> newStatus == EN_PROGRESO
                             || newStatus == COMPLETADA;
            case BLOQUEADA -> newStatus == EN_PROGRESO;
            case COMPLETADA -> false;  // Estado final
        };
    }
}
```

**Dependencias:**
- `TaskRepository`
- `ProjectRepository`
- `UserRepository`
- `TaskMapper`

---

#### 3.4.4 MetricsService
**Ubicación:** `com.gestionproyectos.gestion_tareas.service.MetricsService`

**Responsabilidades:**
- Calcular estadísticas globales
- Generar reportes
- Cachear métricas frecuentes

**Métodos Principales:**
```java
public interface MetricsService {
    long getTotalUsers();
    long getTotalProjects();
    long getTotalTasks();
    Map<TaskStatus, Long> getTaskStatusDistribution();
    Map<TaskPriority, Long> getTaskPriorityDistribution();
    double getAverageProjectProgress();
    List<ProjectMetricsDto> getProjectMetrics();
}
```

**Caché:**
```java
@Cacheable(value = "metrics", key = "'totalUsers'")
public long getTotalUsers() {
    return userRepository.count();
}
```

**Dependencias:**
- `UserRepository`
- `ProjectRepository`
- `TaskRepository`

---

#### 3.4.5 RateLimitService
**Ubicación:** `com.gestionproyectos.gestion_tareas.service.RateLimitService`

**Responsabilidades:**
- Controlar tasa de peticiones por IP
- Bloquear IPs sospechosas
- Limpiar registros antiguos

**Implementación:**
```java
@Service
public class RateLimitService {
    private final Map<String, List<Long>> requestMap = new ConcurrentHashMap<>();
    private final int MAX_REQUESTS = 5;
    private final long TIME_WINDOW = 15 * 60 * 1000; // 15 minutos

    public boolean allowRequest(String clientIP) {
        List<Long> timestamps = requestMap.computeIfAbsent(clientIP,
                                                           k -> new ArrayList<>());
        long now = System.currentTimeMillis();

        // Limpiar timestamps antiguos
        timestamps.removeIf(t -> now - t > TIME_WINDOW);

        if (timestamps.size() >= MAX_REQUESTS) {
            return false;
        }

        timestamps.add(now);
        return true;
    }
}
```

---

### 3.5 Capa de Mappers

#### 3.5.1 UserMapper
**Ubicación:** `com.gestionproyectos.gestion_tareas.mapper.UserMapper`

**Responsabilidades:**
- Convertir `User` ↔ DTOs
- Ocultar información sensible (contraseñas)
- Formatear datos para presentación

**Conversiones:**
```java
public class UserMapper {
    public static UserListDto toUserListDto(User user) {
        return new UserListDto(
            user.getId(),
            user.getFullName(),
            user.getEmail(),
            user.getRole().name(),
            user.isActive()
        );
    }

    public static UserSimpleDto toUserSimpleDto(User user) {
        return new UserSimpleDto(
            user.getId(),
            user.getFullName(),
            user.getEmail()
        );
    }
}
```

---

#### 3.5.2 ProjectMapper
**Ubicación:** `com.gestionproyectos.gestion_tareas.mapper.ProjectMapper`

**Responsabilidades:**
- Convertir `Project` ↔ DTOs
- Incluir datos relacionados (líder, colaboradores)
- Calcular progreso

**Conversiones:**
```java
public class ProjectMapper {
    public static ProjectDetailsDto toProjectDetailsDto(Project project,
                                                         User leader,
                                                         List<User> collaborators,
                                                         List<Task> tasks) {
        return new ProjectDetailsDto(
            project.getId(),
            project.getName(),
            project.getDescription(),
            project.getStatus(),
            UserMapper.toUserSimpleDto(leader),
            collaborators.stream()
                .map(UserMapper::toUserSimpleDto)
                .toList(),
            tasks.stream()
                .map(TaskMapper::toTaskDetailsDto)
                .toList(),
            calculateProgress(tasks)
        );
    }
}
```

---

### 3.6 Capa de Repositorios

#### 3.6.1 UserRepository
**Ubicación:** `com.gestionproyectos.gestion_tareas.repository.UserRepository`

**Responsabilidades:**
- Acceso a datos de usuarios en MongoDB
- Consultas personalizadas

**Interfaz:**
```java
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByRole(Role role);
    List<User> findByIsActiveTrue();

    @Query("{ 'role': 'LIDER', 'isActive': true }")
    List<User> findActiveLideres();
}
```

---

#### 3.6.2 ProjectRepository
**Ubicación:** `com.gestionproyectos.gestion_tareas.repository.ProjectRepository`

**Responsabilidades:**
- Acceso a datos de proyectos en MongoDB
- Consultas por líder

**Interfaz:**
```java
public interface ProjectRepository extends MongoRepository<Project, String> {
    List<Project> findByLeaderId(String leaderId);
    List<Project> findByStatus(ProjectStatus status);

    @Query("{ 'collaboratorIds': ?0 }")
    List<Project> findByCollaboratorId(String collaboratorId);
}
```

---

#### 3.6.3 TaskRepository
**Ubicación:** `com.gestionproyectos.gestion_tareas.repository.TaskRepository`

**Responsabilidades:**
- Acceso a datos de tareas en MongoDB
- Consultas por proyecto, responsable, estado

**Interfaz:**
```java
public interface TaskRepository extends MongoRepository<Task, String> {
    List<Task> findByProjectId(String projectId);
    List<Task> findByAssignedToId(String userId);
    List<Task> findByStatus(TaskStatus status);
    List<Task> findByPriority(TaskPriority priority);

    @Query("{ 'projectId': ?0, 'status': ?1 }")
    List<Task> findByProjectIdAndStatus(String projectId, TaskStatus status);

    long countByProjectIdAndStatus(String projectId, TaskStatus status);
}
```

---

## 4. Flujos de Datos

### 4.1 Flujo de Creación de Tarea (Líder)

```
┌─────────────┐
│   Líder     │
└──────┬──────┘
       │ 1. GET /lider/proyecto/{id}/nueva-tarea
       ▼
┌──────────────────┐
│ LiderController  │
└──────┬───────────┘
       │ 2. projectService.getProjectById(id)
       ▼
┌──────────────────┐
│  ProjectService  │
└──────┬───────────┘
       │ 3. projectRepository.findById(id)
       ▼
┌──────────────────┐
│ProjectRepository │
└──────┬───────────┘
       │ 4. MongoDB query
       ▼
┌──────────────────┐
│  MongoDB Atlas   │
└──────┬───────────┘
       │ 5. Return Project
       │
       │ 6. userService.findCollaborators()
       ▼
┌──────────────────┐
│   UserService    │
└──────┬───────────┘
       │ 7. userRepository.findByRole(COLABORADOR)
       ▼
┌──────────────────┐
│  UserRepository  │
└──────┬───────────┘
       │ 8. Return List<User>
       ▼
┌──────────────────┐
│ LiderController  │ → 9. Render form with collaborators
└──────────────────┘
       │
       │ 10. POST /lider/proyecto/{id}/nueva-tarea
       ▼
┌──────────────────┐
│ LiderController  │ → 11. Validate form
└──────┬───────────┘
       │ 12. taskService.createTask(dto, projectId, creatorId)
       ▼
┌──────────────────┐
│   TaskService    │ → 13. Validate business rules
└──────┬───────────┘
       │ 14. Create Task entity
       │ 15. taskRepository.save(task)
       ▼
┌──────────────────┐
│  TaskRepository  │
└──────┬───────────┘
       │ 16. Insert into MongoDB
       ▼
┌──────────────────┐
│  MongoDB Atlas   │
└──────┬───────────┘
       │ 17. Return saved Task
       ▼
┌──────────────────┐
│ LiderController  │ → 18. Redirect with success message
└──────────────────┘
```

---

### 4.2 Flujo de Actualización de Estado (Colaborador)

```
┌─────────────┐
│ Colaborador │
└──────┬──────┘
       │ 1. POST /tareas/{id}/actualizar-estado?newStatus=EN_PROGRESO
       ▼
┌──────────────────┐
│  TaskController  │
└──────┬───────────┘
       │ 2. Get current user from SecurityContext
       │ 3. taskService.updateTaskStatus(taskId, newStatus, currentUser)
       ▼
┌──────────────────┐
│   TaskService    │
└──────┬───────────┘
       │ 4. taskRepository.findById(taskId)
       ▼
┌──────────────────┐
│  TaskRepository  │
└──────┬───────────┘
       │ 5. Return Task
       ▼
┌──────────────────┐
│   TaskService    │ → 6. VALIDATE: Is user assigned to task?
└──────┬───────────┘
       │ 7. VALIDATE: Is transition allowed?
       │ 8. task.changeStatus(newStatus)
       │ 9. taskRepository.save(task)
       ▼
┌──────────────────┐
│  TaskRepository  │
└──────┬───────────┘
       │ 10. Update in MongoDB
       ▼
┌──────────────────┐
│  MongoDB Atlas   │
└──────┬───────────┘
       │ 11. Return updated Task
       ▼
┌──────────────────┐
│  TaskController  │ → 12. Redirect with success message
└──────────────────┘
```

---

## 5. Patrones de Diseño Aplicados

### 5.1 Patrones Arquitectónicos
- **MVC (Model-View-Controller):** Separación de presentación, lógica y datos
- **Layered Architecture:** Capas con responsabilidades bien definidas
- **Repository Pattern:** Abstracción de acceso a datos

### 5.2 Patrones de Diseño
- **DTO (Data Transfer Object):** Transferencia de datos entre capas
- **Mapper:** Conversión entre entidades y DTOs
- **Dependency Injection:** Gestión de dependencias con Spring
- **Service Layer:** Lógica de negocio centralizada
- **Front Controller:** Spring DispatcherServlet

### 5.3 Patrones de Seguridad
- **RBAC (Role-Based Access Control):** Control de acceso basado en roles
- **Rate Limiting:** Protección contra fuerza bruta
- **CSRF Protection:** Prevención de ataques CSRF

---

## 6. Optimizaciones de Rendimiento

### 6.1 Caché (Caffeine)
**Configuración:**
```java
@Cacheable(value = "users", key = "#id")
public User getUserById(String id) {
    return userRepository.findById(id).orElse(null);
}

@Cacheable(value = "projects", key = "#leaderId")
public List<Project> getProjectsByLeaderId(String leaderId) {
    return projectRepository.findByLeaderId(leaderId);
}
```

**Beneficios:**
- Reducción del 60-70% en consultas a MongoDB
- Mejora en tiempo de respuesta de dashboards

### 6.2 Índices en MongoDB
```javascript
db.users.createIndex({ "email": 1 }, { unique: true })
db.projects.createIndex({ "leaderId": 1 })
db.tasks.createIndex({ "projectId": 1 })
db.tasks.createIndex({ "assignedToId": 1 })
```

---

## 7. Gestión de Errores

### 7.1 GlobalExceptionHandler
**Ubicación:** `com.gestionproyectos.gestion_tareas.exception.GlobalExceptionHandler`

**Responsabilidades:**
- Capturar excepciones no controladas
- Retornar vistas de error amigables
- Loggear errores para diagnóstico

**Excepciones Manejadas:**
```java
@ExceptionHandler(IllegalArgumentException.class)
public String handleIllegalArgument(IllegalArgumentException ex, Model model) {
    model.addAttribute("errorMessage", ex.getMessage());
    return "error/400";
}

@ExceptionHandler(AccessDeniedException.class)
public String handleAccessDenied(AccessDeniedException ex, Model model) {
    model.addAttribute("errorMessage", "No tienes permisos para acceder a este recurso");
    return "error/403";
}

@ExceptionHandler(Exception.class)
public String handleGenericException(Exception ex, Model model) {
    log.error("Error no controlado", ex);
    model.addAttribute("errorMessage", "Ha ocurrido un error inesperado");
    return "error/500";
}
```

---

## 8. Referencias

- **Código Fuente:** https://github.com/DeividCardenas/Dise-o_Software
- **Spring Boot Documentation:** https://spring.io/projects/spring-boot
- **Spring Security Documentation:** https://spring.io/projects/spring-security
- **MongoDB Documentation:** https://www.mongodb.com/docs/

---

**Última Actualización:** 2025-11-06
**Responsable:** Equipo de Desarrollo
**Estado:** Documento Aprobado
