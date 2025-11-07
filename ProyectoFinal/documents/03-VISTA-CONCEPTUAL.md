# Vista Conceptual - Sistema de Gestión de Proyectos y Tareas

**Fecha:** 2025-11-06
**Versión:** 2.0
**Proyecto:** Sistema de Gestión de Proyectos y Tareas

---

## 1. Introducción

### 1.1 Propósito del Documento
Este documento describe la **Vista Conceptual** del sistema, definiendo el modelo de dominio, las entidades principales, sus relaciones y las reglas de negocio que gobiernan el sistema.

### 1.2 Alcance
La vista conceptual define:
- Modelo de dominio con entidades principales
- Relaciones entre entidades
- Atributos clave de cada entidad
- Reglas de negocio
- Restricciones de integridad
- Enumeraciones y tipos de datos

---

## 2. Diagrama del Modelo Conceptual

```
                         ┌────────────────────────────────────┐
                         │            USUARIO                 │
                         ├────────────────────────────────────┤
                         │ + id: String                       │
                         │ + nombre: String                   │
                         │ + email: String (único)            │
                         │ + password: String (encriptado)    │
                         │ + rol: Role (ENUM)                 │
                         │ + activo: Boolean                  │
                         │ + fechaCreacion: Date              │
                         │ + fechaActualizacion: Date         │
                         └────────────┬───────────────────────┘
                                      │
                                      │ 1
                         ┌────────────┼────────────┐
                         │            │            │
                         │ Lidera     │ Asignado a │ Creada por
                         │            │            │
                    0..* │            │ 0..*       │ 0..*
                         ▼            │            ▼
┌───────────────────────────────┐     │    ┌──────────────────────────────┐
│         PROYECTO              │     │    │          TAREA               │
├───────────────────────────────┤     │    ├──────────────────────────────┤
│ + id: String                  │     │    │ + id: String                 │
│ + nombre: String              │◄────┘    │ + titulo: String             │
│ + descripcion: String         │ 1        │ + descripcion: String        │
│ + estado: ProjectStatus       │──────────│ + proyectoId: String (FK)    │
│ + leaderId: String (FK)       │ 0..*     │ + assignedToId: String (FK)  │
│ + collaboratorIds: List<String>│         │ + createdById: String (FK)   │
│ + fechaInicio: Date           │          │ + prioridad: TaskPriority    │
│ + fechaFin: Date              │          │ + estado: TaskStatus         │
│ + fechaCreacion: Date         │          │ + fechaVencimiento: Date     │
│ + fechaActualizacion: Date    │          │ + fechaCreacion: Date        │
└───────────────────────────────┘          │ + fechaActualizacion: Date   │
                                           │ + fechaCompletado: Date      │
                                           └──────────────────────────────┘

┌──────────────────────┐     ┌──────────────────────┐     ┌──────────────────────┐
│    <<enumeration>>   │     │    <<enumeration>>   │     │    <<enumeration>>   │
│        Role          │     │    ProjectStatus     │     │    TaskStatus        │
├──────────────────────┤     ├──────────────────────┤     ├──────────────────────┤
│ + ADMIN              │     │ + PLANNING           │     │ + PENDIENTE          │
│ + LIDER              │     │ + IN_PROGRESS        │     │ + EN_PROGRESO        │
│ + COLABORADOR        │     │ + COMPLETED          │     │ + BLOQUEADA          │
└──────────────────────┘     └──────────────────────┘     │ + EN_REVISION        │
                                                          │ + COMPLETADA         │
┌──────────────────────┐                                  └──────────────────────┘
│    <<enumeration>>   │
│    TaskPriority      │
├──────────────────────┤
│ + BAJA               │
│ + MEDIA              │
│ + ALTA               │
│ + CRITICA            │
└──────────────────────┘
```

---

## 3. Entidades del Dominio

### 3.1 Usuario (User)

#### 3.1.1 Descripción
Representa a cualquier persona que interactúa con el sistema. Los usuarios se clasifican en tres roles: Administrador, Líder y Colaborador.

#### 3.1.2 Atributos

