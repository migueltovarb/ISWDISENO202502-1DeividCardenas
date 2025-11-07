package com.gestionproyectos.gestion_tareas.model;

import com.gestionproyectos.gestion_tareas.enums.TaskPriority;
import com.gestionproyectos.gestion_tareas.enums.TaskStatus;
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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Task - Representa una tarea dentro de un proyecto
 *
 * Una tarea es una unidad de trabajo asignable que forma parte de un proyecto.
 * Las tareas son creadas por líderes de proyecto y asignadas a colaboradores.
 *
 * CARACTERÍSTICAS PRINCIPALES:
 * - Pertenece a un único proyecto
 * - Puede ser asignada a un colaborador específico
 * - Tiene estado, prioridad y fechas de vencimiento
 * - Permite comentarios para comunicación del equipo
 * - Rastrea el tiempo estimado vs tiempo real
 *
 * RELACIONES:
 * - Muchos-a-Uno con Project: Muchas tareas pertenecen a un proyecto
 * - Muchos-a-Uno con User (asignado): Muchas tareas pueden ser asignadas a un usuario
 * - Muchos-a-Uno con User (creador): Un usuario crea múltiples tareas
 *
 * CICLO DE VIDA:
 * 1. Creación: Líder crea la tarea (estado PENDIENTE)
 * 2. Asignación: Se asigna a un colaborador
 * 3. Ejecución: Colaborador trabaja (estado EN_PROGRESO)
 * 4. Revisión: Se revisa el trabajo (estado EN_REVISION)
 * 5. Finalización: Se marca como COMPLETADA
 *
 * @author [Tu Nombre]
 * @version 1.0
 * @since 2025-11-03
 */
