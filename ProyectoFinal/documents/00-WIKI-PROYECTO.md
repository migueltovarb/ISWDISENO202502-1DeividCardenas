# Wiki del Proyecto - Sistema de Gestión de Proyectos y Tareas

**Fecha:** 2025-11-06
**Versión:** 2.0
**Proyecto:** Sistema de Gestión de Proyectos y Tareas

---

## 📋 Tabla de Contenidos

1. [Información General](#1-información-general)
2. [Inicio Rápido](#2-inicio-rápido)
3. [Arquitectura del Sistema](#3-arquitectura-del-sistema)
4. [Guías de Desarrollo](#4-guías-de-desarrollo)
5. [Deployment y Operaciones](#5-deployment-y-operaciones)
6. [Documentación de API](#6-documentación-de-api)
7. [Testing](#7-testing)
8. [FAQ - Preguntas Frecuentes](#8-faq---preguntas-frecuentes)
9. [Troubleshooting](#9-troubleshooting)
10. [Contribución](#10-contribución)

---

## 1. Información General

### 1.1 ¿Qué es este Proyecto?

El **Sistema de Gestión de Proyectos y Tareas** es una aplicación web empresarial que permite a organizaciones gestionar proyectos, asignar tareas y realizar seguimiento del progreso de trabajo en equipos.

### 1.2 Características Principales

- ✅ **Control de Acceso Basado en Roles (RBAC)**
  - 3 roles: Administrador, Líder, Colaborador
  - Permisos diferenciados por rol

- ✅ **Gestión de Proyectos**
  - Crear y gestionar proyectos
  - Asignar líderes y colaboradores
  - Seguimiento de progreso

- ✅ **Gestión de Tareas**
  - Crear tareas con prioridades
  - Asignar a colaboradores
  - Seguimiento de estado con workflow definido

- ✅ **Dashboards Personalizados**
  - Dashboard de Admin con métricas globales
  - Dashboard de Líder con sus proyectos
  - Vista de colaborador con sus tareas

- ✅ **Seguridad Robusta**
  - Autenticación con Spring Security
  - Contraseñas encriptadas con BCrypt
  - Rate limiting en login
  - Protección CSRF

- ✅ **Alto Rendimiento**
  - Caché en memoria con Caffeine
  - 60-70% mejora en tiempo de respuesta
  - Índices optimizados en MongoDB

### 1.3 Tecnologías Utilizadas

#### Backend
- **Java 21** - Lenguaje de programación
- **Spring Boot 3.3.5** - Framework MVC
- **Spring Security 6** - Autenticación y autorización
- **Spring Data MongoDB** - ORM para MongoDB
- **Caffeine Cache** - Caché en memoria
- **Lombok** - Reducción de boilerplate
- **Jakarta Validation** - Validación de datos

#### Frontend
- **Thymeleaf 3** - Motor de plantillas del servidor
- **Tailwind CSS 3.4** - Framework de estilos
- **JavaScript Vanilla** - Interactividad del cliente

#### Base de Datos
- **MongoDB Atlas** - Base de datos NoSQL en la nube

#### DevOps
- **Maven 3** - Gestión de dependencias
- **Railway** - Platform as a Service (deployment)
- **Git/GitHub** - Control de versiones

---

## 2. Inicio Rápido

### 2.1 Requisitos Previos

- **Java 21** o superior
- **Maven 3.8+** o usar el wrapper incluido (`mvnw`)
- **Node.js 20+** (para compilación de Tailwind CSS)
- **MongoDB Atlas** (o MongoDB local)
- **Git** para control de versiones

### 2.2 Instalación Local

#### Paso 1: Clonar el Repositorio
```bash
git clone https://github.com/DeividCardenas/Dise-o_Software.git
cd Dise-o_Software
```

#### Paso 2: Configurar MongoDB
Crea una cuenta en [MongoDB Atlas](https://www.mongodb.com/cloud/atlas) y:

1. Crea un cluster (gratis con M0)
2. Configura network access (permite tu IP o `0.0.0.0/0` para desarrollo)
3. Crea un usuario de base de datos
4. Obtén la URI de conexión SRV

**Ver guía completa:** `/SETUP_MONGODB_ATLAS_PASO_A_PASO.md`

#### Paso 3: Configurar Variables de Entorno
Edita `src/main/resources/application.properties`:

```properties
# MongoDB Atlas
spring.data.mongodb.uri=mongodb+srv://usuario:password@cluster.mongodb.net/gestion_tareas

# Server
server.port=8080

# Logging
logging.level.com.gestionproyectos=DEBUG
```

#### Paso 4: Compilar Tailwind CSS
```bash
# Instalar dependencias
npm install

# Compilar CSS (watch mode)
npm run build:css

# O compilar una sola vez
npx tailwindcss -i ./src/main/resources/static/css/input.css \
                -o ./src/main/resources/static/css/tailwind.min.css --minify
```

#### Paso 5: Ejecutar la Aplicación
```bash
# Usando Maven wrapper
./mvnw spring-boot:run

# O usando Maven instalado
mvn spring-boot:run
```

#### Paso 6: Acceder al Sistema
Abre tu navegador en: http://localhost:8080

**Credenciales por defecto:**
```
Administrador:
Email: admin@gestion.com
Password: admin123
```

### 2.3 Usuarios de Prueba

Al iniciar con base de datos vacía, el sistema crea automáticamente:

| Rol | Email | Contraseña |
|-----|-------|------------|
| ADMIN | admin@gestion.com | admin123 |
| LIDER | juan.perez@gestion.com | lider123 |
| COLABORADOR | maria.gonzalez@gestion.com | colab123 |
| COLABORADOR | carlos.rodriguez@gestion.com | colab123 |

---

## 3. Arquitectura del Sistema

### 3.1 Visión General

El sistema sigue una arquitectura en capas (Layered Architecture) con patrón MVC:

```
┌─────────────────────────────────────┐
│  CAPA DE PRESENTACIÓN (Thymeleaf)   │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│  CAPA DE CONTROLADORES (MVC)        │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│  CAPA DE SEGURIDAD (Spring Security)│
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│  CAPA DE SERVICIOS (Lógica Negocio) │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│  CAPA DE REPOSITORIOS (Spring Data) │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│  CAPA DE PERSISTENCIA (MongoDB)     │
└─────────────────────────────────────┘
```

**Ver documentación completa:**
- [Vista de Contexto](/documents/01-VISTA-CONTEXTO.md)
- [Vista Funcional](/documents/02-VISTA-FUNCIONAL.md)
- [Vista Conceptual](/documents/03-VISTA-CONCEPTUAL.md)

### 3.2 Estructura de Directorios

```
Dise-o_Software/
├── src/main/java/com/gestionproyectos/gestion_tareas/
│   ├── config/              # Configuración (Security, DataSeeder)
│   ├── controller/          # Controladores MVC (8)
│   ├── service/             # Servicios de lógica de negocio (6)
│   ├── repository/          # Repositorios Spring Data (3)
│   ├── model/               # Entidades MongoDB (3)
│   ├── dto/                 # Data Transfer Objects (15+)
│   ├── mapper/              # Conversores Entity ↔ DTO (3)
│   ├── enums/               # Enumeraciones (4)
│   ├── exception/           # Manejo de excepciones
│   └── security/            # Filtros de seguridad
│
├── src/main/resources/
│   ├── application.properties       # Configuración principal
│   ├── application-production.properties
│   ├── templates/                   # Vistas Thymeleaf (29)
│   │   ├── layouts/
│   │   ├── fragments/
│   │   ├── auth/
│   │   ├── admin/
│   │   ├── lider/
│   │   ├── colaborador/
│   │   └── tareas/
│   └── static/
│       ├── css/
│       └── js/
│
├── documents/               # Documentación arquitectónica
├── pom.xml                 # Dependencias Maven
├── package.json            # Dependencias Node (Tailwind)
├── tailwind.config.js      # Configuración Tailwind
└── README.md
```

### 3.3 Componentes Clave

#### Controladores (Controllers)
| Controlador | Responsabilidad |
|-------------|-----------------|
| `AuthController` | Login, registro, logout |
| `AdminDashboardController` | Dashboard de administrador |
| `AdminUserController` | CRUD de usuarios |
| `AdminProjectController` | CRUD de proyectos (admin) |
| `LiderController` | Gestión de proyectos y tareas (líder) |
| `TaskController` | Gestión de tareas |
| `DashboardController` | Dashboard general y redirección |
| `HealthCheckController` | Health check endpoint |

#### Servicios (Services)
| Servicio | Responsabilidad |
|----------|-----------------|
| `UserService` | Lógica de usuarios |
| `ProjectService` | Lógica de proyectos |
| `TaskService` | Lógica de tareas |
| `CustomUserDetailsService` | Autenticación Spring Security |
| `MetricsService` | Cálculo de métricas |
| `RateLimitService` | Rate limiting |

#### Entidades (Models)
| Entidad | Colección MongoDB | Descripción |
|---------|-------------------|-------------|
| `User` | `users` | Usuarios del sistema |
| `Project` | `projects` | Proyectos |
| `Task` | `tasks` | Tareas |

---

## 4. Guías de Desarrollo

### 4.1 Convenciones de Código

#### Nombrado de Clases
- **Controllers:** Terminar en `Controller` (ej: `AuthController`)
- **Services:** Terminar en `Service` (ej: `UserService`)
- **Repositories:** Terminar en `Repository` (ej: `UserRepository`)
- **DTOs:** Terminar en `Dto` (ej: `UserRegistrationDto`)
- **Mappers:** Terminar en `Mapper` (ej: `UserMapper`)

#### Nombrado de Métodos
- **Servicios:**
  - `create*()` - Crear entidad
  - `update*()` - Actualizar entidad
  - `get*()` - Obtener entidad
  - `find*()` - Buscar entidades
  - `delete*()` - Eliminar entidad

- **Controladores:**
  - `show*Form()` - Mostrar formulario (GET)
  - `create*()` - Procesar creación (POST)
  - `update*()` - Procesar actualización (POST)

#### Estructura de Paquetes
```java
package com.gestionproyectos.gestion_tareas.controller;
package com.gestionproyectos.gestion_tareas.service;
package com.gestionproyectos.gestion_tareas.repository;
package com.gestionproyectos.gestion_tareas.model;
package com.gestionproyectos.gestion_tareas.dto;
```

### 4.2 Agregar una Nueva Funcionalidad

#### Ejemplo: Agregar "Comentarios en Tareas"

**Paso 1: Crear Entidad**
```java
@Document(collection = "task_comments")
public class TaskComment {
    @Id
    private String id;
    private String taskId;
    private String userId;
    private String content;
    private LocalDateTime createdAt;
    // getters, setters, constructors
}
```

**Paso 2: Crear Repositorio**
```java
public interface TaskCommentRepository extends MongoRepository<TaskComment, String> {
    List<TaskComment> findByTaskId(String taskId);
}
```

**Paso 3: Crear DTO**
```java
public record TaskCommentDto(
    String content,
    String taskId
) { }
```

**Paso 4: Crear Servicio**
```java
@Service
public class TaskCommentService {
    private final TaskCommentRepository repository;

    public TaskComment addComment(TaskCommentDto dto, String userId) {
        TaskComment comment = new TaskComment();
        comment.setTaskId(dto.taskId());
        comment.setUserId(userId);
        comment.setContent(dto.content());
        comment.setCreatedAt(LocalDateTime.now());
        return repository.save(comment);
    }
}
```

**Paso 5: Agregar al Controlador**
```java
@PostMapping("/tareas/{taskId}/comentarios")
public String addComment(@PathVariable String taskId,
                          @ModelAttribute TaskCommentDto dto) {
    taskCommentService.addComment(dto, getCurrentUserId());
    return "redirect:/tareas/" + taskId;
}
```

**Paso 6: Crear Vista**
Agregar formulario en `tareas/detalle.html`

### 4.3 Logging

```java
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {
    public User createUser(UserDto dto) {
        log.info("Creando usuario: {}", dto.email());
        try {
            // lógica
            log.debug("Usuario creado exitosamente: {}", user.getId());
            return user;
        } catch (Exception e) {
            log.error("Error al crear usuario: {}", e.getMessage(), e);
            throw e;
        }
    }
}
```

**Niveles de Log:**
- `ERROR` - Errores que afectan funcionalidad
- `WARN` - Advertencias
- `INFO` - Información importante
- `DEBUG` - Información de depuración (solo desarrollo)

### 4.4 Manejo de Errores

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException ex, Model model) {
        log.error("Error de validación: {}", ex.getMessage());
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/400";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied(AccessDeniedException ex, Model model) {
        model.addAttribute("errorMessage", "No tienes permisos");
        return "error/403";
    }
}
```

---

## 5. Deployment y Operaciones

### 5.1 Deployment en Railway

**Ver guía completa:** `/RAILWAY_DEPLOYMENT.md`

#### Pasos Resumidos:

1. **Crear cuenta en Railway:** https://railway.app
2. **Conectar repositorio GitHub**
3. **Configurar variables de entorno:**
   ```
   SPRING_DATA_MONGODB_URI=mongodb+srv://...
   SPRING_PROFILES_ACTIVE=production
   ```
4. **Deploy automático** con cada push a `main`

### 5.2 Variables de Entorno

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `SPRING_DATA_MONGODB_URI` | URI de MongoDB Atlas | `mongodb+srv://user:pass@cluster.mongodb.net/db` |
| `SPRING_PROFILES_ACTIVE` | Perfil activo | `production` |
| `SERVER_PORT` | Puerto del servidor | `8080` |

### 5.3 Monitoreo

#### Health Check
```bash
curl https://tu-app.railway.app/health
```

Respuesta esperada:
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

#### Logs
```bash
# En Railway dashboard → Deployments → View logs
```

---

## 6. Documentación de API

### 6.1 Endpoints Públicos

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/login` | Formulario de login |
| POST | `/login` | Procesar login (Spring Security) |
| GET | `/register` | Formulario de registro |
| POST | `/register` | Crear cuenta de colaborador |
| GET | `/health` | Health check |

### 6.2 Endpoints de Administrador

**Autenticación requerida: ADMIN**

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/admin/dashboard` | Dashboard de admin |
| GET | `/admin/usuarios` | Listado de usuarios |
| GET | `/admin/crear-usuario` | Formulario de creación |
| POST | `/admin/crear-usuario` | Crear usuario |
| GET | `/admin/editar-usuario/{id}` | Formulario de edición |
| POST | `/admin/editar-usuario/{id}` | Actualizar usuario |
| GET | `/admin/proyectos` | Listado de proyectos |
| GET | `/admin/crear-proyecto` | Formulario de creación |
| POST | `/admin/crear-proyecto` | Crear proyecto |

### 6.3 Endpoints de Líder

**Autenticación requerida: LIDER o ADMIN**

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/lider/dashboard` | Dashboard del líder |
| GET | `/lider/proyectos` | Proyectos asignados |
| GET | `/lider/proyecto/{id}` | Vista detallada del proyecto |
| GET | `/lider/proyecto/{id}/nueva-tarea` | Formulario de creación de tarea |
| POST | `/lider/proyecto/{id}/nueva-tarea` | Crear tarea |
| GET | `/lider/proyecto/{id}/gestionar-colaboradores` | Gestionar colaboradores |

### 6.4 Endpoints de Colaborador

**Autenticación requerida: Cualquier usuario**

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/mis-tareas` | Tareas asignadas al usuario |
| GET | `/tareas/{id}/detalle` | Detalle de tarea |
| POST | `/tareas/{id}/actualizar-estado` | Actualizar estado de tarea |

---

## 7. Testing

### 7.1 Ejecutar Tests

```bash
# Todos los tests
./mvnw test

# Tests específicos
./mvnw test -Dtest=UserServiceTest

# Con coverage
./mvnw test jacoco:report
```

### 7.2 Tests Manuales

**Ver guía completa:** `/GUIA_PRUEBAS.md`

#### Checklist de Testing

**Autenticación:**
- [ ] Login con credenciales correctas
- [ ] Login con credenciales incorrectas
- [ ] Logout exitoso
- [ ] Redirección según rol

**Admin:**
- [ ] Crear usuario ADMIN, LIDER, COLABORADOR
- [ ] Editar usuario
- [ ] Desactivar usuario
- [ ] Crear proyecto
- [ ] Asignar líder a proyecto

**Líder:**
- [ ] Ver mis proyectos
- [ ] Crear tarea en proyecto
- [ ] Asignar tarea a colaborador
- [ ] Ver todas las tareas del proyecto
- [ ] Gestionar colaboradores

**Colaborador:**
- [ ] Ver mis tareas (solo asignadas a mí)
- [ ] Cambiar estado de tarea (transiciones válidas)
- [ ] Marcar tarea como completada
- [ ] No puedo ver tareas de otros

---

## 8. FAQ - Preguntas Frecuentes

### 8.1 ¿Cómo cambio mi contraseña?

Actualmente no hay interfaz para cambiar contraseña. Puedes:

1. **Opción 1:** Pedir al admin que te edite y asigne nueva contraseña
2. **Opción 2:** (Futuro) Funcionalidad de "Recuperar contraseña"

### 8.2 ¿Puedo eliminar un proyecto?

Sí, pero:
- Solo ADMIN puede eliminar proyectos
- Al eliminar un proyecto, se eliminan todas sus tareas
- Considera usar soft delete (desactivar en lugar de eliminar)

### 8.3 ¿Cómo agrego un colaborador a mi proyecto?

1. Como LIDER, ve a tu proyecto
2. Click en "Gestionar Colaboradores"
3. Selecciona el colaborador del dropdown
4. Click en "Agregar"

### 8.4 ¿Por qué no veo todas las tareas en "Mis Tareas"?

La vista "Mis Tareas" (`/mis-tareas`) **solo muestra tareas asignadas a ti**. Esto es por diseño:

- **Colaborador:** Solo ve sus tareas asignadas
- **Líder:** Para ver todas las tareas, ve a "Ver Proyecto"
- **Admin:** Puede ver todas las tareas desde el panel de admin

### 8.5 ¿Puedo cambiar una tarea de PENDIENTE a COMPLETADA directamente?

No, las transiciones de estado son:

```
PENDIENTE → EN_PROGRESO
EN_PROGRESO → EN_REVISION | BLOQUEADA | COMPLETADA
EN_REVISION → EN_PROGRESO | COMPLETADA
BLOQUEADA → EN_PROGRESO
COMPLETADA → (final, no cambios)
```

Esto asegura un workflow consistente.

### 8.6 ¿Cómo escalo horizontalmente (múltiples instancias)?

Actualmente el sistema usa sesiones en memoria, lo que dificulta escalar. Opciones:

1. **Corto plazo:** Sticky sessions en el balanceador
2. **Largo plazo:** Migrar sesiones a Redis
3. **Alternativa:** Cambiar a JWT stateless

---

## 9. Troubleshooting

### 9.1 Error: "Could not connect to MongoDB"

**Síntomas:**
```
com.mongodb.MongoTimeoutException: Timed out after 30000 ms while waiting to connect
```

**Soluciones:**
1. Verificar URI de MongoDB en `application.properties`
2. Verificar que tu IP esté en la whitelist de MongoDB Atlas
3. Verificar usuario y contraseña correctos
4. Verificar que el cluster esté activo

**Ver:** `/MONGODB_ATLAS_CONFIG.md`

### 9.2 Error: "Access Denied" (403)

**Síntomas:**
Usuario intenta acceder a recurso y recibe error 403.

**Soluciones:**
1. Verificar que el usuario tenga el rol correcto
2. Verificar que la ruta esté configurada correctamente en `SecurityConfig`
3. Verificar anotación `@PreAuthorize` en el controlador

**Ejemplo:**
```java
// En SecurityConfig
.requestMatchers("/admin/**").hasRole("ADMIN")

// En Controller
@PreAuthorize("hasRole('ADMIN')")
```

### 9.3 Error: "Too Many Requests" (429)

**Síntomas:**
Después de varios intentos de login fallidos, se bloquea el acceso.

**Causa:**
Rate limiting activado (5 intentos en 15 minutos).

**Solución:**
- Esperar 30 minutos
- O cambiar de IP (si estás en desarrollo)
- O reiniciar la aplicación (limpia el caché en memoria)

### 9.4 CSS no se carga / Estilos rotos

**Síntomas:**
La página se ve sin estilos.

**Soluciones:**
1. Verificar que Tailwind CSS se compiló:
   ```bash
   npm run build:css
   ```
2. Verificar que existe `/static/css/tailwind.min.css`
3. Limpiar caché del navegador (Ctrl+Shift+R)
4. Verificar logs de Spring Boot para errores de recursos estáticos

### 9.5 Usuario admin no se crea automáticamente

**Síntomas:**
Al iniciar la app, no puedo hacer login con `admin@gestion.com`.

**Soluciones:**
1. Verificar que `DataSeeder` se ejecutó:
   - Buscar en logs: "Admin user already exists" o "Creating admin user"
2. Verificar conexión a MongoDB
3. Crear manualmente vía MongoDB Compass:
   ```javascript
   db.users.insertOne({
     fullName: "Administrador",
     email: "admin@gestion.com",
     password: "$2a$12$...", // BCrypt hash de "admin123"
     role: "ADMIN",
     isActive: true,
     createdAt: new Date(),
     updatedAt: new Date()
   })
   ```

---

## 10. Contribución

### 10.1 Cómo Contribuir

1. **Fork el repositorio**
2. **Crea una rama para tu feature:**
   ```bash
   git checkout -b feature/nueva-funcionalidad
   ```
3. **Haz tus cambios y commits:**
   ```bash
   git add .
   git commit -m "feat: Agregar comentarios en tareas"
   ```
4. **Push a tu fork:**
   ```bash
   git push origin feature/nueva-funcionalidad
   ```
5. **Crea un Pull Request** en GitHub

### 10.2 Convenciones de Commits

Usamos [Conventional Commits](https://www.conventionalcommits.org/):

```
feat: Agregar nueva funcionalidad
fix: Corregir un bug
docs: Actualizar documentación
style: Cambios de formato (sin afectar código)
refactor: Refactorización de código
test: Agregar o corregir tests
chore: Tareas de mantenimiento
```

**Ejemplos:**
```
feat: Agregar comentarios en tareas (HU-019)
fix: Corregir validación de email en registro
docs: Actualizar guía de deployment
refactor: Separar lógica de TaskService en métodos más pequeños
```

### 10.3 Code Review

Antes de hacer merge, asegúrate de:

- [ ] El código compila sin errores
- [ ] Los tests pasan (`./mvnw test`)
- [ ] Se agregaron tests para nueva funcionalidad
- [ ] La documentación se actualizó
- [ ] Se siguieron las convenciones de código
- [ ] No hay credenciales hardcodeadas
- [ ] Se agregaron logs apropiados

---

## 11. Recursos Adicionales

### 11.1 Documentación del Proyecto

| Documento | Descripción |
|-----------|-------------|
| [Vista de Contexto](/documents/01-VISTA-CONTEXTO.md) | Límites del sistema, actores, sistemas externos |
| [Vista Funcional](/documents/02-VISTA-FUNCIONAL.md) | Componentes, responsabilidades, flujos |
| [Vista Conceptual](/documents/03-VISTA-CONCEPTUAL.md) | Modelo de dominio, entidades, reglas de negocio |
| [Historias de Usuario](/documents/04-HISTORIAS-USUARIO.md) | Requisitos funcionales desde perspectiva de usuario |
| [Escenarios de Calidad](/documents/05-ESCENARIOS-CALIDAD.md) | Atributos de calidad, tácticas arquitectónicas |
| [User Story Mapping](/documents/06-USER-STORY-MAPPING.md) | Priorización, roadmap, releases |

### 11.2 Guías Técnicas

| Documento | Descripción |
|-----------|-------------|
| [README.md](/README.md) | Información general del proyecto |
| [FUNCIONALIDADES.md](/FUNCIONALIDADES.md) | Funcionalidades implementadas |
| [GUIA_PRUEBAS.md](/GUIA_PRUEBAS.md) | Guía de testing manual |
| [MONGODB_ATLAS_CONFIG.md](/MONGODB_ATLAS_CONFIG.md) | Configuración de MongoDB Atlas |
| [RAILWAY_DEPLOYMENT.md](/RAILWAY_DEPLOYMENT.md) | Deployment en Railway |
| [MEJORAS_IMPLEMENTADAS.md](/MEJORAS_IMPLEMENTADAS.md) | Historial de mejoras |

### 11.3 Enlaces Externos

- **Repositorio GitHub:** https://github.com/DeividCardenas/Dise-o_Software
- **Spring Boot Docs:** https://spring.io/projects/spring-boot
- **Spring Security Docs:** https://spring.io/projects/spring-security
- **MongoDB Docs:** https://www.mongodb.com/docs/
- **Tailwind CSS Docs:** https://tailwindcss.com/docs
- **Thymeleaf Docs:** https://www.thymeleaf.org/documentation.html

### 11.4 Contacto y Soporte

**Equipo de Desarrollo:**
- GitHub Issues: https://github.com/DeividCardenas/Dise-o_Software/issues

---

**Última Actualización:** 2025-11-06
**Mantenida por:** Equipo de Desarrollo
**Estado:** Documento Vivo
