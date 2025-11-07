package com.gestionproyectos.gestion_tareas.enums;

/**
 * Enum ProjectStatus
 *
 * Define los diferentes estados del ciclo de vida de un proyecto.
 * Permite gestionar y hacer seguimiento de los proyectos desde su planificación
 * hasta su finalización.
 *
 * CICLO DE VIDA DE UN PROYECTO:
 * PLANIFICACION → EN_PROGRESO → PAUSADO (opcional) → COMPLETADO
 *                                   ↓
 *                              CANCELADO
 *
 * BUENAS PRÁCTICAS:
 * - Los proyectos deben iniciar en PLANIFICACION
 * - Solo se puede marcar como COMPLETADO cuando todas las tareas estén terminadas
 * - PAUSADO permite suspender temporalmente sin perder el progreso
 * - CANCELADO es un estado final para proyectos que no continuarán
 *
 * @author [Tu Nombre]
 * @version 1.0
 * @since 2025-11-03
 */
public enum ProjectStatus {

    /**
     * PLANIFICACION - Estado inicial del proyecto
     *
     * El proyecto está siendo planificado, definiendo objetivos, alcance y recursos.
     * En esta etapa se crean las tareas principales y se asignan responsables.
     *
     * Características:
     * - No se han iniciado tareas de ejecución
     * - Se están definiendo los entregables
     * - Se asignan los colaboradores al equipo
     *
     * Transiciones permitidas: → EN_PROGRESO, → CANCELADO
     */
    PLANIFICACION(
        "En Planificación",
        "El proyecto está en fase de planificación y definición",
        "#9ca3af",  // Color gris (Tailwind gray-400)
        false,      // No está activo
        1
    ),

    /**
     * EN_PROGRESO - El proyecto está siendo ejecutado activamente
     *
     * El equipo está trabajando activamente en las tareas del proyecto.
     * Es el estado donde se realiza el mayor esfuerzo y seguimiento.
     *
     * Características:
     * - Las tareas están siendo ejecutadas
     * - Se hace seguimiento diario/semanal del progreso
     * - Los colaboradores están activos en sus asignaciones
     *
     * Transiciones permitidas: → PAUSADO, → COMPLETADO, → CANCELADO
     */
    EN_PROGRESO(
        "En Progreso",
        "El proyecto está en ejecución activa",
        "#3b82f6",  // Color azul (Tailwind blue-500)
        true,       // Está activo
        2
    ),

    /**
     * PAUSADO - El proyecto ha sido suspendido temporalmente
     *
     * El proyecto se ha pausado por razones externas (falta de recursos,
     * cambio de prioridades, espera de aprobaciones, etc.).
     * No se pierde el progreso, se puede reanudar en cualquier momento.
     *
     * Características:
     * - El trabajo se ha detenido temporalmente
     * - Se mantiene toda la información y progreso
     * - Puede reactivarse cuando se resuelvan los impedimentos
     *
     * Transiciones permitidas: → EN_PROGRESO, → CANCELADO
     */
    PAUSADO(
        "Pausado",
        "El proyecto está temporalmente suspendido",
        "#f59e0b",  // Color naranja (Tailwind amber-500)
        false,      // No está activo
        3
    ),

    /**
     * COMPLETADO - El proyecto ha sido finalizado exitosamente
     *
     * Todas las tareas han sido completadas y el proyecto cumplió sus objetivos.
     * Este es el estado final deseado para un proyecto exitoso.
     *
     * Características:
     * - Todas las tareas están completadas
     * - Los objetivos fueron alcanzados
     * - Se han generado los entregables finales
     * - Estado final (no permite más transiciones)
     *
     * Transiciones permitidas: Ninguna (estado final)
     */
    COMPLETADO(
        "Completado",
        "El proyecto ha sido completado exitosamente",
        "#10b981",  // Color verde (Tailwind green-500)
        false,      // Ya no está activo
        4
    ),

    /**
     * CANCELADO - El proyecto ha sido cancelado
     *
     * El proyecto no continuará por decisión del equipo o stakeholders.
     * Este es un estado final donde se documenta el motivo de la cancelación.
     *
     * Características:
     * - El proyecto no se completará
     * - Se mantiene el historial para referencia
     * - Se documentan las razones de la cancelación
     * - Estado final (no permite más transiciones)
     *
     * Transiciones permitidas: Ninguna (estado final)
     */
    CANCELADO(
        "Cancelado",
        "El proyecto ha sido cancelado",
        "#ef4444",  // Color rojo (Tailwind red-500)
        false,      // No está activo
        5
    );

    // Atributos del enum
    private final String displayName;     // Nombre amigable para la UI
    private final String description;     // Descripción del estado
    private final String color;           // Color hexadecimal para visualización
    private final boolean active;         // Indica si el proyecto está en ejecución activa
    private final int order;              // Orden lógico del estado

    /**
     * Constructor privado del enum
     *
     * @param displayName Nombre descriptivo del estado
     * @param description Descripción detallada del estado
     * @param color Color en hexadecimal para la UI
     * @param active Indica si el proyecto está activamente en ejecución
     * @param order Orden numérico del estado
     */
    ProjectStatus(String displayName, String description, String color, boolean active, int order) {
        this.displayName = displayName;
        this.description = description;
        this.color = color;
        this.active = active;
        this.order = order;
    }

    /**
     * Obtiene el nombre descriptivo del estado
     *
     * @return String - Nombre para mostrar en la interfaz
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Obtiene la descripción del estado
     *
     * @return String - Descripción detallada
     */
    public String getDescription() {
        return description;
    }

