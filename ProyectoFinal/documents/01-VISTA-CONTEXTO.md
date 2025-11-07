# Vista de Contexto - Sistema de Gestión de Proyectos y Tareas

**Fecha:** 2025-11-06
**Versión:** 2.0
**Proyecto:** Sistema de Gestión de Proyectos y Tareas

---

## 1. Introducción

### 1.1 Propósito del Documento
Este documento describe la **Vista de Contexto** del Sistema de Gestión de Proyectos y Tareas, mostrando cómo el sistema interactúa con actores externos, sistemas externos y el entorno operativo.

### 1.2 Alcance
La vista de contexto define:
- Los límites del sistema
- Los actores que interactúan con el sistema
- Los sistemas externos con los que se integra
- Los flujos de información principales

---

## 2. Descripción General del Sistema

### 2.1 Propósito del Sistema
El **Sistema de Gestión de Proyectos y Tareas** es una aplicación web colaborativa que permite a organizaciones gestionar proyectos, asignar tareas y realizar seguimiento del progreso de trabajo en equipos.

### 2.2 Objetivos Principales
- Facilitar la creación y gestión de proyectos
- Permitir la asignación de tareas a colaboradores
- Proporcionar seguimiento en tiempo real del progreso
- Implementar control de acceso basado en roles (RBAC)
- Ofrecer visibilidad diferenciada según el rol del usuario

---

## 3. Diagrama de Contexto

```
                                 ┌─────────────────────────────────────┐
                                 │                                     │
                                 │    SISTEMA DE GESTIÓN DE            │
    ┌──────────────┐            │    PROYECTOS Y TAREAS               │            ┌──────────────┐
    │              │            │                                     │            │              │
    │ Administrador│◄──────────►│  - Gestión de usuarios              │◄──────────►│   MongoDB    │
    │              │            │  - Gestión de proyectos             │            │    Atlas     │
    └──────────────┘            │  - Control de acceso                │            │  (Cloud DB)  │
                                 │  - Dashboard y reportes             │            └──────────────┘
    ┌──────────────┐            │  - Autenticación y autorización     │
    │              │            │  - Gestión de tareas                │
    │    Líder     │◄──────────►│                                     │
    │  de Proyecto │            └─────────────────────────────────────┘
    └──────────────┘                          ▲
                                               │
    ┌──────────────┐                          │
    │              │                          │
    │ Colaborador  │◄─────────────────────────┘
    │              │
    └──────────────┘

           ▲
           │
           │
    ┌──────────────┐
    │  Navegador   │
    │     Web      │
    │ (HTTP/HTTPS) │
    └──────────────┘
```

---

## 4. Actores del Sistema

### 4.1 Actores Humanos

#### 4.1.1 Administrador
**Descripción:** Usuario con privilegios completos sobre el sistema.

**Responsabilidades:**
- Crear y gestionar usuarios del sistema
- Crear y gestionar proyectos
- Asignar líderes a proyectos
- Monitorear métricas globales del sistema
- Gestionar configuraciones del sistema

**Interfaces Principales:**
- `/admin/dashboard` - Dashboard de administración
- `/admin/crear-usuario` - Creación de usuarios
- `/admin/usuarios` - Listado de usuarios
- `/admin/crear-proyecto` - Creación de proyectos
- `/admin/proyectos` - Gestión de proyectos

**Información de Entrada:**
- Datos de nuevos usuarios (nombre, email, rol, contraseña)
- Datos de proyectos (nombre, descripción, líder asignado)
- Actualizaciones de usuarios y proyectos

**Información de Salida:**
- Confirmaciones de creación/actualización
- Estadísticas globales del sistema
- Listados de usuarios y proyectos

---

#### 4.1.2 Líder de Proyecto
**Descripción:** Usuario responsable de uno o más proyectos.

**Responsabilidades:**
- Crear tareas dentro de sus proyectos
- Asignar tareas a colaboradores
- Monitorear el progreso de tareas
- Actualizar estado de tareas
- Gestionar colaboradores del proyecto

