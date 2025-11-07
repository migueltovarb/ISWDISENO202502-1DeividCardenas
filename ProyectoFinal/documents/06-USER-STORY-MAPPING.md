# User Story Mapping - Sistema de Gestión de Proyectos y Tareas

**Fecha:** 2025-11-06
**Versión:** 2.0
**Proyecto:** Sistema de Gestión de Proyectos y Tareas

---

## 1. Introducción

### 1.1 ¿Qué es User Story Mapping?
El **User Story Mapping** es una técnica visual para organizar historias de usuario que ayuda a:
- Entender el flujo completo de usuario
- Priorizar funcionalidades
- Planificar releases
- Identificar MVP (Minimum Viable Product)

### 1.2 Estructura del User Story Map
```
Actividades (Nivel Superior)
    ↓
Tareas del Usuario (Nivel Medio)
    ↓
Historias de Usuario (Nivel Inferior)
    ↓
Priorización por Release/Sprint
```

---

## 2. User Story Map Completo

```
═══════════════════════════════════════════════════════════════════════════════
                        SISTEMA DE GESTIÓN DE PROYECTOS Y TAREAS
═══════════════════════════════════════════════════════════════════════════════

┌─────────────────┬─────────────────┬─────────────────┬─────────────────┬─────────────────┐
│   ACTIVIDAD 1   │   ACTIVIDAD 2   │   ACTIVIDAD 3   │   ACTIVIDAD 4   │   ACTIVIDAD 5   │
│  Autenticarse   │   Gestionar     │   Gestionar     │   Gestionar     │   Monitorear    │
│   en Sistema    │    Usuarios     │   Proyectos     │     Tareas      │    Progreso     │
└─────────────────┴─────────────────┴─────────────────┴─────────────────┴─────────────────┘

───────────────────────────────────────────────────────────────────────────────
TAREAS DEL USUARIO (User Tasks)
───────────────────────────────────────────────────────────────────────────────

┌─────────────────┬──────────────────┬─────────────────┬─────────────────┬─────────────────┐
│ Iniciar sesión  │ Crear usuarios   │ Crear proyectos │ Crear tareas    │ Ver dashboard   │
│                 │                  │                 │                 │                 │
│ Cerrar sesión   │ Editar usuarios  │ Editar proyectos│ Asignar tareas  │ Ver estadísticas│
│                 │                  │                 │                 │                 │
│ Registrarse     │ Eliminar usuarios│ Ver proyectos   │ Actualizar      │ Ver reportes    │
│                 │                  │                 │ estado tareas   │                 │
│                 │ Asignar roles    │ Asignar líderes │                 │                 │
│                 │                  │                 │ Ver mis tareas  │ Ver progreso    │
│                 │                  │ Gestionar       │                 │ de proyecto     │
│                 │                  │ colaboradores   │ Filtrar tareas  │                 │
└─────────────────┴──────────────────┴─────────────────┴─────────────────┴─────────────────┘

───────────────────────────────────────────────────────────────────────────────
MVP - RELEASE 1.0 (Funcionalidades Esenciales)
───────────────────────────────────────────────────────────────────────────────

┌─────────────────┬─────────────────┬─────────────────┬─────────────────┬─────────────────┐
│ 🔴 HU-001       |🔴 HU-003       │ 🔴 HU-004       │ 🔴 HU-007      | 🔴 HU-011      │
│ Login usuarios  │ Crear usuarios  │ Crear proyectos │ Crear tareas    │ Dashboard Admin │
│ (Admin, Líder,  │ (Solo Admin)    │ (Admin)         │ (Líder)         │                 │
│ Colaborador)    │                 │                 │                 │                 │
│                 │                 │                 │                 │                 │
│                 │                 │ 🔴 HU-005       │🔴 HU-008       │ 🔴 HU-012       │
│                 │                 │ Asignar líder   │ Ver mis tareas  │ Dashboard Líder │
│                 │                 │ a proyecto      │ (Colaborador)   │                 │
│                 │                 │                 │                 │                 │
│                 │                 │                 │ 🔴 HU-009      │ 🔴 HU-010       │
│                 │                 │                 │ Actualizar      │ Vista proyecto  │
│                 │                 │                 │ estado tarea    │ completo (Líder)│
│                 │                 │                 │ (Colaborador)   │                 │
└─────────────────┴─────────────────┴─────────────────┴─────────────────┴─────────────────┘

───────────────────────────────────────────────────────────────────────────────
RELEASE 1.1 (Mejoras de Usabilidad y Gestión)
───────────────────────────────────────────────────────────────────────────────

┌─────────────────┬─────────────────┬─────────────────┬─────────────────┬─────────────────┐
│ 🟡 HU-002       │ 🟡 HU-013      │ 🟡 HU-006       │ 🟡 HU-016      │ 🟡 HU-017       │
│ Registro público│ Editar usuarios │ Gestionar       │ Búsqueda de     │ Ver historial   │
│ (Colaboradores) │                 │ colaboradores   │ tareas          │ de cambios      │
│                 │                 │ en proyecto     │                 │                 │
│                 │ 🟡 HU-014       │                 │                 │                 │
│                 │ Desactivar      │                 │ 🟡 HU-018       │                 │
│                 │ usuarios        │                 │ Filtros         │                 │
│                 │                 │                 │ avanzados       │                 │
└─────────────────┴─────────────────┴─────────────────┴─────────────────┴─────────────────┘

───────────────────────────────────────────────────────────────────────────────
RELEASE 2.0 (Colaboración y Notificaciones)
───────────────────────────────────────────────────────────────────────────────

┌─────────────────┬─────────────────┬─────────────────┬─────────────────┬─────────────────┐
│                 │                 │                 │ 🟢 HU-019       │ 🟢 HU-020      │
│                 │                 │                 │ Comentarios     │ Notificaciones  │
│                 │                 │                 │ en tareas       │ por email       │
│                 │                 │                 │                 │                 │
│                 │                 │                 │ 🟢 HU-021       │ 🟢 HU-022      │
│                 │                 │                 │ Adjuntar        │ Notificaciones  │
│                 │                 │                 │ archivos        │ en tiempo real  │
└─────────────────┴─────────────────┴─────────────────┴─────────────────┴─────────────────┘

───────────────────────────────────────────────────────────────────────────────
RELEASE 3.0 (Analítica y Reportes Avanzados)
───────────────────────────────────────────────────────────────────────────────

┌─────────────────┬─────────────────┬─────────────────┬─────────────────┬─────────────────┐
│                 │                 │                 │                 │ 🔵 HU-023       │
│                 │                 │                 │                 │ Exportar        │
│                 │                 │                 │                 │ reportes PDF    │
│                 │                 │                 │                 │                 │
│                 │                 │                 │                 │ 🔵 HU-024       │
│                 │                 │                 │                 │ Gráficos de     │
│                 │                 │                 │                 │ progreso        │
│                 │                 │                 │                 │                 │
│                 │                 │                 │                 │ 🔵 HU-025       │
│                 │                 │                 │                 │ Vista Kanban    │
└─────────────────┴─────────────────┴─────────────────┴─────────────────┴─────────────────┘

Leyenda:
🔴 MVP / Release 1.0 (COMPLETADO)
🟡 Release 1.1 (PARCIALMENTE COMPLETADO)
🟢 Release 2.0 (PLANIFICADO)
🔵 Release 3.0 (FUTURO)
```