@Document(collection = "tasks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    /**
     * Identificador único de la tarea
     *
     * Generado automáticamente por MongoDB.
     */
    @Id
    private String id;

    /**
     * Título de la tarea
     *
     * Debe ser conciso y descriptivo de la acción a realizar.
     *
     * Buenas prácticas:
     * - Usar verbos en infinitivo: "Implementar", "Corregir", "Diseñar"
     * - Ser específico: "Implementar login con OAuth2"
     * - Evitar ambigüedades: "Arreglar el bug" ❌ vs "Corregir error de validación en formulario de registro" ✅
     *
     * Ejemplos:
     * - "Implementar autenticación con JWT"
     * - "Corregir bug de paginación en tabla de usuarios"
     * - "Diseñar mockups de pantalla de perfil"
     */
    private String title;

    /**
     * Descripción detallada de la tarea
     *
     * Debe incluir:
     * - Contexto: ¿Por qué es necesaria esta tarea?
     * - Requisitos: ¿Qué se debe hacer exactamente?
     * - Criterios de aceptación: ¿Cuándo se considera completa?
     * - Recursos: Enlaces, documentos, ejemplos de referencia
     *
     * Puede usar Markdown para formatear:
     * - Listas de subtareas
     * - Código de ejemplo
     * - Enlaces a documentación
     * - Imágenes de referencia
     *
     * Ejemplo:
     * ```
     * Implementar el endpoint de autenticación con las siguientes especificaciones:
     *
     * - POST /api/auth/login
     * - Recibe: email, password
     * - Retorna: JWT token + información del usuario
     *
     * Criterios de aceptación:
     * - [ ] Validar credenciales contra la BD
     * - [ ] Generar JWT con expiración de 24h
     * - [ ] Manejar errores de credenciales inválidas
     * - [ ] Incluir tests unitarios
     * ```
     */
    private String description;

    /**
     * ID del proyecto al que pertenece la tarea
     *
     * RELACIÓN: Referencia a Project.id
     *
     * Esta es una relación obligatoria: toda tarea DEBE pertenecer a un proyecto.
     * No pueden existir tareas "huérfanas" sin proyecto.
     *
     * IMPORTANTE: Al eliminar un proyecto, se deben eliminar todas sus tareas
     * o moverlas a otro proyecto (según reglas de negocio).
     */
    private String projectId;

    /**
     * ID del usuario al que está asignada la tarea
     *
     * RELACIÓN: Referencia a User.id
     *
     * Puede ser null si la tarea aún no ha sido asignada.
     * Solo colaboradores del proyecto pueden ser asignados a tareas.
     *
     * VALIDACIÓN (en capa de servicio):
     * - El usuario debe ser colaborador del proyecto (o el líder)
     * - El usuario debe estar activo
     * - El usuario debe tener rol COLABORADOR o superior
     *
     * Cuando se asigna una tarea:
     * - El usuario recibe una notificación (si hay sistema de notificaciones)
     * - La tarea aparece en su dashboard personal
     */
    private String assignedToId;

    /**
     * ID del usuario que creó la tarea
     *
     * RELACIÓN: Referencia a User.id
     *
     * Generalmente es el líder del proyecto o un ADMIN.
     * Este campo es inmutable después de la creación.
     *
     * Usos:
     * - Auditoría: saber quién creó cada tarea
     * - Responsabilidad: contacto para dudas sobre la tarea
     * - Métricas: tareas creadas por usuario
     */
    private String createdById;

    /**
     * Estado actual de la tarea
     *
     * Valores posibles (definidos en TaskStatus enum):
     * - PENDIENTE: Tarea creada pero no iniciada
     * - EN_PROGRESO: Se está trabajando activamente
     * - BLOQUEADA: Hay impedimentos para continuar
     * - EN_REVISION: Trabajo completado, pendiente de revisión
     * - COMPLETADA: Tarea finalizada y aprobada
     *
     * El estado determina:
     * - Visualización en tableros Kanban
     * - Filtros en listados de tareas
     * - Cálculo de progreso del proyecto
     * - Métricas de productividad
     */
    @Builder.Default
    private TaskStatus status = TaskStatus.PENDIENTE;

    /**
     * Prioridad de la tarea
     *
     * Valores posibles (definidos en TaskPriority enum):
     * - BAJA: Puede hacerse cuando haya tiempo
     * - MEDIA: Trabajo regular (valor por defecto)
     * - ALTA: Requiere atención preferencial
     * - CRITICA: Máxima urgencia, atención inmediata
     *
     * La prioridad afecta:
     * - Orden en el backlog y tableros
     * - Asignación de recursos
     * - SLA (tiempo esperado de resolución)
     * - Alertas y notificaciones
     */
    @Builder.Default
    private TaskPriority priority = TaskPriority.MEDIA;

    /**
     * Fecha límite de la tarea
     *
     * Fecha en la que la tarea debe estar completada.
     * Es opcional pero altamente recomendable para planificación.
     *
     * Validaciones:
     * - Debe estar dentro del rango del proyecto (startDate - endDate)
     * - Debe ser futura (o presente) al momento de creación
     *
     * Usos:
     * - Planificación de sprints
     * - Alertas de tareas próximas a vencer
     * - Identificación de tareas vencidas
     * - Métricas de cumplimiento de plazos
     */
    private LocalDate dueDate;

    /**
     * Tiempo estimado para completar la tarea (en horas)
     *
     * Estimación inicial de cuántas horas tomará completar la tarea.
     * Es útil para:
     * - Planificación de capacidad del equipo
     * - Distribución de carga de trabajo
     * - Comparación con tiempo real (estimado vs real)
     * - Mejora de estimaciones futuras
     *
     * Ejemplo:
     * - Tarea simple: 2-4 horas
     * - Tarea media: 8-16 horas (1-2 días)
     * - Tarea compleja: 32+ horas (varios días)
     *
     * Si es null, indica que no se ha estimado.
     */
    private Double estimatedHours;

    /**
     * Tiempo real dedicado a la tarea (en horas)
     *
     * Tiempo acumulado que se ha trabajado en la tarea.
     * Puede actualizarse de diferentes formas:
     * - Manualmente por el colaborador
     * - Automáticamente con time tracking
     * - Calculado en base a registros de tiempo
     *
     * Comparación con estimatedHours:
     * - Si realHours > estimatedHours → La tarea tomó más de lo esperado
     * - Si realHours < estimatedHours → La tarea se completó más rápido
     *
     * Útil para:
     * - Métricas de productividad
     * - Facturación (si aplica)
     * - Mejora de estimaciones futuras
     * - Reportes de tiempo
     */
    @Builder.Default
    private Double realHours = 0.0;

    /**
     * Tags o etiquetas de la tarea
     *
     * Permiten categorizar y filtrar tareas.
     *
     * Ejemplos de tags:
     * - Tecnología: "frontend", "backend", "database", "api"
     * - Tipo: "bug", "feature", "refactor", "documentation"
     * - Módulo: "autenticación", "dashboard", "reportes"
     * - Sprint: "sprint-1", "sprint-2"
     *
     * Usos:
     * - Filtros avanzados en listados
     * - Búsquedas específicas
     * - Agrupación en reportes
     * - Organización en tableros Kanban
     */
    @Builder.Default
    private List<String> tags = new ArrayList<>();

    /**
     * Comentarios de la tarea
     *
     * Lista de comentarios para comunicación del equipo.
     * Cada comentario es un objeto embebido (no una colección separada).
     *
     * Estructura de un comentario (clase Comment embebida):
     * - userId: Quién escribió el comentario
     * - text: Contenido del comentario
     * - createdAt: Cuándo se escribió
     *
     * Usos:
     * - Preguntas sobre la tarea
     * - Actualizaciones de progreso
     * - Compartir información relevante
     * - Coordinación del equipo
     *
     * NOTA: Se almacenan como documentos embebidos en MongoDB, no en colección separada.
     */
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    /**
     * IDs de archivos adjuntos
     *
     * Referencias a archivos asociados a la tarea.
     * Pueden ser:
     * - URLs de archivos subidos al servidor
     * - IDs de documentos en GridFS (MongoDB)
     * - Enlaces externos (Google Drive, Dropbox, etc.)
     *
     * Ejemplos de archivos:
     * - Diseños (mockups, wireframes)
     * - Documentos de requisitos
     * - Capturas de pantalla de bugs
     * - Código de ejemplo
     */
    @Builder.Default
    private List<String> attachments = new ArrayList<>();

    /**
     * Motivo de bloqueo (si la tarea está bloqueada)
     *
     * Solo es relevante cuando status = BLOQUEADA.
     * Describe el impedimento que bloquea el progreso.
     *
     * Ejemplos:
     * - "Esperando aprobación del cliente"
     * - "Bloqueado por tarea #123"
     * - "Falta acceso a la base de datos de producción"
     * - "Esperando definición de requisitos"
     *
     * Este campo ayuda al líder a:
     * - Identificar y resolver bloqueos
     * - Priorizar desbloqueos
     * - Hacer seguimiento de impedimentos
     */
    private String blockReason;

    /**
     * Fecha y hora de creación de la tarea
     *
     * Se establece automáticamente al crear la tarea.
     */
    @CreatedDate
    private LocalDateTime createdAt;

    /**
     * Fecha y hora de la última modificación
     *
     * Se actualiza automáticamente en cada cambio:
     * - Cambio de estado
     * - Actualización de descripción
     * - Añadir comentarios
     * - Modificar fechas o prioridad
     */
    @LastModifiedDate
    private LocalDateTime updatedAt;

    /**
     * Fecha y hora en que se completó la tarea
     *
     * Se establece cuando el estado cambia a COMPLETADA.
     *
     * Usos:
     * - Calcular tiempo total de la tarea (completedAt - createdAt)
     * - Métricas de velocidad del equipo
     * - Reportes de tareas completadas por período
     * - Análisis de productividad
     */
    private LocalDateTime completedAt;

    /**
     * Orden de la tarea dentro del proyecto
     *
     * Se usa para ordenar las tareas visualmente en el drag & drop.
     * Valores más bajos aparecen primero.
     *
     * Se establece automáticamente al crear la tarea.
     */
    @Builder.Default
    private Integer displayOrder = 0;

    // ==================== CLASE EMBEBIDA: Comment ====================

    /**
     * Clase Comment - Representa un comentario en la tarea
     *
     * Se almacena como documento embebido dentro de Task, no como colección separada.
     * Esto optimiza las consultas porque los comentarios se cargan con la tarea.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Comment {
        /**
         * ID del usuario que escribió el comentario
         */
        private String userId;

        /**
         * Contenido del comentario
         * Puede incluir Markdown para formato
         */
        private String text;

        /**
         * Fecha y hora del comentario
         */
        @Builder.Default
        private LocalDateTime createdAt = LocalDateTime.now();
    }

    // ==================== MÉTODOS DE UTILIDAD ====================

    /**
     * Añade un comentario a la tarea
     *
     * @param userId ID del usuario que comenta
     * @param text Contenido del comentario
     */
    public void addComment(String userId, String text) {
        Comment comment = Comment.builder()
                .userId(userId)
                .text(text)
                .createdAt(LocalDateTime.now())
                .build();
        this.comments.add(comment);
    }

    /**
     * Añade un tag a la tarea
     *
     * @param tag Tag a añadir
     */
    public void addTag(String tag) {
        if (tag != null && !tag.trim().isEmpty()) {
            String normalizedTag = tag.trim().toLowerCase();
            if (!this.tags.contains(normalizedTag)) {
                this.tags.add(normalizedTag);
            }
        }
    }

    /**
     * Elimina un tag de la tarea
     *
     * @param tag Tag a eliminar
     */
    public void removeTag(String tag) {
        this.tags.remove(tag);
    }

    /**
     * Verifica si la tarea tiene un tag específico
     *
     * @param tag Tag a buscar
     * @return boolean - true si la tarea tiene ese tag
     */
    public boolean hasTag(String tag) {
        return this.tags.contains(tag);
    }

    /**
     * Añade un archivo adjunto
     *
     * @param attachmentId ID o URL del archivo
     */
    public void addAttachment(String attachmentId) {
        if (attachmentId != null && !attachmentId.trim().isEmpty()) {
            this.attachments.add(attachmentId);
        }
    }

    /**
     * Elimina un archivo adjunto
     *
     * @param attachmentId ID o URL del archivo a eliminar
     */
    public void removeAttachment(String attachmentId) {
        this.attachments.remove(attachmentId);
    }

    /**
     * Verifica si la tarea está asignada a un usuario específico
     *
     * @param userId ID del usuario a verificar
     * @return boolean - true si la tarea está asignada a ese usuario
     */
    public boolean isAssignedTo(String userId) {
        return this.assignedToId != null && this.assignedToId.equals(userId);
    }

    /**
     * Verifica si la tarea fue creada por un usuario específico
     *
     * @param userId ID del usuario a verificar
     * @return boolean - true si ese usuario creó la tarea
     */
    public boolean isCreatedBy(String userId) {
        return this.createdById != null && this.createdById.equals(userId);
    }

    /**
     * Verifica si la tarea está completada
     *
     * @return boolean - true si el estado es COMPLETADA
     */
    public boolean isCompleted() {
        return this.status == TaskStatus.COMPLETADA;
    }

    /**
     * Verifica si la tarea está bloqueada
     *
     * @return boolean - true si el estado es BLOQUEADA
     */
    public boolean isBlocked() {
        return this.status == TaskStatus.BLOQUEADA;
    }

    /**
     * Verifica si la tarea está en progreso
     *
     * @return boolean - true si el estado es EN_PROGRESO
     */
    public boolean isInProgress() {
        return this.status == TaskStatus.EN_PROGRESO;
    }

    /**
     * Verifica si la tarea está vencida
     *
     * Una tarea está vencida si:
     * - Tiene fecha de vencimiento (dueDate)
     * - La fecha de vencimiento ya pasó
     * - NO está completada
     *
     * @return boolean - true si la tarea está vencida
     */
    public boolean isOverdue() {
        if (dueDate == null || isCompleted()) {
            return false;
        }
        return LocalDate.now().isAfter(dueDate);
    }

    /**
     * Calcula los días restantes hasta la fecha de vencimiento
     *
     * @return long - Días restantes (negativo si está vencida, 0 si vence hoy)
     */
    public long getDaysUntilDue() {
        if (dueDate == null) {
            return Long.MAX_VALUE; // Sin fecha límite
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
    }

    /**
     * Verifica si la tarea está próxima a vencer (menos de 3 días)
     *
     * @return boolean - true si vence en menos de 3 días
     */
    public boolean isDueSoon() {
        long daysUntilDue = getDaysUntilDue();
        return daysUntilDue >= 0 && daysUntilDue <= 3;
    }

    /**
     * Calcula la variación entre tiempo estimado y real
     *
     * Retorna un porcentaje:
     * - Positivo: Se tardó más de lo estimado
     * - Negativo: Se tardó menos de lo estimado
     * - 0: Coincide con la estimación
     *
     * Ejemplo:
     * - Estimado: 10h, Real: 12h → +20%
     * - Estimado: 10h, Real: 8h → -20%
     *
     * @return Double - Porcentaje de variación (null si no hay estimación)
     */
    public Double getTimeVariancePercentage() {
        if (estimatedHours == null || estimatedHours == 0) {
            return null;
        }
        double variance = ((realHours - estimatedHours) / estimatedHours) * 100;
        return Math.round(variance * 100.0) / 100.0; // Redondear a 2 decimales
    }

    /**
     * Cambia el estado de la tarea
     *
     * IMPORTANTE: Antes de cambiar el estado, validar la transición
     * usando TaskStatus.canTransitionTo() en la capa de servicio.
     *
     * Si cambia a COMPLETADA, establecer completedAt.
     *
     * @param newStatus Nuevo estado de la tarea
     */
    public void changeStatus(TaskStatus newStatus) {
        this.status = newStatus;
        if (newStatus == TaskStatus.COMPLETADA) {
            this.completedAt = LocalDateTime.now();
        }
    }

    /**
     * Bloquea la tarea con un motivo
     *
     * @param reason Motivo del bloqueo
     */
    public void block(String reason) {
        this.status = TaskStatus.BLOQUEADA;
        this.blockReason = reason;
    }

    /**
     * Desbloquea la tarea y la vuelve a EN_PROGRESO
     */
    public void unblock() {
        if (this.status == TaskStatus.BLOQUEADA) {
            this.status = TaskStatus.EN_PROGRESO;
            this.blockReason = null;
        }
    }

    /**
     * Incrementa las horas reales trabajadas
     *
     * @param hours Horas a añadir
     */
    public void addRealHours(double hours) {
        if (hours > 0) {
            this.realHours += hours;
        }
    }
}
