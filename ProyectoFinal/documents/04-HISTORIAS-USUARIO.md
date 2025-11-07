# Historias de Usuario - Sistema de Gestión de Proyectos y Tareas

**Fecha:** 2025-11-06
**Versión:** 2.0
**Proyecto:** Sistema de Gestión de Proyectos y Tareas

---

## 1. Introducción

### 1.1 Propósito del Documento
Este documento describe las **Historias de Usuario** del sistema, especificando los requisitos funcionales desde la perspectiva de los usuarios finales.

### 1.2 Formato de Historias de Usuario
Cada historia sigue el formato estándar:

```
Como [tipo de usuario]
Quiero [objetivo/funcionalidad]
Para [beneficio/razón]
```

### 1.3 Criterios de Aceptación
Cada historia incluye:
- Criterios de aceptación específicos y medibles
- Escenarios de prueba
- Reglas de negocio relacionadas

---

## 2. Épicas del Sistema

### Épica 1: Gestión de Autenticación y Usuarios
Permitir que los usuarios accedan al sistema de forma segura y que los administradores gestionen cuentas de usuario.

### Épica 2: Gestión de Proyectos
Permitir la creación, edición y seguimiento de proyectos con asignación de líderes.

### Épica 3: Gestión de Tareas
Permitir la creación, asignación y seguimiento de tareas dentro de proyectos.

### Épica 4: Dashboards y Reportes
Proporcionar vistas personalizadas según el rol del usuario con métricas relevantes.

---

## 3. Historias de Usuario por Épica

---

## ÉPICA 1: Gestión de Autenticación y Usuarios

---

### HU-001: Login de Usuarios

**Como** usuario del sistema
**Quiero** iniciar sesión con mi email y contraseña
**Para** acceder a las funcionalidades según mi rol

#### Criterios de Aceptación

1. **Formulario de Login**
   - ✅ El sistema muestra un formulario con campos de email y contraseña
   - ✅ Los campos son obligatorios
   - ✅ Existe un botón "Iniciar Sesión"

2. **Validación de Credenciales**
   - ✅ Si las credenciales son correctas, el sistema autentica al usuario
   - ✅ Si las credenciales son incorrectas, muestra mensaje de error
   - ✅ Las contraseñas se validan contra valores encriptados (BCrypt)

3. **Redirección Según Rol**
   - ✅ Si el usuario es ADMIN → redirige a `/admin/dashboard`
   - ✅ Si el usuario es LIDER → redirige a `/lider/dashboard`
   - ✅ Si el usuario es COLABORADOR → redirige a `/mis-tareas`

4. **Seguridad**
   - ✅ Protección CSRF habilitada
   - ✅ Rate limiting: máximo 5 intentos por IP en 15 minutos
   - ✅ Bloqueo temporal de IP después de exceder el límite

#### Escenarios de Prueba

**Escenario 1: Login Exitoso - Admin**
```gherkin
Dado que soy un usuario con email "admin@gestion.com"
Cuando ingreso mi contraseña correcta "admin123"
Y hago clic en "Iniciar Sesión"
Entonces soy redirigido a "/admin/dashboard"
Y veo el mensaje "Bienvenido, Administrador"
```

**Escenario 2: Login Fallido - Credenciales Incorrectas**
```gherkin
Dado que ingreso email "admin@gestion.com"
Cuando ingreso una contraseña incorrecta "wrongpassword"
Y hago clic en "Iniciar Sesión"
Entonces permanezco en la página de login
Y veo el mensaje "Email o contraseña incorrectos"
```

**Escenario 3: Rate Limiting**
```gherkin
Dado que he fallado 5 intentos de login desde mi IP
Cuando intento iniciar sesión nuevamente
Entonces veo el mensaje "Demasiados intentos. Intente de nuevo en 30 minutos"
Y mi IP es bloqueada temporalmente
```

#### Reglas de Negocio
- **RN-001:** Las contraseñas se almacenan encriptadas con BCrypt (strength 12)
- **RN-002:** Las sesiones expiran después de 30 minutos de inactividad
- **RN-003:** Solo usuarios activos (`isActive = true`) pueden iniciar sesión

#### Implementación
- **Controlador:** `AuthController.java:82-110`
- **Vista:** `auth/login.html`
- **Servicio:** Spring Security + `CustomUserDetailsService`
- **Configuración:** `SecurityConfig.java`