---

## 3. Desglose por Actividad

### ACTIVIDAD 1: Autenticarse en el Sistema

#### Objetivo del Usuario
Acceder al sistema de forma segura para realizar mi trabajo

#### Tareas del Usuario
1. Iniciar sesión con credenciales
2. Recuperar contraseña olvidada (futuro)
3. Cerrar sesión
4. Registrarse como nuevo colaborador

#### Historias de Usuario

**MVP - Release 1.0**

| ID | Historia | Prioridad | Estado |
|----|----------|-----------|--------|
| HU-001 | Como usuario, quiero iniciar sesión con email y contraseña para acceder al sistema | MUST HAVE | ✅ COMPLETO |

**Release 1.1**

| ID | Historia | Prioridad | Estado |
|----|----------|-----------|--------|
| HU-002 | Como visitante, quiero registrarme como colaborador para acceder al sistema | SHOULD HAVE | ✅ COMPLETO |

**Release 2.0 (Futuro)**

| ID | Historia | Prioridad | Estado |
|----|----------|-----------|--------|
| HU-026 | Como usuario, quiero recuperar mi contraseña por email si la olvido | COULD HAVE | 🔄 PENDIENTE |
| HU-027 | Como usuario, quiero autenticarme con Google OAuth | COULD HAVE | 🔄 PENDIENTE |

