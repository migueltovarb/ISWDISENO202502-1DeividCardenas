package com.gestionproyectos.gestion_tareas.model;

import com.gestionproyectos.gestion_tareas.enums.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad Project - Representa un proyecto en el sistema
 *
 * Un proyecto es una iniciativa que agrupa múltiples tareas relacionadas.
 * Cada proyecto tiene un líder responsable y puede tener múltiples colaboradores.
 *
 * CARACTERÍSTICAS PRINCIPALES:
 * - Cada proyecto tiene un único líder (Usuario con rol LIDER o ADMIN)
 * - Puede tener múltiples colaboradores asignados
 * - Agrupa múltiples tareas relacionadas
 * - Tiene fechas de inicio y fin estimadas
 * - Mantiene un estado durante su ciclo de vida
 *
 * RELACIONES:
 * - Uno-a-Uno con User (líder): Un proyecto tiene un líder
 * - Muchos-a-Muchos con User (colaboradores): Varios usuarios pueden colaborar
 * - Uno-a-Muchos con Task: Un proyecto tiene múltiples tareas
 *
 * PATRÓN DE DISEÑO: Aggregate Root (DDD)
 * - El Project es la raíz del agregado que incluye sus tareas
 * - Todas las operaciones sobre tareas pasan por el proyecto
 * - Garantiza la consistencia de los datos del proyecto
 *
 * @author [Tu Nombre]
 * @version 1.0
 * @since 2025-11-03
 */