#### Estado
✅ **COMPLETADO** - Implementado y probado

---

### HU-002: Registro Público de Colaboradores

**Como** visitante del sitio
**Quiero** registrarme para obtener una cuenta de colaborador
**Para** poder ser asignado a proyectos y tareas

#### Criterios de Aceptación

1. **Formulario de Registro**
   - ✅ El sistema muestra un formulario público en `/register`
   - ✅ Campos: nombre completo, email, contraseña, confirmar contraseña
   - ✅ Todos los campos son obligatorios

2. **Validación de Datos**
   - ✅ Email debe tener formato válido
   - ✅ Email debe ser único (no existir en el sistema)
   - ✅ Contraseña mínima de 6 caracteres
   - ✅ Las contraseñas deben coincidir

3. **Creación de Cuenta**
   - ✅ El sistema crea un usuario con rol COLABORADOR
   - ✅ La contraseña se encripta con BCrypt
   - ✅ El usuario queda activo por defecto
   - ✅ Redirige al login con mensaje de éxito

#### Escenarios de Prueba

**Escenario 1: Registro Exitoso**
```gherkin
Dado que visito "/register"
Cuando ingreso nombre "Juan Pérez"
Y ingreso email "juan.perez@ejemplo.com"
Y ingreso contraseña "password123"
Y confirmo contraseña "password123"
Y hago clic en "Registrarse"
Entonces se crea mi cuenta como COLABORADOR
Y soy redirigido a "/login"
Y veo el mensaje "Registro exitoso. Inicia sesión para continuar"
```

**Escenario 2: Email Duplicado**
```gherkin
Dado que existe un usuario con email "juan@ejemplo.com"
Cuando intento registrarme con el mismo email
Entonces veo el mensaje "El email ya está registrado"
Y permanezco en la página de registro
```

#### Reglas de Negocio
- **RN-004:** El registro público solo crea usuarios con rol COLABORADOR
- **RN-005:** Solo ADMIN puede crear usuarios con rol LIDER o ADMIN
- **RN-006:** Los emails deben ser únicos en todo el sistema

#### Implementación
- **Controlador:** `AuthController.java`
- **Vista:** `auth/register.html`
- **Servicio:** `UserService.registerUser()`

#### Estado
✅ **COMPLETADO** - Implementado y probado

---

### HU-003: Gestión de Usuarios por Admin

**Como** administrador
**Quiero** crear, editar y eliminar usuarios del sistema
**Para** gestionar el acceso y roles de los usuarios

#### Criterios de Aceptación

1. **Listado de Usuarios**
   - ✅ El admin puede ver todos los usuarios en `/admin/usuarios`
   - ✅ La lista muestra: nombre, email, rol, estado (activo/inactivo)
   - ✅ Incluye botones de "Editar" y "Eliminar" para cada usuario

2. **Creación de Usuarios**
   - ✅ Formulario en `/admin/crear-usuario`
   - ✅ Campos: nombre, email, contraseña, rol
   - ✅ El admin puede seleccionar cualquier rol (ADMIN, LIDER, COLABORADOR)
   - ✅ Validación de email único

3. **Edición de Usuarios**
   - ✅ Formulario en `/admin/editar-usuario/{id}`
   - ✅ Permite cambiar nombre, email, rol
   - ✅ Permite cambiar contraseña (opcional)
   - ✅ Permite activar/desactivar usuario

4. **Eliminación de Usuarios (Soft Delete)**
   - ✅ Al eliminar, se marca `isActive = false`
   - ✅ No se eliminan físicamente los datos
   - ✅ Usuarios inactivos no pueden iniciar sesión

#### Escenarios de Prueba

**Escenario 1: Crear Usuario Líder**
```gherkin
Dado que soy administrador en "/admin/crear-usuario"
Cuando ingreso nombre "María González"
Y ingreso email "maria@ejemplo.com"
Y ingreso contraseña "lider123"
Y selecciono rol "LIDER"
Y hago clic en "Crear Usuario"
Entonces se crea el usuario con rol LIDER
Y veo el mensaje "Usuario creado exitosamente"
```