| Atributo | Tipo | Restricciones | Descripción |
|----------|------|---------------|-------------|
| **id** | String | PK, Auto-generado, No nulo | Identificador único del usuario (MongoDB ObjectId) |
| **fullName** | String | No nulo, 2-100 caracteres | Nombre completo del usuario |
| **email** | String | Único, No nulo, Formato email | Correo electrónico (usado para login) |
| **password** | String | No nulo, Encriptado BCrypt | Contraseña encriptada (strength 12) |
| **role** | Role | No nulo, Enum | Rol del usuario en el sistema |
| **isActive** | Boolean | Default: true | Indica si el usuario está activo |
| **createdAt** | LocalDateTime | Auto-generado | Fecha y hora de creación |
| **updatedAt** | LocalDateTime | Auto-actualizado | Fecha y hora de última actualización |

#### 3.1.3 Reglas de Negocio

1. **RN-U01: Unicidad de Email**
   - El email debe ser único en todo el sistema
   - No se permiten dos usuarios con el mismo email

2. **RN-U02: Encriptación de Contraseña**
   - Las contraseñas nunca se almacenan en texto plano
   - Utiliza BCrypt con strength 12
   - Contraseña mínima de 6 caracteres

3. **RN-U03: Roles**
   - Un usuario tiene exactamente un rol
   - Los roles son: ADMIN, LIDER, COLABORADOR
   - El rol determina los permisos del usuario

4. **RN-U04: Usuario Inicial**
   - Al iniciar el sistema por primera vez, se crea automáticamente un usuario admin
   - Credenciales: admin@gestion.com / admin123

5. **RN-U05: Eliminación de Usuarios**
   - Los usuarios no se eliminan físicamente
   - Se realiza un soft delete cambiando `isActive` a `false`

#### 3.1.4 Invariantes
- `email` debe tener formato válido (contener @ y dominio)
- `fullName` no puede estar vacío
- `role` debe ser uno de los valores del enum Role
- `password` debe estar encriptado (no puede ser texto plano)

#### 3.1.5 Métodos de Dominio
```java
public class User {
    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }

    public boolean isLider() {
        return this.role == Role.LIDER;
    }

    public boolean isColaborador() {
        return this.role == Role.COLABORADOR;
    }

    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }
}
```

---

### 3.2 Proyecto (Project)

#### 3.2.1 Descripción
Representa un proyecto de trabajo que agrupa tareas relacionadas. Cada proyecto tiene un líder responsable y puede tener múltiples colaboradores.

#### 3.2.2 Atributos

| Atributo | Tipo | Restricciones | Descripción |
|----------|------|---------------|-------------|
| **id** | String | PK, Auto-generado, No nulo | Identificador único del proyecto |
| **name** | String | No nulo, 3-200 caracteres | Nombre del proyecto |
| **description** | String | No nulo, 10-2000 caracteres | Descripción detallada del proyecto |
| **status** | ProjectStatus | No nulo, Enum | Estado actual del proyecto |
| **leaderId** | String | FK → User.id, No nulo | ID del líder asignado al proyecto |
| **collaboratorIds** | List\<String\> | FK → User.id[], Puede ser vacío | IDs de colaboradores del proyecto |
| **startDate** | LocalDate | Opcional | Fecha de inicio planificada |
| **endDate** | LocalDate | Opcional | Fecha de fin planificada |
| **createdAt** | LocalDateTime | Auto-generado | Fecha y hora de creación |
| **updatedAt** | LocalDateTime | Auto-actualizado | Fecha y hora de última actualización |

#### 3.2.3 Reglas de Negocio

1. **RN-P01: Asignación de Líder**
   - Un proyecto debe tener exactamente un líder
   - El líder debe ser un usuario con rol LIDER
   - El líder puede ver y gestionar todas las tareas del proyecto

2. **RN-P02: Colaboradores**
   - Un proyecto puede tener 0 o más colaboradores
   - Los colaboradores deben ser usuarios con rol COLABORADOR
   - Un colaborador puede estar en múltiples proyectos

3. **RN-P03: Estados del Proyecto**
   - **PLANNING:** Proyecto en fase de planificación
   - **IN_PROGRESS:** Proyecto en ejecución
   - **COMPLETED:** Proyecto finalizado