**Interfaces Principales:**
- `/lider/dashboard` - Dashboard del líder
- `/lider/proyectos` - Listado de proyectos asignados
- `/lider/proyecto/{id}` - Vista detallada del proyecto
- `/lider/proyecto/{id}/nueva-tarea` - Creación de tareas
- `/lider/proyecto/{id}/gestionar-colaboradores` - Gestión de colaboradores

**Información de Entrada:**
- Datos de tareas (título, descripción, prioridad, fecha límite)
- Asignación de responsables
- Actualizaciones de estado de tareas

**Información de Salida:**
- Listado completo de tareas del proyecto
- Estadísticas de progreso del proyecto
- Información de colaboradores
- Confirmaciones de creación/actualización

---

#### 4.1.3 Colaborador
**Descripción:** Usuario que ejecuta tareas asignadas.

**Responsabilidades:**
- Visualizar tareas asignadas
- Actualizar estado de sus tareas
- Reportar progreso
- Completar tareas

**Interfaces Principales:**
- `/mis-tareas` - Listado de tareas asignadas
- `/tareas/{id}/detalle` - Detalle de tarea específica
- `/tareas/{id}/actualizar-estado` - Actualización de estado

**Información de Entrada:**
- Cambios de estado de tareas
- Comentarios y observaciones

**Información de Salida:**
- Listado de tareas asignadas únicamente a él
- Detalles de cada tarea
- Confirmaciones de actualización de estado

---

### 4.2 Sistemas Externos

#### 4.2.1 MongoDB Atlas (Base de Datos Cloud)
**Tipo:** Sistema de almacenamiento NoSQL en la nube

**Responsabilidades:**
- Almacenar usuarios, proyectos y tareas
- Proporcionar persistencia de datos
- Asegurar disponibilidad y replicación
- Manejar consultas y actualizaciones

**Protocolo de Comunicación:**
- MongoDB Wire Protocol
- Conexión SRV (DNS Seedlist)
- SSL/TLS para seguridad

**Datos Intercambiados:**
- **Entrada:**
  - Operaciones CRUD de usuarios
  - Operaciones CRUD de proyectos
  - Operaciones CRUD de tareas
  - Consultas de búsqueda y filtrado
- **Salida:**
  - Documentos de usuarios, proyectos y tareas
  - Resultados de consultas
  - Confirmaciones de escritura

**URI de Conexión:**
```
mongodb+srv://<usuario>:<password>@<cluster>.mongodb.net/<database>
```

---

#### 4.2.2 Navegador Web (Cliente HTTP)
**Tipo:** Cliente de interfaz de usuario

**Responsabilidades:**
- Renderizar interfaz de usuario
- Gestionar sesiones de usuario
- Enviar peticiones HTTP/HTTPS
- Ejecutar JavaScript del lado del cliente

**Protocolo de Comunicación:**
- HTTP/HTTPS
- WebSockets (futuro, para notificaciones en tiempo real)

**Tecnologías:**
- HTML5
- CSS3 (Tailwind CSS)
- JavaScript (Vanilla JS)
- Thymeleaf (renderizado del lado del servidor)

---

## 5. Límites del Sistema

### 5.1 Dentro del Sistema
El sistema gestiona:
- Autenticación y autorización de usuarios
- Gestión completa de usuarios (CRUD)
- Gestión completa de proyectos (CRUD)
- Gestión completa de tareas (CRUD)
- Control de acceso basado en roles (RBAC)
- Persistencia de datos
- Generación de vistas según rol
- Validación de datos de entrada
- Encriptación de contraseñas
- Rate limiting en endpoints críticos
- Caché de consultas frecuentes

### 5.2 Fuera del Sistema
El sistema **NO** gestiona:
- Infraestructura de MongoDB (gestionada por MongoDB Atlas)
- Notificaciones por email (no implementado)
- Integraciones con sistemas de terceros (Slack, Jira, etc.)
- Almacenamiento de archivos adjuntos
- Chat en tiempo real
- Videoconferencias
- Integración con calendarios externos
- Sincronización con dispositivos móviles nativos

---

## 6. Flujos de Información Principales

### 6.1 Flujo de Autenticación
```
Usuario (Navegador) → [POST /login] → Spring Security → UserDetailsService
                                                             ↓
MongoDB Atlas ← UserRepository ← CustomUserDetailsService ←┘
      ↓
[Validación de credenciales]
      ↓
SessionManager → Cookie de Sesión → Usuario (Navegador)
```