**Escenario 2: Desactivar Usuario**
```gherkin
Dado que existe un usuario activo "Juan Pérez"
Cuando ingreso a editar su perfil
Y marco "Desactivar usuario"
Y hago clic en "Guardar"
Entonces el usuario queda inactivo
Y ya no puede iniciar sesión
```

#### Reglas de Negocio
- **RN-007:** Solo ADMIN puede crear usuarios con cualquier rol
- **RN-008:** No se puede eliminar el último usuario ADMIN
- **RN-009:** No se puede cambiar el email a uno ya existente
- **RN-010:** Al desactivar un líder, sus proyectos quedan sin líder asignado

#### Implementación
- **Controlador:** `AdminUserController.java`
- **Vistas:** `admin/usuarios.html`, `admin/crear-usuario.html`, `admin/editar-usuario.html`
- **Servicio:** `UserService`

#### Estado
✅ **COMPLETADO** - Implementado y probado

---

## ÉPICA 2: Gestión de Proyectos

---

### HU-004: Creación de Proyectos por Admin

**Como** administrador
**Quiero** crear nuevos proyectos y asignar un líder
**Para** organizar el trabajo en proyectos gestionados

#### Criterios de Aceptación

1. **Formulario de Creación**
   - ✅ Formulario en `/admin/crear-proyecto`
   - ✅ Campos: nombre, descripción, líder, fechas (opcional)
   - ✅ El campo líder muestra un select con usuarios LIDER disponibles

2. **Validación**
   - ✅ Nombre no puede estar vacío (3-200 caracteres)
   - ✅ Descripción no puede estar vacía (10-2000 caracteres)
   - ✅ Debe seleccionar un líder válido

3. **Creación**
   - ✅ El proyecto se crea con estado PLANNING
   - ✅ El líder queda asignado al proyecto
   - ✅ La lista de colaboradores está vacía inicialmente

#### Escenarios de Prueba

**Escenario 1: Crear Proyecto con Líder**
```gherkin
Dado que soy administrador en "/admin/crear-proyecto"
Cuando ingreso nombre "Sistema de Ventas"
Y ingreso descripción "Desarrollo de sistema de ventas online"
Y selecciono líder "María González"
Y hago clic en "Crear Proyecto"
Entonces se crea el proyecto en estado PLANNING
Y María González queda como líder del proyecto
Y veo el mensaje "Proyecto creado exitosamente"
```

#### Reglas de Negocio
- **RN-011:** Solo ADMIN puede crear proyectos
- **RN-012:** El líder debe ser un usuario con rol LIDER
- **RN-013:** El estado inicial siempre es PLANNING

#### Implementación
- **Controlador:** `AdminProjectController.java:218-256`
- **Vista:** `admin/crear-proyecto.html`
- **Servicio:** `ProjectService.createProject()`

#### Estado
✅ **COMPLETADO** - Implementado y probado

---

### HU-005: Vista de Proyectos del Líder

**Como** líder de proyecto
**Quiero** ver todos mis proyectos asignados
**Para** gestionar mis responsabilidades

#### Criterios de Aceptación

1. **Listado de Proyectos**
   - ✅ El líder ve sus proyectos en `/lider/proyectos`
   - ✅ Muestra: nombre, descripción, estado, número de tareas
   - ✅ Solo muestra proyectos donde es líder

2. **Acceso a Detalles**
   - ✅ Cada proyecto tiene un botón "Ver Detalles"
   - ✅ Redirige a `/lider/proyecto/{id}`

#### Escenarios de Prueba

**Escenario 1: Ver Mis Proyectos**
```gherkin
Dado que soy líder "María González"
Cuando accedo a "/lider/proyectos"
Entonces veo solo los proyectos donde soy líder
Y no veo proyectos de otros líderes
```

#### Reglas de Negocio
- **RN-014:** Un líder solo ve proyectos donde `leaderId == userId`
- **RN-015:** ADMIN puede ver todos los proyectos

#### Implementación
- **Controlador:** `LiderController.java`
- **Vista:** `lider/proyectos.html`
- **Servicio:** `ProjectService.getProjectsByLeaderId()`

#### Estado
✅ **COMPLETADO** - Implementado y probado

---

### HU-006: Gestión de Colaboradores en Proyecto

**Como** líder de proyecto
**Quiero** agregar y remover colaboradores de mi proyecto
**Para** formar mi equipo de trabajo

#### Criterios de Aceptación