    /**
     * Obtiene el color asociado al estado
     *
     * @return String - Código hexadecimal del color
     */
    public String getColor() {
        return color;
    }

    /**
     * Verifica si el proyecto está en ejecución activa
     *
     * Solo EN_PROGRESO se considera activo. Esto es útil para:
     * - Filtrar proyectos que requieren seguimiento diario
     * - Generar reportes de proyectos en curso
     * - Dashboard de proyectos activos
     *
     * @return boolean - true si el estado es EN_PROGRESO
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Obtiene el orden del estado
     *
     * @return int - Número de orden (1 a 5)
     */
    public int getOrder() {
        return order;
    }

    /**
     * Verifica si el proyecto está en planificación
     *
     * @return boolean - true si el estado es PLANIFICACION
     */
    public boolean isPlanning() {
        return this == PLANIFICACION;
    }

    /**
     * Verifica si el proyecto está en progreso
     *
     * @return boolean - true si el estado es EN_PROGRESO
     */
    public boolean isInProgress() {
        return this == EN_PROGRESO;
    }

    /**
     * Verifica si el proyecto está pausado
     *
     * @return boolean - true si el estado es PAUSADO
     */
    public boolean isPaused() {
        return this == PAUSADO;
    }

    /**
     * Verifica si el proyecto ha sido completado
     *
     * @return boolean - true si el estado es COMPLETADO
     */
    public boolean isCompleted() {
        return this == COMPLETADO;
    }

    /**
     * Verifica si el proyecto ha sido cancelado
     *
     * @return boolean - true si el estado es CANCELADO
     */
    public boolean isCancelled() {
        return this == CANCELADO;
    }

    /**
     * Verifica si el estado es final (no permite más transiciones)
     *
     * Los estados finales son COMPLETADO y CANCELADO.
     * Una vez que un proyecto alcanza estos estados, no puede cambiar.
     *
     * @return boolean - true si es COMPLETADO o CANCELADO
     */
    public boolean isFinalState() {
        return this == COMPLETADO || this == CANCELADO;
    }

    /**
     * Valida si es posible hacer la transición al estado objetivo
     *
     * Este método implementa las reglas de negocio para las transiciones de estado.
     *
     * MATRIZ DE TRANSICIONES PERMITIDAS:
     * PLANIFICACION  → EN_PROGRESO, CANCELADO
     * EN_PROGRESO    → PAUSADO, COMPLETADO, CANCELADO
     * PAUSADO        → EN_PROGRESO, CANCELADO
     * COMPLETADO     → (ninguna transición)
     * CANCELADO      → (ninguna transición)
     *
     * @param targetStatus Estado al que se quiere transicionar
     * @return boolean - true si la transición es válida
     */
    public boolean canTransitionTo(ProjectStatus targetStatus) {
        // No se puede transicionar al mismo estado
        if (this == targetStatus) {
            return false;
        }

        // Los estados finales no permiten transiciones
        if (this.isFinalState()) {
            return false;
        }

        // Definir las transiciones permitidas según el estado actual
        switch (this) {
            case PLANIFICACION:
                // Puede iniciar el proyecto o cancelarse antes de empezar
                return targetStatus == EN_PROGRESO || targetStatus == CANCELADO;

            case EN_PROGRESO:
                // Puede pausarse, completarse o cancelarse
                return targetStatus == PAUSADO ||
                       targetStatus == COMPLETADO ||
                       targetStatus == CANCELADO;

            case PAUSADO:
                // Puede reanudarse o cancelarse
                return targetStatus == EN_PROGRESO || targetStatus == CANCELADO;

            default:
                return false;
        }
    }

    /**
     * Convierte una cadena de texto a un valor del enum ProjectStatus
     *
     * @param statusStr El nombre del estado como String
     * @return ProjectStatus - El enum correspondiente
     * @throws IllegalArgumentException si el String no corresponde a ningún estado válido
     */
    public static ProjectStatus fromString(String statusStr) {
        if (statusStr == null || statusStr.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado no puede ser nulo o vacío");
        }

        try {
            return ProjectStatus.valueOf(statusStr.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Estado de proyecto inválido: '" + statusStr + "'. " +
                "Los estados válidos son: PLANIFICACION, EN_PROGRESO, PAUSADO, COMPLETADO, CANCELADO"
            );
        }
    }

    /**
     * Obtiene una clase CSS de Tailwind según el estado
     *
     * Este método facilita la aplicación de estilos dinámicos en Thymeleaf.
     * Retorna clases para badges con colores apropiados.
     *
     * @return String - Clases CSS de Tailwind para el badge
     */
    public String getBadgeClass() {
        switch (this) {
            case PLANIFICACION:
                return "bg-gray-100 text-gray-800 border border-gray-300";
            case EN_PROGRESO:
                return "bg-blue-100 text-blue-800 border border-blue-300";
            case PAUSADO:
                return "bg-amber-100 text-amber-800 border border-amber-300";
            case COMPLETADO:
                return "bg-green-100 text-green-800 border border-green-300";
            case CANCELADO:
                return "bg-red-100 text-red-800 border border-red-300";
            default:
                return "bg-gray-100 text-gray-800 border border-gray-300";
        }
    }

    /**
     * Representación en String del estado
     *
     * @return String - El nombre descriptivo del estado
     */
    @Override
    public String toString() {
        return this.displayName;
    }
}
