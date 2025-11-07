package com.gestionproyectos.gestion_tareas.enums;

/**
 * Enum TaskStatus
 *
 * Define los diferentes estados por los que puede pasar una tarea en su ciclo de vida.
 * Este enum permite hacer un seguimiento del progreso de las tareas y facilita
 * la generación de reportes y tableros Kanban.
 *
 * CICLO DE VIDA DE UNA TAREA:
 * PENDIENTE → EN_PROGRESO → EN_REVISION → COMPLETADA
 *                    ↓
 *              BLOQUEADA (puede volver a EN_PROGRESO)
 *
 * PATRÓN DE DISEÑO: State Pattern (implícito)
 * - Cada estado representa una fase específica del ciclo de vida
 * - Facilita la implementación de reglas de negocio basadas en estados
 * - Permite validaciones de transiciones entre estados
 *
 * @author [Tu Nombre]
 * @version 1.0
 * @since 2025-11-03
 */
public enum TaskStatus {

    /**
     * PENDIENTE - Estado inicial de una tarea
     *
     * La tarea ha sido creada pero aún no se ha iniciado su ejecución.
     * En este estado, la tarea está esperando que un colaborador comience a trabajar en ella.
     *
     * Transiciones permitidas: → EN_PROGRESO
     */
    PENDIENTE(
        "Pendiente",
        "La tarea está pendiente de iniciar",
        "#94a3b8",  // Color gris para UI (Tailwind slate-400)
        1
    ),

    /**
     * EN_PROGRESO - La tarea está siendo trabajada activamente
     *
     * Un colaborador ha comenzado a trabajar en la tarea.
     * Es el estado más importante para el seguimiento en tiempo real.
     *
     * Transiciones permitidas: → EN_REVISION, → BLOQUEADA, → PENDIENTE (si se pausa)
     */
    EN_PROGRESO(
        "En Progreso",
        "La tarea está siendo trabajada actualmente",
        "#3b82f6",  // Color azul para UI (Tailwind blue-500)
        2
    ),

    /**
     * BLOQUEADA - La tarea no puede continuar por algún impedimento
     *
     * La tarea encontró un obstáculo que impide su progreso.
     * Ejemplos: falta de información, dependencia de otra tarea, problema técnico.
     * Requiere intervención del líder de proyecto.
     *
     * Transiciones permitidas: → EN_PROGRESO (cuando se resuelve el bloqueo)
     */
    BLOQUEADA(
        "Bloqueada",
        "La tarea tiene impedimentos que bloquean su progreso",
        "#ef4444",  // Color rojo para UI (Tailwind red-500)
        3
    ),

    /**
     * EN_REVISION - La tarea está siendo revisada
     *
     * El colaborador ha terminado su trabajo y la tarea está siendo revisada
     * por el líder de proyecto o por otro miembro del equipo.
     *
     * Transiciones permitidas: → COMPLETADA (aprobada), → EN_PROGRESO (requiere cambios)
     */
    EN_REVISION(
        "En Revisión",
        "La tarea está siendo revisada antes de completarse",
        "#f59e0b",  // Color naranja para UI (Tailwind amber-500)
        4
    ),

    /**
     * COMPLETADA - La tarea ha sido finalizada exitosamente
     *
     * La tarea ha pasado todas las revisiones y se considera terminada.
     * Este es el estado final del ciclo de vida de una tarea.
     *
     * Transiciones permitidas: Ninguna (estado final)
     */
    COMPLETADA(
        "Completada",
        "La tarea ha sido completada exitosamente",
        "#10b981",  // Color verde para UI (Tailwind green-500)
        5
    );

    // Atributos del enum
    private final String displayName;    // Nombre amigable para la UI
    private final String description;    // Descripción del estado
    private final String color;          // Color hexadecimal para representación visual
    private final int order;             // Orden lógico en el flujo de trabajo

    /**
     * Constructor privado del enum
     *
     * @param displayName Nombre descriptivo del estado
     * @param description Descripción detallada del estado
     * @param color Color en hexadecimal para la interfaz de usuario
     * @param order Orden numérico del estado en el flujo (para ordenamiento)
     */
    TaskStatus(String displayName, String description, String color, int order) {
        this.displayName = displayName;
        this.description = description;
        this.color = color;
        this.order = order;
    }

    /**
     * Obtiene el nombre descriptivo del estado
     *
     * @return String - Nombre para mostrar en la UI
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
     * Útil para mostrar badges de colores en la interfaz según el estado.
     * Los colores siguen la convención: gris=pendiente, azul=progreso,
     * rojo=bloqueada, naranja=revisión, verde=completada
     *
     * @return String - Código hexadecimal del color
     */
    public String getColor() {
        return color;
    }

    /**
     * Obtiene el orden del estado en el flujo de trabajo
     *
     * @return int - Número de orden (1 a 5)
     */
    public int getOrder() {
        return order;
    }