1. **Vista de Gestión**
   - ✅ El líder accede a `/lider/proyecto/{id}/gestionar-colaboradores`
   - ✅ Ve la lista actual de colaboradores
   - ✅ Ve un select con colaboradores disponibles

2. **Agregar Colaborador**
   - ✅ Selecciona un colaborador del dropdown
   - ✅ Click en "Agregar"
   - ✅ El colaborador se agrega a la lista del proyecto

3. **Remover Colaborador**
   - ✅ Click en botón "Remover" junto al colaborador
   - ✅ El colaborador se remueve del proyecto
   - ✅ Sus tareas asignadas quedan sin cambios

#### Escenarios de Prueba

**Escenario 1: Agregar Colaborador**
```gherkin
Dado que soy líder del proyecto "Sistema de Ventas"
Cuando accedo a gestionar colaboradores
Y selecciono "Juan Pérez" del dropdown
Y hago clic en "Agregar"
Entonces Juan Pérez se agrega al proyecto
Y ahora puedo asignarle tareas
```

#### Reglas de Negocio
- **RN-016:** Solo el líder del proyecto puede gestionar colaboradores
- **RN-017:** Solo usuarios con rol COLABORADOR pueden ser agregados
- **RN-018:** Un colaborador no puede estar duplicado en el mismo proyecto

#### Implementación
- **Controlador:** `LiderController.java`
- **Vista:** `lider/gestionar-colaboradores.html`
- **Servicio:** `ProjectService.addCollaborator()`, `removeCollaborator()`

#### Estado
✅ **COMPLETADO** - Implementado y probado

---

## ÉPICA 3: Gestión de Tareas

---

### HU-007: Creación de Tareas por Líder

**Como** líder de proyecto
**Quiero** crear tareas en mi proyecto y asignarlas a colaboradores
**Para** distribuir el trabajo del proyecto

#### Criterios de Aceptación

1. **Formulario de Creación**
   - ✅ Formulario en `/lider/proyecto/{id}/nueva-tarea`
   - ✅ Campos: título, descripción, responsable, prioridad, fecha de vencimiento
   - ✅ El campo responsable muestra colaboradores del proyecto

2. **Validación**
   - ✅ Título no vacío (3-200 caracteres)
   - ✅ Descripción no vacía (10-5000 caracteres)
   - ✅ Responsable debe ser un colaborador del proyecto
   - ✅ Prioridad: BAJA, MEDIA, ALTA, CRÍTICA

3. **Creación**
   - ✅ La tarea se crea con estado PENDIENTE
   - ✅ Se asigna al colaborador seleccionado
   - ✅ Se registra el líder como creador (`createdById`)

#### Escenarios de Prueba

**Escenario 1: Crear Tarea y Asignar**
```gherkin
Dado que soy líder del proyecto "Sistema de Ventas"
Cuando accedo a "/lider/proyecto/{id}/nueva-tarea"
Y ingreso título "Implementar login"
Y ingreso descripción "Desarrollar funcionalidad de login con Spring Security"
Y selecciono responsable "Juan Pérez"
Y selecciono prioridad "ALTA"
Y selecciono fecha de vencimiento "2025-11-15"
Y hago clic en "Crear Tarea"
Entonces la tarea se crea en estado PENDIENTE
Y Juan Pérez ve la tarea en su lista de tareas
```

**Escenario 2: Tarea Crítica**
```gherkin
Dado que creo una tarea
Cuando selecciono prioridad "CRÍTICA"
Entonces la tarea se marca visualmente en rojo
Y aparece al inicio de la lista de tareas
```

#### Reglas de Negocio
- **RN-019:** Solo el líder del proyecto puede crear tareas
- **RN-020:** El estado inicial siempre es PENDIENTE
- **RN-021:** El responsable debe ser colaborador del proyecto
- **RN-022:** ADMIN puede crear tareas en cualquier proyecto

#### Implementación
- **Controlador:** `LiderController.java:61-146`
- **Vista:** `lider/crear-tarea.html`
- **Servicio:** `TaskService.createTask()`

#### Estado
✅ **COMPLETADO** - Implementado y probado

---

### HU-008: Vista de Tareas del Colaborador

**Como** colaborador
**Quiero** ver solo las tareas asignadas a mí
**Para** conocer mis responsabilidades

