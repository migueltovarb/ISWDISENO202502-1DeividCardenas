package com.gestionproyectos.gestion_tareas.model;

import com.gestionproyectos.gestion_tareas.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad User - Representa un usuario del sistema
 *
 * Esta clase es una entidad de dominio que se mapea a la colección "users" en MongoDB.
 * Almacena la información de autenticación y autorización de los usuarios.
 *
 * ANOTACIONES PRINCIPALES:
 * - @Document: Indica que esta clase es un documento de MongoDB
 * - @CompoundIndexes: Define índices compuestos para optimizar queries frecuentes
 * - @Data: (Lombok) Genera getters, setters, toString, equals y hashCode
 * - @Builder: (Lombok) Implementa el patrón Builder para construcción de objetos
 * - @NoArgsConstructor: (Lombok) Genera constructor sin parámetros (requerido por MongoDB)
 * - @AllArgsConstructor: (Lombok) Genera constructor con todos los parámetros
 *
 * ÍNDICES COMPUESTOS:
 * - {active, role}: Optimiza búsquedas de usuarios activos por rol
 * - {active, createdAt}: Optimiza listados de usuarios activos ordenados por fecha
 * - {role, createdAt}: Optimiza búsquedas por rol ordenadas por fecha
 *
 * PATRÓN DE DISEÑO: Entity Pattern
 * - Representa una entidad del dominio del negocio
 * - Se mapea directamente a una colección en la base de datos
 * - Contiene lógica de negocio básica relacionada con el usuario
 *
 * SEGURIDAD:
 * - La contraseña se almacena hasheada usando BCrypt
 * - El email es único en el sistema (índice único)
 * - Implementa soft delete con el campo 'active'
 *
 * @author [Tu Nombre]
 * @version 1.0
 * @since 2025-11-03
 */
@Document(collection = "users")  // Nombre de la colección en MongoDB
@CompoundIndexes({
    @CompoundIndex(name = "active_role_idx", def = "{'active': 1, 'role': 1}"),
    @CompoundIndex(name = "active_createdAt_idx", def = "{'active': 1, 'createdAt': -1}"),
    @CompoundIndex(name = "role_createdAt_idx", def = "{'role': 1, 'createdAt': -1}")
})
@Data                             // Lombok: genera getters, setters, toString, equals, hashCode
@Builder                          // Lombok: implementa patrón Builder
@NoArgsConstructor               // Lombok: constructor sin parámetros (necesario para Spring Data)
@AllArgsConstructor              // Lombok: constructor con todos los parámetros
public class User {

    /**
     * Identificador único del usuario
     *
     * @Id: Marca este campo como el identificador primario del documento MongoDB.
     * MongoDB genera automáticamente un ObjectId si no se especifica un valor.
     *
     * Tipo: String porque MongoDB usa ObjectId que se representa como String en Java.
     * Ejemplo de valor: "507f1f77bcf86cd799439011"
     */
    @Id
    private String id;

    /**
     * Nombre completo del usuario
     *
     * Este campo almacena el nombre y apellido del usuario.
     * Se usa para mostrar en la interfaz y en reportes.
     *
     * @Indexed: Crea un índice en MongoDB para optimizar búsquedas por nombre.
     * Mejora el rendimiento de búsquedas parciales (LIKE queries).
     *
     * Ejemplo: "Juan Pérez García"
     */
    @Indexed
    private String fullName;

    /**
     * Correo electrónico del usuario (usado como username para login)
     *
     * @Indexed(unique = true): Crea un índice único en MongoDB, garantizando
     * que no haya dos usuarios con el mismo email. Esto es fundamental para:
     * 1. Autenticación: El email se usa como username
     * 2. Performance: Las búsquedas por email son muy rápidas
     * 3. Integridad: Previene duplicados en la base de datos
     *
     * IMPORTANTE: Este campo se usa en Spring Security como el 'username'
     */
    @Indexed(unique = true)
    private String email;

    /**
     * Contraseña hasheada del usuario
     *
     * SEGURIDAD CRÍTICA:
     * - Este campo NUNCA almacena la contraseña en texto plano
     * - Se almacena usando BCryptPasswordEncoder de Spring Security
     * - El hash tiene formato: $2a$10$[22 caracteres de salt][31 caracteres de hash]
     * - Es prácticamente imposible revertir el hash a la contraseña original
     *
     * Ejemplo de valor: "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
     *
     * BUENA PRÁCTICA: Cuando se serializa el usuario a JSON (para APIs),
     * este campo debe excluirse usando @JsonIgnore o DTOs
     */
    private String password;

