package com.gestionproyectos.gestion_tareas.enums;

/**
 * Enum TaskPriority
 *
 * Define los niveles de prioridad que puede tener una tarea.
 * La prioridad ayuda a los equipos a organizar y enfocar su trabajo
 * en las tareas más importantes o urgentes.
 *
 * PRINCIPIO DE GESTIÓN:
 * Las prioridades deben asignarse considerando:
 * 1. URGENCIA: ¿Qué tan pronto debe completarse?
 * 2. IMPACTO: ¿Qué tan importante es para el proyecto?
 * 3. DEPENDENCIAS: ¿Otras tareas dependen de esta?
 *
 * MATRIZ DE EISENHOWER:
 * CRITICA = Urgente + Importante (hacer primero)
 * ALTA    = Importante pero no urgente (planificar)
 * MEDIA   = Urgente pero no importante (delegar si es posible)
 * BAJA    = Ni urgente ni importante (hacer después)
 *
 * @author [Tu Nombre]
 * @version 1.0
 * @since 2025-11-03
 */
public enum TaskPriority {

    /**
     * BAJA - Prioridad baja
     *
     * Tareas que son deseables pero no esenciales.
     * Pueden postergarse si hay tareas más importantes.
     *
     * Características:
     * - No son urgentes
     * - No son críticas para el proyecto
     * - Pueden completarse cuando haya tiempo disponible
     * - Mejoras menores, optimizaciones opcionales
     *
     * Ejemplos:
     * - Mejoras estéticas menores
     * - Documentación adicional
     * - Refactorización opcional
     * - Tareas de "nice to have"
     */
    BAJA(
        "Baja",
        "Tarea de prioridad baja, puede completarse cuando haya tiempo disponible",
        "#6b7280",  // Color gris (Tailwind gray-500)
        1,          // Nivel de prioridad
        24 * 7      // SLA sugerido: 7 días
    ),

    /**
     * MEDIA - Prioridad media (por defecto)
     *
     * Tareas importantes para el proyecto pero sin urgencia inmediata.
     * Representan el trabajo regular del día a día.
     *
     * Características:
     * - Trabajo estándar del proyecto
     * - Deben completarse en el sprint/iteración actual
     * - No bloquean a otras tareas críticas
     * - Balance entre importancia y urgencia
     *
     * Ejemplos:
     * - Desarrollo de features planificadas
     * - Corrección de bugs no críticos
     * - Tareas de mantenimiento regular
     * - Implementaciones según roadmap
     */
    MEDIA(
        "Media",
        "Tarea de prioridad media, debe completarse en el plazo estimado",
        "#3b82f6",  // Color azul (Tailwind blue-500)
        2,          // Nivel de prioridad
        24 * 3      // SLA sugerido: 3 días
    ),

    /**
     * ALTA - Prioridad alta
     *
     * Tareas importantes que deben ser atendidas pronto.
     * Tienen impacto significativo en el proyecto.
     *
     * Características:
     * - Alto impacto en el proyecto
     * - Deben completarse antes que las tareas MEDIA y BAJA
     * - Requieren atención preferencial
     * - Pueden afectar entregas importantes
     *
     * Ejemplos:
     * - Features críticas del sprint
     * - Bugs que afectan funcionalidad importante
     * - Tareas que bloquean a otros miembros del equipo
     * - Requisitos de stakeholders importantes
     */
    ALTA(
        "Alta",
        "Tarea de prioridad alta, requiere atención preferencial",
        "#f59e0b",  // Color naranja (Tailwind amber-500)
        3,          // Nivel de prioridad
        24          // SLA sugerido: 1 día
    ),