**Descripción:**
1. Usuario ingresa credenciales (email y contraseña)
2. Spring Security intercepta la petición
3. CustomUserDetailsService busca el usuario en MongoDB
4. Se valida la contraseña con BCrypt
5. Si es válido, se crea una sesión
6. Se redirige al dashboard según el rol del usuario

---

### 6.2 Flujo de Creación de Proyecto (Admin)
```
Administrador → [GET /admin/crear-proyecto] → AdminProjectController
                                                        ↓
                                              UserService.getAvailableLideres()
                                                        ↓
                                              [Formulario con lista de líderes]
                                                        ↓
Administrador → [POST /admin/crear-proyecto] → ProjectService.createProject()
                                                        ↓
                                              ProjectRepository.save()
                                                        ↓
                                              MongoDB Atlas
```

**Descripción:**
1. Admin solicita formulario de creación
2. Sistema carga lista de usuarios con rol LIDER
3. Admin completa datos del proyecto y selecciona líder
4. Sistema valida datos
5. Se persiste en MongoDB
6. Se confirma creación al admin

---

### 6.3 Flujo de Creación de Tarea (Líder)
```
Líder → [GET /lider/proyecto/{id}/nueva-tarea] → LiderController
                                                        ↓
                                        [Validar que líder pertenece al proyecto]
                                                        ↓
                                        UserService.findCollaborators()
                                                        ↓
                                        [Formulario con colaboradores]
                                                        ↓
Líder → [POST /lider/proyecto/{id}/nueva-tarea] → TaskService.createTask()
                                                        ↓
                                                TaskRepository.save()
                                                        ↓
                                                MongoDB Atlas
```

**Descripción:**
1. Líder solicita formulario de creación de tarea
2. Sistema valida que el líder tiene acceso al proyecto
3. Se carga lista de colaboradores disponibles
4. Líder completa datos y asigna responsable
5. Sistema valida y persiste en MongoDB
6. Se confirma creación al líder

---

### 6.4 Flujo de Actualización de Estado de Tarea (Colaborador)
```
Colaborador → [GET /mis-tareas] → DashboardController
                                        ↓
                        TaskService.getTasksForUser(userId)
                                        ↓
                        TaskRepository.findByAssignedToId()
                                        ↓
                        [Lista de tareas filtradas]
                                        ↓
Colaborador → [POST /tareas/{id}/actualizar-estado] → TaskController
                                                            ↓
                                        [Validar permisos y transición]
                                                            ↓
                                        TaskService.updateTaskStatus()
                                                            ↓
                                        TaskRepository.save()
                                                            ↓
                                        MongoDB Atlas
```

**Descripción:**
1. Colaborador visualiza sus tareas asignadas
2. Sistema filtra tareas por assignedToId
3. Colaborador selecciona una tarea y cambia su estado
4. Sistema valida que el colaborador es el responsable
5. Sistema valida que la transición de estado es permitida
6. Se actualiza en MongoDB
7. Se confirma actualización al colaborador

---

## 7. Dependencias Externas

### 7.1 Infraestructura de Deployment
**Plataforma:** Railway (Platform as a Service)

**Responsabilidades:**
- Hosting de la aplicación Spring Boot
- Gestión de recursos (CPU, RAM)
- Certificados SSL/TLS
- DNS y balanceo de carga
- Logs y monitoreo básico

**URL de Producción:**
```
https://[app-name].railway.app
```

---

### 7.2 Dependencias de Software

#### Backend (Java)
- **Java 21** - Runtime de ejecución
- **Spring Boot 3.3.5** - Framework principal
- **Spring Security 6** - Autenticación y autorización
- **Spring Data MongoDB** - ORM para MongoDB
- **Caffeine Cache** - Caché en memoria

#### Frontend
- **Thymeleaf 3** - Motor de plantillas
- **Tailwind CSS 3.4** - Framework de estilos
- **Node.js 20.10** - Compilación de CSS

#### Base de Datos
- **MongoDB Atlas** - Base de datos NoSQL en la nube

---

## 8. Restricciones y Supuestos