---

### ACTIVIDAD 2: Gestionar Usuarios

#### Objetivo del Usuario (Admin)
Controlar el acceso al sistema y gestionar permisos de usuarios

#### Tareas del Usuario
1. Crear nuevos usuarios
2. Editar usuarios existentes
3. Activar/Desactivar usuarios
4. Asignar roles
5. Ver listado de usuarios

#### Historias de Usuario

**MVP - Release 1.0**

| ID | Historia | Prioridad | Estado |
|----|----------|-----------|--------|
| HU-003 | Como admin, quiero crear usuarios con cualquier rol para gestionar el acceso | MUST HAVE | ✅ COMPLETO |

**Release 1.1**

| ID | Historia | Prioridad | Estado |
|----|----------|-----------|--------|
| HU-013 | Como admin, quiero editar usuarios existentes para actualizar su información | SHOULD HAVE | ✅ COMPLETO |
| HU-014 | Como admin, quiero desactivar usuarios en lugar de eliminarlos para mantener historial | SHOULD HAVE | ✅ COMPLETO |

**Release 2.0 (Futuro)**

| ID | Historia | Prioridad | Estado |
|----|----------|-----------|--------|
| HU-028 | Como admin, quiero ver el historial de actividad de un usuario | COULD HAVE | 🔄 PENDIENTE |
| HU-029 | Como admin, quiero cambiar el rol de un usuario sin recrear la cuenta | SHOULD HAVE | 🔄 PENDIENTE |

---

### ACTIVIDAD 3: Gestionar Proyectos

#### Objetivo del Usuario
Organizar el trabajo en proyectos con equipos asignados

#### Tareas del Usuario
1. Crear nuevos proyectos
2. Asignar líder al proyecto
3. Editar información del proyecto
4. Gestionar colaboradores del proyecto
5. Ver listado de proyectos
6. Cambiar estado del proyecto

#### Historias de Usuario

**MVP - Release 1.0**

| ID | Historia | Prioridad | Estado |
|----|----------|-----------|--------|
| HU-004 | Como admin, quiero crear proyectos y asignar un líder para organizar el trabajo | MUST HAVE | ✅ COMPLETO |
| HU-005 | Como líder, quiero ver todos mis proyectos asignados en un lugar | MUST HAVE | ✅ COMPLETO |

**Release 1.1**

| ID | Historia | Prioridad | Estado |
|----|----------|-----------|--------|
| HU-006 | Como líder, quiero agregar colaboradores a mi proyecto para formar mi equipo | SHOULD HAVE | ✅ COMPLETO |
| HU-015 | Como admin, quiero editar proyectos existentes para actualizar información | SHOULD HAVE | ✅ COMPLETO |

**Release 2.0 (Futuro)**

| ID | Historia | Prioridad | Estado |
|----|----------|-----------|--------|
| HU-030 | Como líder, quiero establecer fechas de inicio y fin del proyecto | SHOULD HAVE | 🔄 PENDIENTE |
| HU-031 | Como líder, quiero cambiar el estado del proyecto (PLANNING → IN_PROGRESS → COMPLETED) | SHOULD HAVE | 🔄 PENDIENTE |
| HU-032 | Como admin, quiero archivar proyectos completados sin eliminarlos | COULD HAVE | 🔄 PENDIENTE |

---

### ACTIVIDAD 4: Gestionar Tareas

#### Objetivo del Usuario
Asignar, ejecutar y dar seguimiento a tareas dentro de proyectos

#### Tareas del Usuario
1. Crear nuevas tareas
2. Asignar tareas a colaboradores
3. Ver mis tareas asignadas
4. Actualizar estado de tareas
5. Cambiar prioridad de tareas
6. Comentar en tareas (futuro)
7. Adjuntar archivos (futuro)

#### Historias de Usuario

**MVP - Release 1.0**