    /**
     * Rol del usuario en el sistema
     *
     * Puede ser uno de: ADMIN, LIDER, COLABORADOR (definido en el enum Role)
     *
     * MongoDB almacena este enum como String, guardando el nombre de la constante.
     * Ejemplo en BD: "ADMIN", "LIDER", "COLABORADOR"
     *
     * @Indexed: Crea un índice para optimizar filtros y búsquedas por rol.
     *
     * Spring Security usa este campo para:
     * - Control de acceso basado en roles (@PreAuthorize, @Secured)
     * - Determinar qué recursos puede acceder el usuario
     * - Mostrar/ocultar opciones en la UI según permisos
     */
    @Indexed
    private Role role;

    /**
     * Imagen de perfil del usuario (URL o base64)
     *
     * Puede almacenar:
     * 1. URL de una imagen externa: "https://example.com/avatar.jpg"
     * 2. Imagen en base64: "data:image/jpeg;base64,/9j/4AAQSkZJRg..."
     * 3. Ruta a un archivo en el servidor: "/uploads/avatars/user123.jpg"
     *
     * Por defecto, se puede usar un avatar genérico o iniciales del usuario.
     * Si es null, la UI puede mostrar un avatar por defecto.
     */
    private String profileImage;

    /**
     * Indica si la cuenta del usuario está activa
     *
     * SOFT DELETE:
     * En lugar de eliminar físicamente los usuarios de la base de datos,
     * se marca esta bandera como false. Esto permite:
     * 1. Mantener la integridad referencial (proyectos, tareas asignadas)
     * 2. Auditoría: mantener historial de quién hizo qué
     * 3. Recuperación: posibilidad de reactivar cuentas
     *
     * @Indexed: Crea un índice para optimizar filtros por estado activo/inactivo.
     *
     * Usuarios con active=false:
     * - No pueden iniciar sesión
     * - No aparecen en listados de usuarios activos
     * - Mantienen sus datos históricos
     */
    @Indexed
    @Builder.Default  // Lombok: establece el valor por defecto cuando se usa el Builder
    private boolean active = true;

    /**
     * IDs de los proyectos donde el usuario es líder
     *
     * Relación uno-a-muchos: Un líder puede tener múltiples proyectos.
     *
     * ¿Por qué Set en lugar de List?
     * - Set no permite duplicados: un proyecto no puede estar dos veces
     * - No importa el orden de los proyectos
     * - HashSet ofrece operaciones O(1) para añadir/eliminar/buscar
     *
     * En MongoDB se almacena como un array de Strings:
     * ["507f1f77bcf86cd799439011", "507f1f77bcf86cd799439012"]
     *
     * IMPORTANTE: Esta es una relación bidireccional con Project.leaderId
     * Mantener la coherencia entre User.leadProjects y Project.leaderId
     */
    @Builder.Default
    private Set<String> leadProjectIds = new HashSet<>();

    /**
     * IDs de los proyectos donde el usuario es colaborador
     *
     * Relación muchos-a-muchos: Un colaborador puede estar en múltiples proyectos
     * y un proyecto puede tener múltiples colaboradores.
     *
     * En MongoDB se almacena como un array de Strings.
     * Los colaboradores ven estos proyectos en su dashboard pero no pueden
     * modificar configuraciones del proyecto, solo trabajar en sus tareas.
     */
    @Builder.Default
    private Set<String> collaboratorProjectIds = new HashSet<>();

    /**
     * Fecha y hora de creación del usuario
     *
     * @CreatedDate: Anotación de Spring Data que automáticamente establece
     * la fecha cuando se crea el documento por primera vez.
     *
     * IMPORTANTE: Para que funcione, se necesita:
     * 1. @EnableMongoAuditing en una clase de configuración
     * 2. Que la entidad esté siendo gestionada por un repositorio Spring Data
     *
     * Este campo es útil para:
     * - Auditoría: saber cuándo se registró cada usuario
     * - Reportes: usuarios registrados por período
     * - Ordenamiento: mostrar usuarios más recientes primero
     */
    @CreatedDate
    private LocalDateTime createdAt;

    /**
     * Fecha y hora de la última modificación
     *
     * @LastModifiedDate: Anotación de Spring Data que automáticamente
     * actualiza este campo cada vez que se modifica el documento.
     *
     * Se actualiza cuando cambia cualquier campo del usuario:
     * nombre, email, rol, contraseña, etc.
     *
     * Útil para:
     * - Auditoría: rastrear cambios en los usuarios
     * - Cache: invalidar cachés basándose en fecha de modificación
     * - Sincronización: en sistemas distribuidos
     */
    @LastModifiedDate
    private LocalDateTime updatedAt;