    /**
     * CRITICA - Prioridad crítica
     *
     * Tareas de máxima urgencia e importancia.
     * Requieren atención inmediata y tienen el mayor impacto.
     *
     * Características:
     * - Máxima urgencia
     * - Bloquea el progreso del proyecto si no se resuelve
     * - Requiere atención inmediata
     * - Puede requerir escalar o asignar más recursos
     *
     * Ejemplos:
     * - Bugs críticos en producción
     * - Problemas de seguridad
     * - Bloqueos que impiden trabajar a todo el equipo
     * - Entregas con deadline inmediato
     * - Incidentes que afectan a usuarios
     */
    CRITICA(
        "Crítica",
        "Tarea de prioridad crítica, requiere atención inmediata",
        "#ef4444",  // Color rojo (Tailwind red-500)
        4,          // Nivel de prioridad
        4           // SLA sugerido: 4 horas
    );

    // Atributos del enum
    private final String displayName;      // Nombre amigable para la UI
    private final String description;      // Descripción del nivel de prioridad
    private final String color;            // Color hexadecimal para visualización
    private final int level;               // Nivel numérico de prioridad (1-4)
    private final int suggestedSlaHours;   // SLA sugerido en horas

    /**
     * Constructor privado del enum
     *
     * @param displayName Nombre descriptivo de la prioridad
     * @param description Descripción detallada
     * @param color Color en hexadecimal para la UI
     * @param level Nivel numérico de prioridad (1=baja, 4=crítica)
     * @param suggestedSlaHours Tiempo sugerido para completar (SLA) en horas
     */
    TaskPriority(String displayName, String description, String color, int level, int suggestedSlaHours) {
        this.displayName = displayName;
        this.description = description;
        this.color = color;
        this.level = level;
        this.suggestedSlaHours = suggestedSlaHours;
    }

    /**
     * Obtiene el nombre descriptivo de la prioridad
     *
     * @return String - Nombre para mostrar en la UI
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Obtiene la descripción de la prioridad
     *
     * @return String - Descripción detallada
     */
    public String getDescription() {
        return description;
    }

    /**
     * Obtiene el color asociado a la prioridad
     *
     * @return String - Código hexadecimal del color
     */
    public String getColor() {
        return color;
    }

    /**
     * Obtiene el nivel numérico de prioridad
     *
     * Útil para ordenar tareas por prioridad de forma ascendente o descendente.
     * Nivel más alto = mayor prioridad
     *
     * @return int - Nivel de 1 (BAJA) a 4 (CRITICA)
     */
    public int getLevel() {
        return level;
    }

    /**
     * Obtiene el SLA (Service Level Agreement) sugerido en horas
     *
     * El SLA sugiere en cuántas horas debería completarse una tarea
     * según su prioridad.
     *
     * @return int - Número de horas sugeridas
     */
    public int getSuggestedSlaHours() {
        return suggestedSlaHours;
    }

    /**
     * Obtiene el SLA sugerido en formato legible
     *
     * Convierte las horas del SLA a un formato más amigable.
     *
     * @return String - SLA en formato legible (ej: "4 horas", "3 días")
     */
    public String getSuggestedSlaFormatted() {
        if (suggestedSlaHours < 24) {
            return suggestedSlaHours + " horas";
        } else {
            int days = suggestedSlaHours / 24;
            return days + (days == 1 ? " día" : " días");
        }
    }

    /**
     * Verifica si la prioridad es crítica
     *
     * @return boolean - true si la prioridad es CRITICA
     */
    public boolean isCritical() {
        return this == CRITICA;
    }

    /**
     * Verifica si la prioridad es alta o crítica
     *
     * Útil para filtrar tareas que requieren atención urgente.
     *
     * @return boolean - true si la prioridad es ALTA o CRITICA
     */
    public boolean isHighOrCritical() {
        return this == ALTA || this == CRITICA;
    }

    /**
     * Verifica si la prioridad es baja
     *
     * @return boolean - true si la prioridad es BAJA
     */
    public boolean isLow() {
        return this == BAJA;
    }