| ID | Historia | Prioridad | Estado |
|----|----------|-----------|--------|
| HU-007 | Como líder, quiero crear tareas en mi proyecto y asignarlas a colaboradores | MUST HAVE | ✅ COMPLETO |
| HU-008 | Como colaborador, quiero ver solo las tareas asignadas a mí | MUST HAVE | ✅ COMPLETO |
| HU-009 | Como colaborador, quiero actualizar el estado de mis tareas para reportar progreso | MUST HAVE | ✅ COMPLETO |
| HU-010 | Como líder, quiero ver todas las tareas de mi proyecto sin filtros | MUST HAVE | ✅ COMPLETO |

**Release 1.1**

| ID | Historia | Prioridad | Estado |
|----|----------|-----------|--------|
| HU-016 | Como usuario, quiero buscar tareas por título o descripción | SHOULD HAVE | 🔄 PENDIENTE |
| HU-018 | Como líder, quiero filtrar tareas por estado, prioridad o responsable | SHOULD HAVE | 🔄 PENDIENTE |

**Release 2.0**

| ID | Historia | Prioridad | Estado |
|----|----------|-----------|--------|
| HU-019 | Como usuario, quiero comentar en tareas para comunicarme con el equipo | SHOULD HAVE | 🔄 PENDIENTE |
| HU-021 | Como usuario, quiero adjuntar archivos a tareas | COULD HAVE | 🔄 PENDIENTE |
| HU-033 | Como líder, quiero reasignar tareas a otro colaborador | SHOULD HAVE | 🔄 PENDIENTE |
| HU-034 | Como colaborador, quiero ver la fecha de vencimiento destacada si está próxima | SHOULD HAVE | 🔄 PENDIENTE |

**Release 3.0**

| ID | Historia | Prioridad | Estado |
|----|----------|-----------|--------|
| HU-035 | Como líder, quiero ver dependencias entre tareas | COULD HAVE | 🔄 PENDIENTE |
| HU-036 | Como usuario, quiero ver el historial completo de cambios de una tarea | COULD HAVE | 🔄 PENDIENTE |

---

### ACTIVIDAD 5: Monitorear Progreso

#### Objetivo del Usuario
Visualizar el estado y progreso del trabajo para tomar decisiones

#### Tareas del Usuario
1. Ver dashboard personalizado por rol
2. Ver estadísticas globales (admin)
3. Ver progreso de proyectos
4. Ver métricas de tareas
5. Exportar reportes (futuro)
6. Ver gráficos de avance (futuro)

#### Historias de Usuario

**MVP - Release 1.0**

| ID | Historia | Prioridad | Estado |
|----|----------|-----------|--------|
| HU-011 | Como admin, quiero ver métricas globales del sistema en mi dashboard | MUST HAVE | ✅ COMPLETO |
| HU-012 | Como líder, quiero ver un resumen de mis proyectos en mi dashboard | MUST HAVE | ✅ COMPLETO |

**Release 1.1**

| ID | Historia | Prioridad | Estado |
|----|----------|-----------|--------|
| HU-017 | Como líder, quiero ver el historial de cambios de estado de tareas | SHOULD HAVE | 🔄 PENDIENTE |

**Release 2.0**

| ID | Historia | Prioridad | Estado |
|----|----------|-----------|--------|
| HU-020 | Como usuario, quiero recibir notificaciones por email cuando cambia el estado de mis tareas | SHOULD HAVE | 🔄 PENDIENTE |
| HU-022 | Como usuario, quiero recibir notificaciones en tiempo real en la aplicación | COULD HAVE | 🔄 PENDIENTE |

**Release 3.0**

| ID | Historia | Prioridad | Estado |
|----|----------|-----------|--------|
| HU-023 | Como líder, quiero exportar reportes de proyecto a PDF | COULD HAVE | 🔄 PENDIENTE |
| HU-024 | Como líder, quiero ver gráficos de progreso del proyecto (burndown chart) | COULD HAVE | 🔄 PENDIENTE |
| HU-025 | Como líder, quiero ver las tareas en vista Kanban | COULD HAVE | 🔄 PENDIENTE |
| HU-037 | Como admin, quiero ver dashboard con métricas de rendimiento del sistema | COULD HAVE | 🔄 PENDIENTE |

---

## 4. Priorización MoSCoW

### MUST HAVE (MVP - Release 1.0) ✅ COMPLETADO