    // ==================== MÉTODOS DE UTILIDAD ====================

    /**
     * Verifica si el usuario tiene rol de ADMIN
     *
     * Método de conveniencia para simplificar validaciones en el código.
     * Es más legible usar user.isAdmin() que user.getRole() == Role.ADMIN
     *
     * @return boolean - true si el usuario es administrador
     */
    public boolean isAdmin() {
        return this.role != null && this.role.isAdmin();
    }

    /**
     * Verifica si el usuario tiene rol de LIDER
     *
     * @return boolean - true si el usuario es líder de proyecto
     */
    public boolean isLider() {
        return this.role != null && this.role.isLider();
    }

    /**
     * Verifica si el usuario tiene rol de COLABORADOR
     *
     * @return boolean - true si el usuario es colaborador
     */
    public boolean isColaborador() {
        return this.role != null && this.role.isColaborador();
    }

    /**
     * Añade un proyecto a la lista de proyectos liderados
     *
     * Este método mantiene la coherencia de la relación bidireccional.
     * Se debe llamar cuando un usuario se convierte en líder de un proyecto.
     *
     * @param projectId ID del proyecto a añadir
     */
    public void addLeadProject(String projectId) {
        if (projectId != null && !projectId.trim().isEmpty()) {
            this.leadProjectIds.add(projectId);
        }
    }

    /**
     * Elimina un proyecto de la lista de proyectos liderados
     *
     * @param projectId ID del proyecto a eliminar
     */
    public void removeLeadProject(String projectId) {
        this.leadProjectIds.remove(projectId);
    }

    /**
     * Añade un proyecto a la lista de proyectos donde colabora
     *
     * @param projectId ID del proyecto a añadir
     */
    public void addCollaboratorProject(String projectId) {
        if (projectId != null && !projectId.trim().isEmpty()) {
            this.collaboratorProjectIds.add(projectId);
        }
    }

    /**
     * Elimina un proyecto de la lista de proyectos donde colabora
     *
     * @param projectId ID del proyecto a eliminar
     */
    public void removeCollaboratorProject(String projectId) {
        this.collaboratorProjectIds.remove(projectId);
    }

    /**
     * Verifica si el usuario es líder de un proyecto específico
     *
     * @param projectId ID del proyecto a verificar
     * @return boolean - true si el usuario lidera ese proyecto
     */
    public boolean isLeaderOf(String projectId) {
        return this.leadProjectIds.contains(projectId);
    }

    /**
     * Verifica si el usuario es colaborador de un proyecto específico
     *
     * @param projectId ID del proyecto a verificar
     * @return boolean - true si el usuario colabora en ese proyecto
     */
    public boolean isCollaboratorOf(String projectId) {
        return this.collaboratorProjectIds.contains(projectId);
    }

    /**
     * Verifica si el usuario tiene alguna relación con el proyecto
     * (ya sea como líder o colaborador)
     *
     * @param projectId ID del proyecto a verificar
     * @return boolean - true si el usuario está relacionado con el proyecto
     */
    public boolean isRelatedToProject(String projectId) {
        return isLeaderOf(projectId) || isCollaboratorOf(projectId);
    }

    /**
     * Obtiene el total de proyectos del usuario (como líder + como colaborador)
     *
     * @return int - Número total de proyectos
     */
    public int getTotalProjects() {
        return leadProjectIds.size() + collaboratorProjectIds.size();
    }

    /**
     * Obtiene las iniciales del usuario para mostrar en avatares
     *
     * Extrae las primeras letras del nombre completo.
     * Ejemplo: "Juan Pérez García" → "JP"
     *
     * @return String - Iniciales del usuario (máximo 2 letras)
     */
    public String getInitials() {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "??";
        }

        String[] names = fullName.trim().split("\\s+");
        if (names.length == 1) {
            // Solo un nombre: tomar las dos primeras letras
            return names[0].substring(0, Math.min(2, names[0].length())).toUpperCase();
        } else {
            // Múltiples nombres: primera letra del primero y del segundo
            return (names[0].charAt(0) + "" + names[1].charAt(0)).toUpperCase();
        }
    }

    /**
     * Desactiva la cuenta del usuario (soft delete)
     *
     * En lugar de eliminar el usuario, se marca como inactivo.
     * Esto preserva la integridad de datos históricos.
     */
    public void deactivate() {
        this.active = false;
    }

    /**
     * Reactiva la cuenta del usuario
     *
     * Permite restaurar cuentas previamente desactivadas.
     */
    public void activate() {
        this.active = true;
    }
}