    /**
     * Compara esta prioridad con otra por nivel
     *
     * Útil para ordenar colecciones de tareas por prioridad.
     * Nota: No sobrescribe Enum.compareTo() que es final.
     *
     * @param other Otra prioridad para comparar
     * @return int - negativo si esta prioridad es menor,
     *               0 si son iguales,
     *               positivo si esta prioridad es mayor
     */
    public int compareByLevel(TaskPriority other) {
        return Integer.compare(this.level, other.level);
    }

    /**
     * Verifica si esta prioridad es mayor que otra
     *
     * @param other Otra prioridad para comparar
     * @return boolean - true si esta prioridad es mayor
     */
    public boolean isHigherThan(TaskPriority other) {
        return this.level > other.level;
    }

    /**
     * Verifica si esta prioridad es menor que otra
     *
     * @param other Otra prioridad para comparar
     * @return boolean - true si esta prioridad es menor
     */
    public boolean isLowerThan(TaskPriority other) {
        return this.level < other.level;
    }

    /**
     * Convierte una cadena de texto a un valor del enum TaskPriority
     *
     * @param priorityStr El nombre de la prioridad como String
     * @return TaskPriority - El enum correspondiente
     * @throws IllegalArgumentException si el String no corresponde a ninguna prioridad válida
     */
    public static TaskPriority fromString(String priorityStr) {
        if (priorityStr == null || priorityStr.trim().isEmpty()) {
            throw new IllegalArgumentException("La prioridad no puede ser nula o vacía");
        }

        try {
            return TaskPriority.valueOf(priorityStr.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Prioridad de tarea inválida: '" + priorityStr + "'. " +
                "Las prioridades válidas son: BAJA, MEDIA, ALTA, CRITICA"
            );
        }
    }

    /**
     * Obtiene la prioridad por defecto
     *
     * Cuando se crea una nueva tarea sin especificar prioridad,
     * se asigna automáticamente MEDIA.
     *
     * @return TaskPriority - MEDIA como valor por defecto
     */
    public static TaskPriority getDefault() {
        return MEDIA;
    }

    /**
     * Convierte un nivel numérico a TaskPriority
     *
     * @param level Nivel numérico (1-4)
     * @return TaskPriority - El enum correspondiente
     * @throws IllegalArgumentException si el nivel no está en el rango 1-4
     */
    public static TaskPriority fromLevel(int level) {
        switch (level) {
            case 1: return BAJA;
            case 2: return MEDIA;
            case 3: return ALTA;
            case 4: return CRITICA;
            default:
                throw new IllegalArgumentException(
                    "Nivel de prioridad inválido: " + level + ". Debe estar entre 1 y 4."
                );
        }
    }

    /**
     * Obtiene una clase CSS de Tailwind según la prioridad
     *
     * Este método facilita la aplicación de estilos dinámicos en Thymeleaf.
     * Retorna clases para badges con colores apropiados.
     *
     * @return String - Clases CSS de Tailwind para el badge
     */
    public String getBadgeClass() {
        switch (this) {
            case BAJA:
                return "bg-gray-100 text-gray-800 border border-gray-300";
            case MEDIA:
                return "bg-blue-100 text-blue-800 border border-blue-300";
            case ALTA:
                return "bg-amber-100 text-amber-800 border border-amber-300";
            case CRITICA:
                return "bg-red-100 text-red-800 border border-red-300";
            default:
                return "bg-gray-100 text-gray-800 border border-gray-300";
        }
    }

    /**
     * Obtiene un ícono Unicode según la prioridad
     *
     * Útil para mostrar indicadores visuales en la UI.
     *
     * @return String - Emoji o símbolo Unicode
     */
    public String getIcon() {
        switch (this) {
            case BAJA:
                return "⬇";  // Flecha abajo
            case MEDIA:
                return "➡";  // Flecha derecha
            case ALTA:
                return "⬆";  // Flecha arriba
            case CRITICA:
                return "🔥"; // Fuego (urgente)
            default:
                return "•";
        }
    }

    /**
     * Representación en String de la prioridad
     *
     * @return String - El nombre descriptivo de la prioridad
     */
    @Override
    public String toString() {
        return this.displayName;
    }
}