4. **RN-P04: Transiciones de Estado**
   - PLANNING → IN_PROGRESS: Cuando se comienza a trabajar
   - IN_PROGRESS → COMPLETED: Cuando todas las tareas están completadas
   - No se puede volver a PLANNING desde otros estados

5. **RN-P05: Fechas**
   - Si se especifica `endDate`, debe ser posterior a `startDate`
   - Las fechas son opcionales

6. **RN-P06: Eliminación de Proyectos**
   - Al eliminar un proyecto, se deben eliminar todas sus tareas
   - Se puede implementar soft delete

#### 3.2.4 Invariantes
- `leaderId` debe referenciar a un usuario existente con rol LIDER
- Todos los elementos de `collaboratorIds` deben referenciar usuarios existentes con rol COLABORADOR
- `name` no puede estar vacío
- `status` debe ser uno de los valores del enum ProjectStatus

#### 3.2.5 Métodos de Dominio
```java
public class Project {
    public void addCollaborator(String collaboratorId) {
        if (!this.collaboratorIds.contains(collaboratorId)) {
            this.collaboratorIds.add(collaboratorId);
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void removeCollaborator(String collaboratorId) {
        this.collaboratorIds.remove(collaboratorId);
        this.updatedAt = LocalDateTime.now();
    }

    public void changeStatus(ProjectStatus newStatus) {
        // Validar transición
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isInProgress() {
        return this.status == ProjectStatus.IN_PROGRESS;
    }

    public boolean isCompleted() {
        return this.status == ProjectStatus.COMPLETED;
    }
}
```

---

### 3.3 Tarea (Task)

#### 3.3.1 Descripción
Representa una unidad de trabajo asignada a un colaborador dentro de un proyecto. Las tareas tienen prioridad, estado y fecha de vencimiento.

#### 3.3.2 Atributos

| Atributo | Tipo | Restricciones | Descripción |
|----------|------|---------------|-------------|
| **id** | String | PK, Auto-generado, No nulo | Identificador único de la tarea |
| **title** | String | No nulo, 3-200 caracteres | Título breve de la tarea |
| **description** | String | No nulo, 10-5000 caracteres | Descripción detallada |
| **projectId** | String | FK → Project.id, No nulo | ID del proyecto al que pertenece |
| **assignedToId** | String | FK → User.id, No nulo | ID del colaborador asignado |
| **createdById** | String | FK → User.id, No nulo | ID del usuario que creó la tarea |
| **priority** | TaskPriority | No nulo, Enum | Prioridad de la tarea |
| **status** | TaskStatus | No nulo, Enum | Estado actual de la tarea |
| **dueDate** | LocalDate | Opcional | Fecha límite para completar |
| **createdAt** | LocalDateTime | Auto-generado | Fecha y hora de creación |
| **updatedAt** | LocalDateTime | Auto-actualizado | Fecha y hora de última actualización |
| **completedAt** | LocalDateTime | Opcional | Fecha y hora de completado |

#### 3.3.3 Reglas de Negocio

1. **RN-T01: Asignación de Tareas**
   - Una tarea debe estar asignada a exactamente un colaborador
   - El colaborador asignado debe tener rol COLABORADOR
   - Solo el líder del proyecto puede asignar tareas

2. **RN-T02: Creación de Tareas**
   - Solo el líder del proyecto puede crear tareas
   - Solo el admin puede crear tareas en cualquier proyecto
   - Al crear una tarea, el estado inicial es PENDIENTE

3. **RN-T03: Prioridades**
   - **BAJA:** Tareas de baja prioridad, no urgentes
   - **MEDIA:** Tareas de prioridad normal
   - **ALTA:** Tareas importantes que requieren atención
   - **CRITICA:** Tareas urgentes que bloquean otras tareas

4. **RN-T04: Estados de Tarea**
   - **PENDIENTE:** Tarea creada, no iniciada
   - **EN_PROGRESO:** Colaborador trabajando en la tarea
   - **BLOQUEADA:** Tarea bloqueada por impedimentos
   - **EN_REVISION:** Tarea terminada, esperando revisión
   - **COMPLETADA:** Tarea finalizada y aprobada