| ID | Historia | Rol |
|----|----------|-----|
| HU-001 | Login de usuarios | Todos |
| HU-003 | Crear usuarios (Admin) | Admin |
| HU-004 | Crear proyectos (Admin) | Admin |
| HU-005 | Ver mis proyectos (Líder) | Líder |
| HU-007 | Crear tareas (Líder) | Líder |
| HU-008 | Ver mis tareas (Colaborador) | Colaborador |
| HU-009 | Actualizar estado de tarea | Colaborador |
| HU-010 | Vista completa de proyecto (Líder) | Líder |
| HU-011 | Dashboard Admin | Admin |
| HU-012 | Dashboard Líder | Líder |

**Total: 10 historias COMPLETADAS ✅**

### SHOULD HAVE (Release 1.1) 🟡 PARCIALMENTE COMPLETADO

| ID | Historia | Estado |
|----|----------|--------|
| HU-002 | Registro público | ✅ COMPLETO |
| HU-006 | Gestionar colaboradores | ✅ COMPLETO |
| HU-013 | Editar usuarios | ✅ COMPLETO |
| HU-014 | Desactivar usuarios | ✅ COMPLETO |
| HU-015 | Editar proyectos | ✅ COMPLETO |
| HU-016 | Búsqueda de tareas | 🔄 PENDIENTE |
| HU-017 | Historial de cambios | 🔄 PENDIENTE |
| HU-018 | Filtros avanzados | 🔄 PENDIENTE |

**Completado: 5/8 (62.5%)**

### COULD HAVE (Release 2.0 y 3.0) 🔄 PENDIENTE

| ID | Historia | Release |
|----|----------|---------|
| HU-019 | Comentarios en tareas | 2.0 |
| HU-020 | Notificaciones por email | 2.0 |
| HU-021 | Adjuntar archivos | 2.0 |
| HU-022 | Notificaciones en tiempo real | 2.0 |
| HU-023 | Exportar reportes PDF | 3.0 |
| HU-024 | Gráficos de progreso | 3.0 |
| HU-025 | Vista Kanban | 3.0 |
| HU-026 | Recuperar contraseña | 2.0 |
| HU-027 | OAuth Google | 2.0 |
| HU-028 | Historial de actividad usuario | 2.0 |
| HU-029 | Cambiar rol de usuario | 2.0 |
| HU-030 | Fechas de proyecto | 2.0 |
| HU-031 | Cambiar estado de proyecto | 2.0 |
| HU-032 | Archivar proyectos | 2.0 |
| HU-033 | Reasignar tareas | 2.0 |
| HU-034 | Alertas de vencimiento | 2.0 |
| HU-035 | Dependencias de tareas | 3.0 |
| HU-036 | Historial de tarea completo | 3.0 |
| HU-037 | Dashboard de rendimiento | 3.0 |

**Total: 19 historias planificadas**

### WON'T HAVE (Fuera de Alcance)

- Integración con Jira o Trello
- Aplicación móvil nativa
- Videoconferencias integradas
- Gestión de presupuestos
- Facturación
- Chat en tiempo real (considerado en release 2.0 como notificaciones)

---

## 5. Flujo de Usuario por Rol

### 5.1 Flujo del Administrador

```
1. LOGIN
   ↓
2. VER DASHBOARD ADMIN
   - Total usuarios
   - Total proyectos
   - Total tareas
   ↓
3. CREAR USUARIO
   - Nombre, email, rol
   - Asignar ADMIN, LIDER o COLABORADOR
   ↓
4. CREAR PROYECTO
   - Nombre, descripción
   - Asignar líder
   ↓
5. MONITOREAR SISTEMA
   - Ver métricas globales
   - Ver estadísticas
```

### 5.2 Flujo del Líder

```
1. LOGIN
   ↓
2. VER DASHBOARD LÍDER
   - Mis proyectos
   - Tareas críticas
   - Tareas vencidas
   ↓
3. SELECCIONAR PROYECTO
   ↓
4. VER TODAS LAS TAREAS
   - De todos los colaboradores
   - Progreso general
   ↓
5. CREAR NUEVA TAREA
   - Título, descripción
   - Asignar a colaborador
   - Prioridad y fecha
   ↓
6. GESTIONAR COLABORADORES
   - Agregar colaboradores
   - Remover colaboradores
   ↓
7. MONITOREAR PROGRESO
   - Ver % de completado
   - Ver tareas por estado
```