    /**
     * Verifica si la tarea está en un estado activo (en progreso o revisión)
     *
     * Estados activos son aquellos donde la tarea está siendo trabajada.
     * Útil para filtrar tareas que requieren atención inmediata.
     *
     * @return boolean - true si está EN_PROGRESO o EN_REVISION
     */
    public boolean isActive() {
        return this == EN_PROGRESO || this == EN_REVISION;
    }

    /**
     * Verifica si la tarea ha sido completada
     *
     * @return boolean - true si el estado es COMPLETADA
     */
    public boolean isCompleted() {
        return this == COMPLETADA;
    }

    /**
     * Verifica si la tarea está bloqueada
     *
     * @return boolean - true si el estado es BLOQUEADA
     */
    public boolean isBlocked() {
        return this == BLOQUEADA;
    }

    /**
     * Verifica si la tarea está pendiente de inicio
     *
     * @return boolean - true si el estado es PENDIENTE
     */
    public boolean isPending() {
        return this == PENDIENTE;
    }

    /**
     * Valida si es posible hacer la transición al estado objetivo
     *
     * Este método implementa las reglas de negocio para las transiciones de estado.
     * No todas las transiciones están permitidas, por ejemplo, no se puede pasar
     * directamente de PENDIENTE a COMPLETADA sin pasar por EN_PROGRESO.
     *
     * MATRIZ DE TRANSICIONES PERMITIDAS:
     * PENDIENTE      → EN_PROGRESO
     * EN_PROGRESO    → EN_REVISION, BLOQUEADA, PENDIENTE
     * BLOQUEADA      → EN_PROGRESO
     * EN_REVISION    → COMPLETADA, EN_PROGRESO
     * COMPLETADA     → (ninguna transición permitida)
     *
     * @param targetStatus Estado al que se quiere transicionar
     * @return boolean - true si la transición es válida
     */
    public boolean canTransitionTo(TaskStatus targetStatus) {
        // No se puede transicionar al mismo estado
        if (this == targetStatus) {
            return false;
        }

        // Definir las transiciones permitidas según el estado actual
        switch (this) {
            case PENDIENTE:
                // Solo puede pasar a EN_PROGRESO
                return targetStatus == EN_PROGRESO;

            case EN_PROGRESO:
                // Puede pasar a EN_REVISION, BLOQUEADA, o volver a PENDIENTE
                return targetStatus == EN_REVISION ||
                       targetStatus == BLOQUEADA ||
                       targetStatus == PENDIENTE;

            case BLOQUEADA:
                // Solo puede volver a EN_PROGRESO cuando se resuelva el bloqueo
                return targetStatus == EN_PROGRESO;

            case EN_REVISION:
                // Puede completarse o volver a EN_PROGRESO si necesita cambios
                return targetStatus == COMPLETADA ||
                       targetStatus == EN_PROGRESO;

            case COMPLETADA:
                // Estado final: no permite transiciones
                return false;

            default:
                return false;
        }
    }

    /**
     * Obtiene la lista de estados válidos a los que se puede transicionar
     *
     * Este método es útil para mostrar en la UI solo las opciones válidas
     * de transición, mejorando la experiencia de usuario al prevenir
     * selecciones inválidas.
     *
     * @return List<TaskStatus> - Lista de estados válidos para transicionar
     */
    public java.util.List<TaskStatus> getValidNextStates() {
        java.util.List<TaskStatus> validStates = new java.util.ArrayList<>();

        for (TaskStatus status : TaskStatus.values()) {
            if (this.canTransitionTo(status)) {
                validStates.add(status);
            }
        }

        return validStates;
    }

    /**
     * Convierte una cadena de texto a un valor del enum TaskStatus
     *
     * @param statusStr El nombre del estado como String
     * @return TaskStatus - El enum correspondiente
     * @throws IllegalArgumentException si el String no corresponde a ningún estado válido
     */
    public static TaskStatus fromString(String statusStr) {
        if (statusStr == null || statusStr.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado no puede ser nulo o vacío");
        }

        try {
            return TaskStatus.valueOf(statusStr.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Estado de tarea inválido: '" + statusStr + "'. " +
                "Los estados válidos son: PENDIENTE, EN_PROGRESO, BLOQUEADA, EN_REVISION, COMPLETADA"
            );
        }
    }

    /**
     * Obtiene una clase CSS de Tailwind según el estado
     *
     * Este método es útil para aplicar estilos dinámicos en las plantillas Thymeleaf.
     * Retorna clases de badges con colores apropiados.
     *
     * @return String - Clases CSS de Tailwind para el badge
     */
    public String getBadgeClass() {
        switch (this) {
            case PENDIENTE:
                return "bg-slate-100 text-slate-800 border border-slate-300";
            case EN_PROGRESO:
                return "bg-blue-100 text-blue-800 border border-blue-300";
            case BLOQUEADA:
                return "bg-red-100 text-red-800 border border-red-300";
            case EN_REVISION:
                return "bg-amber-100 text-amber-800 border border-amber-300";
            case COMPLETADA:
                return "bg-green-100 text-green-800 border border-green-300";
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
