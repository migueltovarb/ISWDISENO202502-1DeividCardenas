package com.gestionproyectos.gestion_tareas.enums;

/**
 * Enum Role
 *
 * Este enum define los tres roles de usuario disponibles en el sistema de gestión de proyectos.
 * En Spring Security, los roles se usan para controlar el acceso a diferentes recursos y funcionalidades.
 *
 * PATRÓN DE DISEÑO: Enum Type Pattern
 * - Proporciona un conjunto fijo y seguro de constantes para los roles
 * - Evita errores de escritura y valores inválidos
 * - Facilita el mantenimiento y la refactorización del código
 *
 * @author [Tu Nombre]
 * @version 1.0
 * @since 2025-11-03
 */
public enum Role {

    /**
     * ADMIN - Administrador del sistema
     *
     * Permisos:
     * - Crear, editar y eliminar usuarios
     * - Gestionar todos los proyectos y tareas del sistema
     * - Asignar roles a otros usuarios
     * - Acceder a paneles de administración
     * - Ver reportes y estadísticas globales
     * - Configurar parámetros del sistema
     */
    ADMIN("Administrador", "Acceso completo al sistema"),

    /**
     * LIDER - Líder de Proyecto
     *
     * Permisos:
     * - Crear y gestionar proyectos propios
     * - Asignar tareas a colaboradores
     * - Ver el progreso de sus proyectos
     * - Editar y eliminar tareas de sus proyectos
     * - Invitar colaboradores a sus proyectos
     * - Ver reportes de sus proyectos
     */
    LIDER("Líder de Proyecto", "Gestiona proyectos y asigna tareas"),

    /**
     * COLABORADOR - Colaborador del Proyecto
     *
     * Permisos:
     * - Ver proyectos a los que fue asignado
     * - Actualizar el estado de sus tareas asignadas
     * - Comentar en tareas
     * - Marcar tareas como completadas
     * - Ver detalles de proyectos donde participa
     * - No puede crear proyectos ni asignar tareas a otros
     */
    COLABORADOR("Colaborador", "Ejecuta tareas asignadas");

    // Atributos del enum
    // Cada rol tiene un nombre descriptivo y una descripción de sus capacidades
    private final String displayName;  // Nombre amigable para mostrar en la UI
    private final String description;  // Descripción de las capacidades del rol

    /**
     * Constructor privado del enum
     *
     * Los constructores de los enums siempre son privados porque los valores
     * son instancias únicas creadas automáticamente por la JVM.
     *
     * @param displayName Nombre descriptivo del rol para mostrar en la interfaz
     * @param description Descripción de las capacidades y permisos del rol
     */
    Role(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Obtiene el nombre descriptivo del rol
     *
     * Este método se usa en las vistas Thymeleaf para mostrar un nombre
     * más amigable en lugar del nombre técnico del enum.
     *
     * Ejemplo: Role.ADMIN.getDisplayName() retorna "Administrador"
     *
     * @return String - El nombre descriptivo del rol
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Obtiene la descripción de las capacidades del rol
     *
     * Útil para mostrar tooltips o información adicional en la interfaz
     * sobre qué puede hacer cada tipo de usuario.
     *
     * @return String - La descripción de los permisos del rol
     */
    public String getDescription() {
        return description;
    }

    /**
     * Obtiene el nombre del rol con el prefijo "ROLE_" requerido por Spring Security
     *
     * Spring Security internamente usa el prefijo "ROLE_" para diferenciar
     * roles de authorities (permisos más granulares).
     *
     * Ejemplo: Role.ADMIN.getRoleName() retorna "ROLE_ADMIN"
     *
     * @return String - El nombre del rol con el prefijo ROLE_
     */
    public String getRoleName() {
        return "ROLE_" + this.name();
    }

    /**
     * Verifica si el rol actual es ADMIN
     *
     * Método de conveniencia para simplificar las validaciones en el código.
     * Es más legible usar role.isAdmin() que role == Role.ADMIN
     *
     * @return boolean - true si el rol es ADMIN, false en caso contrario
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }

    /**
     * Verifica si el rol actual es LIDER
     *
     * @return boolean - true si el rol es LIDER, false en caso contrario
     */
    public boolean isLider() {
        return this == LIDER;
    }

    /**
     * Verifica si el rol actual es COLABORADOR
     *
     * @return boolean - true si el rol es COLABORADOR, false en caso contrario
     */
    public boolean isColaborador() {
        return this == COLABORADOR;
    }

    /**
     * Convierte una cadena de texto a un valor del enum Role
     *
     * Este método es útil cuando recibimos el rol como String desde un formulario
     * o una petición HTTP y necesitamos convertirlo al enum correspondiente.
     *
     * Ejemplo de uso:
     * Role role = Role.fromString("ADMIN");  // Retorna Role.ADMIN
     * Role role = Role.fromString("admin");  // También retorna Role.ADMIN (case insensitive)
     *
     * @param roleStr El nombre del rol como String
     * @return Role - El enum correspondiente
     * @throws IllegalArgumentException si el String no corresponde a ningún rol válido
     */
    public static Role fromString(String roleStr) {
        if (roleStr == null || roleStr.trim().isEmpty()) {
            throw new IllegalArgumentException("El rol no puede ser nulo o vacío");
        }

        try {
            // Convertimos el string a mayúsculas y buscamos el enum correspondiente
            return Role.valueOf(roleStr.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            // Si no se encuentra el rol, lanzamos una excepción más descriptiva
            throw new IllegalArgumentException(
                "Rol inválido: '" + roleStr + "'. Los roles válidos son: ADMIN, LIDER, COLABORADOR"
            );
        }
    }

    /**
     * Representación en String del rol
     *
     * Sobrescribimos toString() para retornar el nombre descriptivo
     * en lugar del nombre técnico del enum.
     *
     * @return String - El nombre descriptivo del rol
     */
    @Override
    public String toString() {
        return this.displayName;
    }
}