### 5.3 Flujo del Colaborador

```
1. LOGIN
   ↓
2. VER MIS TAREAS
   - Solo tareas asignadas a mí
   - Ordenadas por prioridad
   ↓
3. SELECCIONAR TAREA
   ↓
4. VER DETALLE DE TAREA
   - Título, descripción
   - Proyecto, prioridad
   - Fecha de vencimiento
   ↓
5. ACTUALIZAR ESTADO
   - PENDIENTE → EN_PROGRESO
   - EN_PROGRESO → EN_REVISION
   - EN_REVISION → COMPLETADA
   ↓
6. MARCAR COMO COMPLETADA
   - Se registra fecha de completado
   - Ya no se puede cambiar
```

---

## 6. Roadmap de Releases

### Release 1.0 - MVP (COMPLETADO) ✅
**Fecha:** Noviembre 2024
**Objetivo:** Sistema funcional con características básicas

**Funcionalidades:**
- ✅ Login y autenticación
- ✅ Gestión de usuarios (Admin)
- ✅ Gestión de proyectos (Admin)
- ✅ Gestión de tareas (Líder)
- ✅ Vista de tareas (Colaborador)
- ✅ Actualización de estado (Colaborador)
- ✅ Dashboards por rol

**Historias:** 10
**Progreso:** 100%

---

### Release 1.1 - Mejoras de Usabilidad (EN PROGRESO) 🟡
**Fecha Estimada:** Diciembre 2024
**Objetivo:** Mejorar experiencia de usuario y gestión

**Funcionalidades:**
- ✅ Registro público de colaboradores
- ✅ Gestión de colaboradores en proyectos
- ✅ Edición de usuarios y proyectos
- 🔄 Búsqueda y filtros avanzados
- 🔄 Historial de cambios

**Historias:** 8
**Progreso:** 62.5%

---

### Release 2.0 - Colaboración (PLANIFICADO) 🟢
**Fecha Estimada:** Q1 2025
**Objetivo:** Mejorar colaboración y notificaciones

**Funcionalidades Planificadas:**
- 🔄 Comentarios en tareas
- 🔄 Adjuntar archivos a tareas
- 🔄 Notificaciones por email
- 🔄 Notificaciones en tiempo real
- 🔄 Recuperar contraseña
- 🔄 OAuth (Google)
- 🔄 Gestión avanzada de proyectos
- 🔄 Reasignación de tareas

**Historias:** 10
**Progreso:** 0%

---

### Release 3.0 - Analítica (FUTURO) 🔵
**Fecha Estimada:** Q2 2025
**Objetivo:** Reportes y analítica avanzada

**Funcionalidades Planificadas:**
- 🔄 Exportar reportes a PDF
- 🔄 Gráficos de progreso (burndown)
- 🔄 Vista Kanban
- 🔄 Dashboard de rendimiento
- 🔄 Dependencias entre tareas
- 🔄 Historial completo de tareas

**Historias:** 9
**Progreso:** 0%

---

## 7. Métricas del Backlog

### Estado Actual del Backlog

| Categoría | Cantidad | Porcentaje |
|-----------|----------|------------|
| **Completadas** | 15 | 40.5% |
| **En Progreso** | 0 | 0% |
| **Pendientes** | 22 | 59.5% |
| **TOTAL** | 37 | 100% |

### Distribución por Prioridad

| Prioridad | Cantidad | Estado |
|-----------|----------|--------|
| **MUST HAVE** | 10 | ✅ 100% Completado |
| **SHOULD HAVE** | 8 | 🟡 62.5% Completado |
| **COULD HAVE** | 19 | 🔄 0% Completado |

### Distribución por Rol

| Rol | Historias |
|-----|-----------|
| **Admin** | 10 |
| **Líder** | 12 |
| **Colaborador** | 8 |
| **Todos** | 7 |

---

## 8. Referencias

- **Historias de Usuario Detalladas:** `/documents/04-HISTORIAS-USUARIO.md`
- **Verificación de Historias:** `/VERIFICACION_HISTORIAS_USUARIO.md`
- **User History Mapping (Excel):** `/documents/User history maping.xlsx`

---

**Última Actualización:** 2025-11-06
**Responsable:** Product Owner
**Estado:** Documento Vivo (se actualiza continuamente)