#### Criterios de Aceptación

1. **Listado de Tareas**
   - ✅ El colaborador ve sus tareas en `/mis-tareas`
   - ✅ Solo muestra tareas donde `assignedToId == userId`
   - ✅ No ve tareas de otros colaboradores

2. **Información Mostrada**
   - ✅ Título y descripción de la tarea
   - ✅ Proyecto al que pertenece
   - ✅ Prioridad (con color distintivo)
   - ✅ Estado actual
   - ✅ Fecha de vencimiento
   - ✅ Indicador si está vencida

3. **Acciones Disponibles**
   - ✅ Botón "Ver Detalle"
   - ✅ Botón "Cambiar Estado" (según estado actual)

#### Escenarios de Prueba

**Escenario 1: Ver Mis Tareas**
```gherkin
Dado que soy colaborador "Juan Pérez"
Cuando accedo a "/mis-tareas"
Entonces veo solo las tareas asignadas a mí
Y NO veo tareas de otros colaboradores
Y veo el proyecto de cada tarea
```

**Escenario 2: Tarea Vencida**
```gherkin
Dado que tengo una tarea con fecha de vencimiento pasada
Cuando veo mis tareas
Entonces la tarea aparece marcada en rojo
Y veo el mensaje "VENCIDA"
```

#### Reglas de Negocio
- **RN-023:** Un colaborador solo ve tareas donde es el responsable
- **RN-024:** Las tareas se ordenan por prioridad y fecha de vencimiento
- **RN-025:** Las tareas completadas se muestran al final

#### Implementación
- **Controlador:** `DashboardController.java:79-98`
- **Vista:** `colaborador/mis-tareas.html`
- **Servicio:** `TaskService.getTasksForUser()`

#### Estado
✅ **COMPLETADO** - Implementado y probado

---

### HU-009: Actualización de Estado de Tarea

**Como** colaborador
**Quiero** cambiar el estado de mis tareas
**Para** reportar mi progreso

#### Criterios de Aceptación

1. **Transiciones Permitidas**
   - ✅ PENDIENTE → EN_PROGRESO
   - ✅ EN_PROGRESO → EN_REVISION, BLOQUEADA, COMPLETADA
   - ✅ EN_REVISION → EN_PROGRESO, COMPLETADA
   - ✅ BLOQUEADA → EN_PROGRESO
   - ✅ COMPLETADA → (no permite cambios)

2. **Validación de Permisos**
   - ✅ Solo el responsable puede cambiar el estado
   - ✅ El líder del proyecto también puede cambiar el estado
   - ✅ El admin también puede cambiar el estado

3. **Registro de Completado**
   - ✅ Al cambiar a COMPLETADA, se registra `completedAt`
   - ✅ Una vez completada, no se puede cambiar el estado

#### Escenarios de Prueba

**Escenario 1: Cambiar a En Progreso**
```gherkin
Dado que tengo una tarea en estado PENDIENTE
Cuando hago clic en "Iniciar Tarea"
Entonces el estado cambia a EN_PROGRESO
Y veo el mensaje "Estado actualizado exitosamente"
```

**Escenario 2: Transición Inválida**
```gherkin
Dado que tengo una tarea en estado PENDIENTE
Cuando intento cambiar directamente a COMPLETADA
Entonces veo el mensaje de error "No se puede cambiar de PENDIENTE a COMPLETADA"
Y el estado permanece sin cambios
```

**Escenario 3: Completar Tarea**
```gherkin
Dado que tengo una tarea en estado EN_PROGRESO
Cuando hago clic en "Completar Tarea"
Entonces el estado cambia a COMPLETADA
Y se registra la fecha y hora de completado
Y ya no puedo cambiar el estado
```

#### Reglas de Negocio
- **RN-026:** Solo se permiten transiciones válidas definidas en TaskStatus
- **RN-027:** El estado COMPLETADA es final (no permite más cambios)
- **RN-028:** Al completar, se registra automáticamente `completedAt`
- **RN-029:** Solo el responsable, líder o admin pueden cambiar el estado

#### Implementación
- **Controlador:** `TaskController.java:58-95`
- **Servicio:** `TaskService.updateTaskStatus()`
- **Validación:** `TaskStatus.canTransitionTo()`

#### Estado
✅ **COMPLETADO** - Implementado y probado