5. **RN-T05: Transiciones de Estado Permitidas**
   ```
   PENDIENTE → EN_PROGRESO
   EN_PROGRESO → EN_REVISION | BLOQUEADA | COMPLETADA
   EN_REVISION → EN_PROGRESO | COMPLETADA
   BLOQUEADA → EN_PROGRESO
   COMPLETADA → (estado final, no permite cambios)
   ```

6. **RN-T06: Permisos de Actualización**
   - El colaborador asignado puede actualizar el estado de su tarea
   - El líder del proyecto puede actualizar cualquier tarea
   - El admin puede actualizar cualquier tarea

7. **RN-T07: Fecha de Completado**
   - Cuando una tarea pasa a estado COMPLETADA, se registra `completedAt`
   - Una vez completada, no se puede cambiar a otro estado

8. **RN-T08: Fecha de Vencimiento**
   - La fecha de vencimiento debe ser en el futuro al crear la tarea
   - Es opcional pero recomendada

#### 3.3.4 Invariantes
- `projectId` debe referenciar a un proyecto existente
- `assignedToId` debe referenciar a un usuario con rol COLABORADOR
- `createdById` debe referenciar a un usuario existente
- `title` y `description` no pueden estar vacíos
- `priority` debe ser uno de los valores del enum TaskPriority
- `status` debe ser uno de los valores del enum TaskStatus
- Si `status` es COMPLETADA, `completedAt` debe estar establecido

#### 3.3.5 Métodos de Dominio
```java
public class Task {
    public void changeStatus(TaskStatus newStatus) {
        // Validar transición
        if (!this.status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                "No se puede cambiar de " + this.status + " a " + newStatus
            );
        }

        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();

        if (newStatus == TaskStatus.COMPLETADA) {
            this.completedAt = LocalDateTime.now();
        }
    }

    public boolean isPending() {
        return this.status == TaskStatus.PENDIENTE;
    }

    public boolean isCompleted() {
        return this.status == TaskStatus.COMPLETADA;
    }

    public boolean isOverdue() {
        if (this.dueDate == null || this.isCompleted()) {
            return false;
        }
        return LocalDate.now().isAfter(this.dueDate);
    }

    public boolean isAssignedTo(String userId) {
        return this.assignedToId.equals(userId);
    }
}
```

---

## 4. Enumeraciones

### 4.1 Role (Rol de Usuario)

```java
public enum Role {
    ADMIN("Administrador"),
    LIDER("Líder de Proyecto"),
    COLABORADOR("Colaborador");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
```

**Descripción:**
- **ADMIN:** Control total del sistema, gestiona usuarios y proyectos
- **LIDER:** Gestiona uno o más proyectos, crea tareas
- **COLABORADOR:** Ejecuta tareas asignadas

---

### 4.2 ProjectStatus (Estado del Proyecto)

```java
public enum ProjectStatus {
    PLANNING("En Planificación"),
    IN_PROGRESS("En Progreso"),
    COMPLETED("Completado");

    private final String displayName;

    ProjectStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
```

**Descripción:**
- **PLANNING:** Proyecto en fase de planificación
- **IN_PROGRESS:** Proyecto en ejecución
- **COMPLETED:** Proyecto finalizado

---

### 4.3 TaskStatus (Estado de la Tarea)

```java
public enum TaskStatus {
    PENDIENTE("Pendiente"),
    EN_PROGRESO("En Progreso"),
    BLOQUEADA("Bloqueada"),
    EN_REVISION("En Revisión"),
    COMPLETADA("Completada");

    private final String displayName;

    TaskStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

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

**Descripción:**
- **PENDIENTE:** Tarea creada, no iniciada
- **EN_PROGRESO:** Colaborador trabajando activamente
- **BLOQUEADA:** Tarea con impedimentos
- **EN_REVISION:** Esperando revisión del líder
- **COMPLETADA:** Finalizada y aprobada

---

### 4.4 TaskPriority (Prioridad de la Tarea)

```java
public enum TaskPriority {
    BAJA("Baja", 1),
    MEDIA("Media", 2),
    ALTA("Alta", 3),
    CRITICA("Crítica", 4);

    private final String displayName;
    private final int level;