### 8.1 Restricciones Técnicas
- El sistema requiere conexión a internet para acceder a MongoDB Atlas
- Navegador con soporte para JavaScript ES6+
- Conexión HTTPS obligatoria en producción
- Sesiones basadas en cookies (no JWT)

### 8.2 Restricciones de Negocio
- Solo el administrador puede crear usuarios
- Un proyecto solo puede tener un líder asignado
- Las tareas solo pueden ser actualizadas por su responsable (o superiores)
- Las transiciones de estado de tareas tienen reglas específicas

### 8.3 Supuestos
- Los usuarios tienen acceso a un navegador web moderno
- MongoDB Atlas está siempre disponible (99.9% SLA)
- Los usuarios tienen credenciales únicas (email)
- Un colaborador puede estar asignado a múltiples proyectos

---

## 9. Atributos de Calidad Relevantes al Contexto

### 9.1 Disponibilidad
- **Objetivo:** 99% de uptime
- **Dependencia:** MongoDB Atlas (99.9% SLA) y Railway (99.9% SLA)
- **Riesgo:** Caídas de servicios externos

### 9.2 Seguridad
- **Autenticación:** Spring Security con sesiones
- **Autorización:** RBAC con 3 roles
- **Encriptación:** BCrypt para contraseñas
- **Protección:** CSRF tokens, rate limiting en login
- **Transporte:** HTTPS obligatorio en producción

### 9.3 Rendimiento
- **Caché:** Caffeine Cache reduce consultas a BD en 60-70%
- **Optimización:** Índices en MongoDB para consultas frecuentes
- **Rate Limiting:** Protección contra fuerza bruta en login

### 9.4 Escalabilidad
- **Horizontal:** Railway permite escalar instancias
- **Base de Datos:** MongoDB Atlas soporta sharding
- **Limitación:** Sesiones en memoria (considerar Redis para multi-instancia)

---

## 10. Evolución Futura

### 10.1 Integraciones Planificadas
- **Notificaciones por Email** (SendGrid/AWS SES)
- **Autenticación OAuth** (Google, Microsoft)
- **API REST** para aplicaciones móviles
- **WebSockets** para notificaciones en tiempo real
- **Integración con Slack** para notificaciones

### 10.2 Sistemas Externos Futuros
- **S3/Cloud Storage** para adjuntos de tareas
- **Servicio de Analytics** (Google Analytics, Mixpanel)
- **Servicio de Logs** (Datadog, Loggly)
- **Servicio de Métricas** (Prometheus, Grafana)

---

## 11. Diagrama de Deployment

```
┌────────────────────────────────────────────────────────────────┐
│                          INTERNET                               │
└────────────────────┬───────────────────────────────────────────┘
                     │ HTTPS
                     │
         ┌───────────▼──────────────┐
         │  Railway (Cloud PaaS)    │
         │  ┌────────────────────┐  │
         │  │  Spring Boot App   │  │
         │  │  - Tomcat Server   │  │
         │  │  - Java 21 Runtime │  │
         │  │  - Port 8080       │  │
         │  └────────┬───────────┘  │
         │           │              │
         │  ┌────────▼───────────┐  │
         │  │  Caffeine Cache    │  │
         │  │  (In-Memory)       │  │
         │  └────────────────────┘  │
         └───────────┬──────────────┘
                     │ MongoDB Wire Protocol
                     │ SSL/TLS
         ┌───────────▼──────────────┐
         │  MongoDB Atlas (Cloud)   │
         │  ┌────────────────────┐  │
         │  │  Cluster M0/M10    │  │
         │  │  - Replica Set     │  │
         │  │  - 3 Nodes         │  │
         │  │  - Auto Backup     │  │
         │  └────────────────────┘  │
         └──────────────────────────┘
```

---

## 12. Referencias

- **Código Fuente:** https://github.com/DeividCardenas/Dise-o_Software
- **Documentación Técnica:** Ver `/FUNCIONALIDADES.md`
- **Guía de Deployment:** Ver `/RAILWAY_DEPLOYMENT.md`
- **Configuración MongoDB:** Ver `/MONGODB_ATLAS_CONFIG.md`

---

**Última Actualización:** 2025-11-06
**Responsable:** Equipo de Desarrollo
**Estado:** Documento Aprobado