---

### HU-010: Vista Detallada del Proyecto (Líder)

**Como** líder de proyecto
**Quiero** ver todas las tareas de mi proyecto
**Para** monitorear el progreso global

#### Criterios de Aceptación

1. **Información del Proyecto**
   - ✅ Nombre y descripción del proyecto
   - ✅ Estado del proyecto
   - ✅ Fechas de inicio y fin
   - ✅ Lista de colaboradores

2. **Listado de TODAS las Tareas**
   - ✅ Muestra todas las tareas del proyecto
   - ✅ No filtra por responsable (a diferencia del colaborador)
   - ✅ Muestra responsable de cada tarea
   - ✅ Muestra estado, prioridad y fechas

3. **Métricas del Proyecto**
   - ✅ Progreso general (% de tareas completadas)
   - ✅ Número de tareas por estado
   - ✅ Tareas vencidas
   - ✅ Tareas críticas pendientes

4. **Acciones Disponibles**
   - ✅ Botón "Nueva Tarea"
   - ✅ Botón "Gestionar Colaboradores"
   - ✅ Acceso a detalles de cada tarea

#### Escenarios de Prueba

**Escenario 1: Ver Todas las Tareas**
```gherkin
Dado que soy líder del proyecto "Sistema de Ventas"
Cuando accedo a "/lider/proyecto/{id}"
Entonces veo TODAS las tareas del proyecto
Y veo tareas asignadas a diferentes colaboradores
Y veo el responsable de cada tarea
```

**Escenario 2: Ver Progreso**
```gherkin
Dado que mi proyecto tiene 10 tareas
Y 7 están completadas
Cuando veo el detalle del proyecto
Entonces veo "Progreso: 70%"
Y veo una barra de progreso visual
```

#### Reglas de Negocio
- **RN-030:** El líder ve todas las tareas sin filtro por responsable
- **RN-031:** El progreso se calcula como: (tareas completadas / total tareas) * 100
- **RN-032:** Solo el líder del proyecto y admin pueden ver esta vista

#### Implementación
- **Controlador:** `LiderController.java:159-176`
- **Vista:** `lider/ver-proyecto.html`
- **Servicio:** `TaskService.getProjectDetails()`

#### Estado
✅ **COMPLETADO** - Implementado y probado

---

## ÉPICA 4: Dashboards y Reportes

---

### HU-011: Dashboard del Administrador

**Como** administrador
**Quiero** ver métricas globales del sistema
**Para** monitorear el estado general

#### Criterios de Aceptación

1. **Métricas Principales**
   - ✅ Total de usuarios (por rol)
   - ✅ Total de proyectos (por estado)
   - ✅ Total de tareas (por estado)
   - ✅ Tareas vencidas en el sistema

2. **Accesos Rápidos**
   - ✅ Botón "Crear Usuario"
   - ✅ Botón "Crear Proyecto"
   - ✅ Enlace a "Gestionar Usuarios"
   - ✅ Enlace a "Gestionar Proyectos"

#### Escenarios de Prueba

**Escenario 1: Ver Métricas Globales**
```gherkin
Dado que soy administrador
Cuando accedo a "/admin/dashboard"
Entonces veo el total de usuarios del sistema
Y veo el total de proyectos
Y veo el total de tareas
Y veo cuántas tareas están vencidas
```

#### Reglas de Negocio
- **RN-033:** Solo ADMIN puede ver este dashboard
- **RN-034:** Las métricas se cachean por 5 minutos para mejorar rendimiento

#### Implementación
- **Controlador:** `AdminDashboardController.java`
- **Vista:** `admin/dashboard.html`
- **Servicio:** `MetricsService`

#### Estado
✅ **COMPLETADO** - Implementado y probado

---

### HU-012: Dashboard del Líder

**Como** líder de proyecto
**Quiero** ver un resumen de mis proyectos
**Para** tener una vista rápida de mi trabajo

#### Criterios de Aceptación

1. **Resumen de Proyectos**
   - ✅ Lista de proyectos donde soy líder
   - ✅ Progreso de cada proyecto
   - ✅ Número de tareas pendientes por proyecto

2. **Tareas Críticas**
   - ✅ Lista de tareas críticas en mis proyectos
   - ✅ Lista de tareas vencidas en mis proyectos