    TaskPriority(String displayName, int level) {
        this.displayName = displayName;
        this.level = level;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getLevel() {
        return level;
    }

    public boolean isHigherThan(TaskPriority other) {
        return this.level > other.level;
    }
}
```

**Descripción:**
- **BAJA:** Tareas de baja prioridad
- **MEDIA:** Prioridad normal
- **ALTA:** Alta prioridad
- **CRITICA:** Máxima prioridad, urgente

---

## 5. Relaciones entre Entidades

### 5.1 Usuario - Proyecto (Liderazgo)

**Cardinalidad:** 1 Usuario (LIDER) → 0..* Proyectos

**Descripción:** Un líder puede estar asignado a cero o más proyectos. Un proyecto tiene exactamente un líder.

**Atributo de Relación:** `Project.leaderId` → `User.id`

**Restricciones:**
- El usuario debe tener rol LIDER
- No se puede eliminar un usuario que sea líder de proyectos activos

**Operaciones:**
```java
// Asignar líder a proyecto
project.setLeaderId(lider.getId());

// Obtener proyectos de un líder
List<Project> projects = projectRepository.findByLeaderId(liderId);
```

---

### 5.2 Usuario - Proyecto (Colaboración)

**Cardinalidad:** N Usuarios (COLABORADOR) ↔ M Proyectos

**Descripción:** Un colaborador puede estar en múltiples proyectos. Un proyecto puede tener múltiples colaboradores.

**Atributo de Relación:** `Project.collaboratorIds` → `User.id[]`

**Restricciones:**
- Los usuarios deben tener rol COLABORADOR
- Un colaborador no puede estar duplicado en la lista

**Operaciones:**
```java
// Agregar colaborador a proyecto
project.addCollaborator(colaborador.getId());

// Remover colaborador de proyecto
project.removeCollaborator(colaborador.getId());

// Obtener proyectos de un colaborador
List<Project> projects = projectRepository.findByCollaboratorId(colaboradorId);
```

---

### 5.3 Proyecto - Tarea (Composición)

**Cardinalidad:** 1 Proyecto → 0..* Tareas

**Descripción:** Un proyecto puede tener múltiples tareas. Una tarea pertenece a exactamente un proyecto.

**Atributo de Relación:** `Task.projectId` → `Project.id`

**Restricciones:**
- Al eliminar un proyecto, se deben eliminar todas sus tareas (composición fuerte)
- Una tarea no puede existir sin un proyecto

**Operaciones:**
```java
// Obtener tareas de un proyecto
List<Task> tasks = taskRepository.findByProjectId(projectId);

// Eliminar proyecto y sus tareas
taskRepository.deleteByProjectId(projectId);
projectRepository.deleteById(projectId);
```

---

### 5.4 Usuario - Tarea (Asignación)

**Cardinalidad:** 1 Usuario (COLABORADOR) → 0..* Tareas

**Descripción:** Un colaborador puede tener múltiples tareas asignadas. Una tarea está asignada a exactamente un colaborador.

**Atributo de Relación:** `Task.assignedToId` → `User.id`

**Restricciones:**
- El usuario debe tener rol COLABORADOR
- No se puede eliminar un usuario con tareas activas

**Operaciones:**
```java
// Obtener tareas asignadas a un usuario
List<Task> tasks = taskRepository.findByAssignedToId(userId);

// Reasignar tarea
task.setAssignedToId(nuevoColaborador.getId());
```

---

### 5.5 Usuario - Tarea (Creación)

**Cardinalidad:** 1 Usuario (LIDER/ADMIN) → 0..* Tareas

**Descripción:** Un usuario puede crear múltiples tareas. Una tarea tiene exactamente un creador.

**Atributo de Relación:** `Task.createdById` → `User.id`

**Restricciones:**
- Solo usuarios con rol LIDER o ADMIN pueden crear tareas
- Este campo es de auditoría, no se modifica

**Operaciones:**
```java
// Al crear tarea
task.setCreatedById(currentUser.getId());

// Obtener tareas creadas por un usuario
List<Task> tasks = taskRepository.findByCreatedById(userId);
```

---

## 6. Restricciones de Integridad

### 6.1 Restricciones de Clave Primaria
- `User.id` es única
- `Project.id` es única
- `Task.id` es única

### 6.2 Restricciones de Unicidad
- `User.email` es único en toda la colección

### 6.3 Restricciones de Clave Foránea
- `Project.leaderId` debe existir en `User` con rol LIDER
- Todos los elementos de `Project.collaboratorIds` deben existir en `User` con rol COLABORADOR
- `Task.projectId` debe existir en `Project`
- `Task.assignedToId` debe existir en `User` con rol COLABORADOR
- `Task.createdById` debe existir en `User`

### 6.4 Restricciones de Dominio
- `User.email` debe tener formato de email válido
- `User.password` debe estar encriptado con BCrypt
- `Task.dueDate` debe ser en el futuro al crear (opcional)
- `Project.endDate` debe ser posterior a `Project.startDate` (si ambos están definidos)

### 6.5 Restricciones de Negocio
- No se puede cambiar el estado de una tarea a COMPLETADA si no está en un estado válido
- No se puede eliminar un proyecto con tareas activas sin eliminar primero las tareas
- Un usuario no puede tener más de un rol simultáneamente
- Solo el responsable de una tarea (o superior) puede cambiar su estado

---

## 7. Agregados (Domain-Driven Design)

### 7.1 Agregado Usuario
**Raíz del Agregado:** User

**Entidades incluidas:** User (solo)

**Invariantes del Agregado:**
- Email único
- Contraseña encriptada
- Rol válido

**Operaciones del Agregado:**
- Crear usuario
- Actualizar usuario
- Cambiar contraseña
- Activar/Desactivar usuario

---

### 7.2 Agregado Proyecto
**Raíz del Agregado:** Project

**Entidades incluidas:** Project, Task (composición)

**Invariantes del Agregado:**
- Un proyecto tiene exactamente un líder
- Todos los colaboradores son válidos
- Las tareas pertenecen al proyecto

**Operaciones del Agregado:**
- Crear proyecto
- Actualizar proyecto
- Agregar/Remover colaborador
- Cambiar estado del proyecto
- Crear tarea en proyecto
- Eliminar proyecto (elimina tareas)

---

## 8. Eventos del Dominio

### 8.1 Eventos de Usuario
- `UserCreated`: Cuando se crea un nuevo usuario
- `UserUpdated`: Cuando se actualiza un usuario
- `UserDeactivated`: Cuando se desactiva un usuario

### 8.2 Eventos de Proyecto
- `ProjectCreated`: Cuando se crea un proyecto
- `ProjectUpdated`: Cuando se actualiza un proyecto
- `ProjectStatusChanged`: Cuando cambia el estado del proyecto
- `CollaboratorAdded`: Cuando se agrega un colaborador
- `CollaboratorRemoved`: Cuando se remueve un colaborador

### 8.3 Eventos de Tarea
- `TaskCreated`: Cuando se crea una tarea
- `TaskAssigned`: Cuando se asigna una tarea
- `TaskStatusChanged`: Cuando cambia el estado de una tarea
- `TaskCompleted`: Cuando se completa una tarea
- `TaskOverdue`: Cuando una tarea vence sin completarse

---

## 9. Casos de Uso del Dominio

### 9.1 Crear Proyecto

**Precondiciones:**
- Usuario autenticado con rol ADMIN
- Existe al menos un usuario con rol LIDER

**Flujo:**
1. Admin ingresa nombre, descripción y selecciona líder
2. Sistema valida que el líder existe y tiene rol LIDER
3. Sistema crea proyecto con estado PLANNING
4. Sistema asigna el líder al proyecto
5. Sistema persiste el proyecto

**Postcondiciones:**
- Proyecto creado en estado PLANNING
- Líder asignado al proyecto
- Lista de colaboradores vacía

---

### 9.2 Crear Tarea

**Precondiciones:**
- Usuario autenticado con rol LIDER o ADMIN
- Proyecto existe
- Usuario es el líder del proyecto (si es LIDER)

**Flujo:**
1. Líder ingresa título, descripción, prioridad y fecha
2. Líder selecciona colaborador de la lista
3. Sistema valida que el colaborador existe y tiene rol COLABORADOR
4. Sistema crea tarea con estado PENDIENTE
5. Sistema asigna la tarea al colaborador
6. Sistema registra el creador de la tarea
7. Sistema persiste la tarea

**Postcondiciones:**
- Tarea creada en estado PENDIENTE
- Tarea asignada al colaborador
- Creador registrado

---

### 9.3 Actualizar Estado de Tarea

**Precondiciones:**
- Usuario autenticado
- Tarea existe
- Usuario es el responsable, líder del proyecto o admin
- Transición de estado es válida

**Flujo:**
1. Usuario selecciona nueva estado
2. Sistema valida permisos del usuario
3. Sistema valida que la transición es permitida
4. Sistema actualiza el estado de la tarea
5. Si el nuevo estado es COMPLETADA, registra fecha de completado
6. Sistema persiste los cambios

**Postcondiciones:**
- Estado de tarea actualizado
- Fecha de completado registrada (si aplica)
- Fecha de actualización actualizada

---

## 10. Glosario del Dominio

| Término | Definición |
|---------|------------|
| **Usuario** | Persona que interactúa con el sistema |
| **Administrador** | Usuario con control total del sistema |
| **Líder** | Usuario responsable de uno o más proyectos |
| **Colaborador** | Usuario que ejecuta tareas asignadas |
| **Proyecto** | Conjunto de tareas relacionadas con un objetivo común |
| **Tarea** | Unidad de trabajo asignada a un colaborador |
| **Asignación** | Relación entre una tarea y un colaborador |
| **Prioridad** | Nivel de importancia de una tarea |
| **Estado** | Fase actual en el ciclo de vida de un proyecto o tarea |
| **Soft Delete** | Eliminación lógica sin borrar físicamente el registro |
| **Transición de Estado** | Cambio válido de un estado a otro |

---

## 11. Reglas de Validación

### 11.1 Validaciones de Usuario
```java
@NotBlank(message = "El nombre no puede estar vacío")
@Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
private String fullName;

@NotBlank(message = "El email no puede estar vacío")
@Email(message = "El email debe tener un formato válido")
private String email;

@NotBlank(message = "La contraseña no puede estar vacía")
@Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
private String password;

@NotNull(message = "El rol no puede ser nulo")
private Role role;
```

### 11.2 Validaciones de Proyecto
```java
@NotBlank(message = "El nombre del proyecto no puede estar vacío")
@Size(min = 3, max = 200, message = "El nombre debe tener entre 3 y 200 caracteres")
private String name;

@NotBlank(message = "La descripción no puede estar vacía")
@Size(min = 10, max = 2000, message = "La descripción debe tener entre 10 y 2000 caracteres")
private String description;

@NotNull(message = "El líder no puede ser nulo")
private String leaderId;

@NotNull(message = "El estado no puede ser nulo")
private ProjectStatus status;
```

### 11.3 Validaciones de Tarea
```java
@NotBlank(message = "El título no puede estar vacío")
@Size(min = 3, max = 200, message = "El título debe tener entre 3 y 200 caracteres")
private String title;

@NotBlank(message = "La descripción no puede estar vacía")
@Size(min = 10, max = 5000, message = "La descripción debe tener entre 10 y 5000 caracteres")
private String description;

@NotNull(message = "El proyecto no puede ser nulo")
private String projectId;

@NotNull(message = "El responsable no puede ser nulo")
private String assignedToId;

@NotNull(message = "La prioridad no puede ser nula")
private TaskPriority priority;

@NotNull(message = "El estado no puede ser nulo")
private TaskStatus status;
```

---

## 12. Referencias

- **Diagrama de Contexto:** Ver `/documents/Diagrama de Contexto.jpg`
- **Diagrama de Componentes:** Ver `/documents/Diagrama de Componentes.jpg`
- **Modelo Conceptual (Imagen):** Ver `/documents/Modelo Conceptual.jpg`
- **Verificación de Historias de Usuario:** Ver `/VERIFICACION_HISTORIAS_USUARIO.md`

---

**Última Actualización:** 2025-11-06
**Responsable:** Equipo de Desarrollo
**Estado:** Documento Aprobado