@Document(collection = "projects")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    /**
     * Identificador único del proyecto
     *
     * Generado automáticamente por MongoDB como ObjectId.
     */
    @Id
    private String id;

    /**
     * Nombre del proyecto
     *
     * Debe ser descriptivo y único dentro del contexto organizacional.
     * Ejemplos:
     * - "Sistema de Gestión de Inventarios"
     * - "App Móvil de Delivery"
     * - "Migración a Microservicios"
     */
    private String name;

    /**
     * Descripción detallada del proyecto
     *
     * Incluye:
     * - Objetivos del proyecto
     * - Alcance
     * - Entregables esperados
     * - Contexto o justificación del proyecto
     *
     * Puede contener formato Markdown para mejor visualización en la UI.
     */
    private String description;

    /**
     * ID del usuario líder del proyecto
     *
     * RELACIÓN: Referencia al campo User.id
     *
     * El líder tiene responsabilidades especiales:
     * - Crear y asignar tareas
     * - Invitar colaboradores
     * - Actualizar el estado del proyecto
     * - Ver reportes y métricas
     * - Definir fechas y prioridades
     *
     * IMPORTANTE: Solo usuarios con rol LIDER o ADMIN pueden ser líderes de proyecto.
     * Esta validación debe hacerse en la capa de servicio.
     */
    private String leaderId;

    /**
     * IDs de los usuarios colaboradores del proyecto
     *
     * RELACIÓN: Colección de referencias a User.id
     *
     * Los colaboradores:
     * - Pueden ver el proyecto
     * - Trabajan en las tareas que se les asignan
     * - Actualizan el estado de sus tareas
     * - No pueden modificar la configuración del proyecto
     *
     * NOTA: El líder NO se incluye en esta lista (está en leaderId).
     * Si el líder también trabaja en tareas, debe estar en esta lista.
     *
     * MongoDB almacena esto como un array de Strings:
     * ["userId1", "userId2", "userId3"]
     */
    @Builder.Default
    private Set<String> collaboratorIds = new HashSet<>();

    /**
     * Estado actual del proyecto
     *
     * Valores posibles (definidos en ProjectStatus enum):
     * - PLANIFICACION: Proyecto en fase de planificación inicial
     * - EN_PROGRESO: Proyecto activamente en ejecución
     * - PAUSADO: Proyecto temporalmente suspendido
     * - COMPLETADO: Proyecto finalizado exitosamente
     * - CANCELADO: Proyecto cancelado
     *
     * El estado determina:
     * - Qué acciones pueden realizarse sobre el proyecto
     * - Filtros en dashboards y reportes
     * - Cálculos de métricas (solo proyectos EN_PROGRESO en estadísticas activas)
     */
    @Builder.Default
    private ProjectStatus status = ProjectStatus.PLANIFICACION;

    /**
     * Fecha de inicio planificada del proyecto
     *
     * Indica cuándo se espera que comience la ejecución del proyecto.
     * Puede ser una fecha futura durante la planificación.
     *
     * Uso:
     * - Planificación de recursos
     * - Cálculo de duración del proyecto
     * - Ordenamiento de proyectos en timeline
     *
     * Tipo LocalDate: Solo fecha, sin hora (ej: 2025-01-15)
     */
    private LocalDate startDate;

    /**
     * Fecha de fin estimada del proyecto
     *
     * Fecha objetivo para la finalización del proyecto.
     * Es una estimación que puede ajustarse según el progreso.
     *
     * Validación: endDate debe ser posterior a startDate.
     * Esta validación debe hacerse en la capa de servicio o mediante validadores.
     *
     * Uso:
     * - Establecer plazos
     * - Calcular progreso en base al tiempo
     * - Alertas de proyectos próximos a vencer
     */
    private LocalDate endDate;

    /**
     * Porcentaje de progreso del proyecto (0-100)
     *
     * CÁLCULO AUTOMÁTICO:
     * Este valor se calcula basándose en las tareas completadas:
     * progreso = (tareasCompletadas / totalTareas) * 100
     *
     * Por ejemplo:
     * - 10 tareas totales, 7 completadas: 70% de progreso
     * - 5 tareas totales, 2 completadas: 40% de progreso
     *
     * IMPORTANTE: Este campo se actualiza automáticamente cuando:
     * - Se crea una nueva tarea
     * - Se completa una tarea
     * - Se elimina una tarea
     *
     * Debe recalcularse en el servicio mediante un método updateProgress().
     */
    @Builder.Default
    private int progress = 0;

    /**
     * Color del proyecto para visualización en la UI
     *
     * Formato: Código hexadecimal (#RRGGBB) o nombre de clase Tailwind.
     *
     * Usos:
     * - Badges en listados de proyectos
     * - Identificación visual rápida en dashboards
     * - Gráficos y reportes
     *
     * Ejemplos:
     * - "#3b82f6" (azul)
     * - "#10b981" (verde)
     * - "#f59e0b" (naranja)
     *
     * Si es null, la UI puede asignar un color por defecto.
     */
    private String color;

    /**
     * Tags o etiquetas del proyecto
     *
     * Permite categorizar y filtrar proyectos.
     *
     * Ejemplos de tags:
     * - "frontend", "backend", "mobile"
     * - "urgente", "cliente-prioritario"
     * - "v2.0", "migración", "refactor"
     *
     * Usos:
     * - Filtros en listados de proyectos
     * - Búsquedas más específicas
     * - Agrupación en reportes
     *
     * MongoDB almacena como array de Strings: ["frontend", "urgente", "v2.0"]
     */
    @Builder.Default
    private Set<String> tags = new HashSet<>();

    /**
     * Indica si el proyecto está archivado
     *
     * ARCHIVADO vs ELIMINADO:
     * - Archivado: El proyecto no está activo pero se mantiene visible en sección "Archivados"
     * - Eliminado: El proyecto se marca como eliminado (soft delete)
     *
     * Proyectos archivados:
     * - No aparecen en el dashboard principal
     * - Se pueden consultar en sección especial
     * - Mantienen toda su información
     * - Pueden ser restaurados
     *
     * Casos de uso:
     * - Proyectos completados hace mucho tiempo
     * - Proyectos cancelados pero con información valiosa
     * - Proyectos de referencia histórica
     */
    @Builder.Default
    private boolean archived = false;

    /**
     * Fecha y hora de creación del proyecto
     *
     * Se establece automáticamente cuando se crea el proyecto.
     * Requiere @EnableMongoAuditing en la configuración.
     */
    @CreatedDate
    private LocalDateTime createdAt;

    /**
     * Fecha y hora de la última modificación
     *
     * Se actualiza automáticamente cada vez que se modifica el proyecto.
     * Incluye cambios en: nombre, descripción, estado, colaboradores, etc.
     */
    @LastModifiedDate
    private LocalDateTime updatedAt;

    // ==================== MÉTODOS DE UTILIDAD ====================

    /**
     * Añade un colaborador al proyecto
     *
     * Validaciones en capa de servicio:
     * - El usuario debe existir
     * - No debe ser el mismo que el líder (o sí, según reglas de negocio)
     * - No debe estar ya en la lista
     *
     * @param userId ID del usuario colaborador a añadir
     */
    public void addCollaborator(String userId) {
        if (userId != null && !userId.trim().isEmpty()) {
            this.collaboratorIds.add(userId);
        }
    }

    /**
     * Elimina un colaborador del proyecto
     *
     * IMPORTANTE: Al eliminar un colaborador, considerar:
     * - ¿Qué pasa con las tareas que tiene asignadas?
     * - Opción 1: Reasignar tareas al líder
     * - Opción 2: Marcar tareas como sin asignar
     * - Opción 3: No permitir eliminar si tiene tareas activas
     *
     * Esta lógica debe estar en el servicio, no en la entidad.
     *
     * @param userId ID del usuario colaborador a eliminar
     */
    public void removeCollaborator(String userId) {
        this.collaboratorIds.remove(userId);
    }

    /**
     * Verifica si un usuario es colaborador del proyecto
     *
     * @param userId ID del usuario a verificar
     * @return boolean - true si el usuario es colaborador
     */
    public boolean hasCollaborator(String userId) {
        return this.collaboratorIds.contains(userId);
    }

    /**
     * Verifica si un usuario es el líder del proyecto
     *
     * @param userId ID del usuario a verificar
     * @return boolean - true si el usuario es el líder
     */
    public boolean isLeader(String userId) {
        return this.leaderId != null && this.leaderId.equals(userId);
    }

    /**
     * Verifica si un usuario tiene acceso al proyecto
     * (es líder o colaborador)
     *
     * @param userId ID del usuario a verificar
     * @return boolean - true si el usuario tiene acceso
     */
    public boolean hasAccess(String userId) {
        return isLeader(userId) || hasCollaborator(userId);
    }

    /**
     * Obtiene el número total de miembros del proyecto
     * (líder + colaboradores)
     *
     * @return int - Número total de miembros
     */
    public int getTotalMembers() {
        // Líder (1) + colaboradores
        return 1 + collaboratorIds.size();
    }

    /**
     * Añade un tag al proyecto
     *
     * @param tag Tag a añadir
     */
    public void addTag(String tag) {
        if (tag != null && !tag.trim().isEmpty()) {
            this.tags.add(tag.trim().toLowerCase());
        }
    }

    /**
     * Elimina un tag del proyecto
     *
     * @param tag Tag a eliminar
     */
    public void removeTag(String tag) {
        this.tags.remove(tag);
    }

    /**
     * Verifica si el proyecto tiene un tag específico
     *
     * @param tag Tag a buscar
     * @return boolean - true si el proyecto tiene ese tag
     */
    public boolean hasTag(String tag) {
        return this.tags.contains(tag);
    }

    /**
     * Actualiza el porcentaje de progreso
     *
     * Este método debe ser llamado desde el servicio cuando:
     * - Se completa una tarea
     * - Se añade una nueva tarea
     * - Se elimina una tarea
     *
     * @param completedTasks Número de tareas completadas
     * @param totalTasks Número total de tareas
     */
    public void updateProgress(int completedTasks, int totalTasks) {
        if (totalTasks == 0) {
            this.progress = 0;
        } else {
            this.progress = (int) Math.round((completedTasks * 100.0) / totalTasks);
        }
    }

    /**
     * Verifica si el proyecto está activo
     * (no archivado y en estado EN_PROGRESO)
     *
     * @return boolean - true si el proyecto está activo
     */
    public boolean isActive() {
        return !archived && status == ProjectStatus.EN_PROGRESO;
    }

    /**
     * Verifica si el proyecto está completado
     *
     * @return boolean - true si el estado es COMPLETADO
     */
    public boolean isCompleted() {
        return status == ProjectStatus.COMPLETADO;
    }

    /**
     * Verifica si el proyecto está cancelado
     *
     * @return boolean - true si el estado es CANCELADO
     */
    public boolean isCancelled() {
        return status == ProjectStatus.CANCELADO;
    }

    /**
     * Verifica si el proyecto está retrasado
     *
     * Un proyecto está retrasado si:
     * - La fecha de fin es anterior a hoy
     * - El proyecto NO está completado
     *
     * @return boolean - true si el proyecto está retrasado
     */
    public boolean isOverdue() {
        if (endDate == null || isCompleted()) {
            return false;
        }
        return LocalDate.now().isAfter(endDate);
    }

    /**
     * Archiva el proyecto
     *
     * Los proyectos archivados no aparecen en listados principales
     * pero mantienen toda su información.
     */
    public void archive() {
        this.archived = true;
    }

    /**
     * Desarchivar el proyecto
     *
     * Restaura el proyecto a los listados principales.
     */
    public void unarchive() {
        this.archived = false;
    }

    /**
     * Cambia el estado del proyecto
     *
     * IMPORTANTE: Antes de cambiar el estado, validar la transición
     * usando ProjectStatus.canTransitionTo() en la capa de servicio.
     *
     * @param newStatus Nuevo estado del proyecto
     */
    public void changeStatus(ProjectStatus newStatus) {
        this.status = newStatus;
    }
}