#### Escenarios de Prueba

**Escenario 1: Ver Mis Proyectos**
```gherkin
Dado que soy líder "María González"
Cuando accedo a "/lider/dashboard"
Entonces veo todos mis proyectos asignados
Y veo el progreso de cada uno
Y veo tareas críticas pendientes
```

#### Reglas de Negocio
- **RN-035:** Solo muestra proyectos donde `leaderId == userId`
- **RN-036:** Las tareas se ordenan por prioridad

#### Implementación
- **Controlador:** `LiderController.java`
- **Vista:** `lider/dashboard.html`
- **Servicio:** `ProjectService`, `TaskService`

#### Estado
✅ **COMPLETADO** - Implementado y probado

---

## 4. Resumen de Historias

| ID | Historia | Prioridad | Estado | Épica |
|----|----------|-----------|--------|-------|
| HU-001 | Login de Usuarios | Alta | ✅ Completado | Épica 1 |
| HU-002 | Registro Público | Media | ✅ Completado | Épica 1 |
| HU-003 | Gestión de Usuarios | Alta | ✅ Completado | Épica 1 |
| HU-004 | Creación de Proyectos | Alta | ✅ Completado | Épica 2 |
| HU-005 | Vista de Proyectos del Líder | Media | ✅ Completado | Épica 2 |
| HU-006 | Gestión de Colaboradores | Media | ✅ Completado | Épica 2 |
| HU-007 | Creación de Tareas | Alta | ✅ Completado | Épica 3 |
| HU-008 | Vista de Tareas del Colaborador | Alta | ✅ Completado | Épica 3 |
| HU-009 | Actualización de Estado | Alta | ✅ Completado | Épica 3 |
| HU-010 | Vista Detallada del Proyecto | Media | ✅ Completado | Épica 3 |
| HU-011 | Dashboard del Administrador | Media | ✅ Completado | Épica 4 |
| HU-012 | Dashboard del Líder | Media | ✅ Completado | Épica 4 |

---

## 5. Trazabilidad con Requisitos

### Requisitos Funcionales Cubiertos

| Requisito | Historias Relacionadas |
|-----------|------------------------|
| RF-001: El sistema debe permitir autenticación de usuarios | HU-001 |
| RF-002: El sistema debe soportar múltiples roles | HU-001, HU-003 |
| RF-003: El sistema debe permitir gestión de usuarios | HU-002, HU-003 |
| RF-004: El sistema debe permitir gestión de proyectos | HU-004, HU-005, HU-006 |
| RF-005: El sistema debe permitir gestión de tareas | HU-007, HU-008, HU-009 |
| RF-006: El sistema debe mostrar progreso de proyectos | HU-010, HU-011, HU-012 |

---

## 6. Historias de Usuario Futuras (Backlog)

### Prioridad Alta
- **HU-013:** Notificaciones por Email
- **HU-014:** Comentarios en Tareas
- **HU-015:** Adjuntar Archivos a Tareas

### Prioridad Media
- **HU-016:** Búsqueda y Filtros Avanzados
- **HU-017:** Exportar Reportes a PDF/Excel
- **HU-018:** Historial de Cambios en Tareas

### Prioridad Baja
- **HU-019:** Integración con Calendario
- **HU-020:** Modo Kanban para Tareas
- **HU-021:** Chat en Tiempo Real

---

## 7. Definición de Terminado (Definition of Done)

Una historia de usuario se considera terminada cuando:

1. ✅ Código implementado y testeado
2. ✅ Pruebas unitarias escritas y pasando
3. ✅ Interfaz de usuario implementada (si aplica)
4. ✅ Validaciones de negocio implementadas
5. ✅ Documentación actualizada
6. ✅ Revisión de código completada
7. ✅ Probado manualmente con datos reales
8. ✅ Criterios de aceptación verificados
9. ✅ Sin bugs conocidos

---

## 8. Referencias

- **Verificación de Historias de Usuario:** `/VERIFICACION_HISTORIAS_USUARIO.md`
- **Funcionalidades Implementadas:** `/FUNCIONALIDADES.md`
- **Guía de Pruebas:** `/GUIA_PRUEBAS.md`

---

**Última Actualización:** 2025-11-06
**Responsable:** Product Owner y Equipo de Desarrollo
**Estado:** Documento Aprobado
