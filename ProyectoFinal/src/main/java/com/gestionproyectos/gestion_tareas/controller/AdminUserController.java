package com.gestionproyectos.gestion_tareas.controller;

import com.gestionproyectos.gestion_tareas.dto.AdminPasswordChangeDto;
import com.gestionproyectos.gestion_tareas.dto.UserListDto;
import com.gestionproyectos.gestion_tareas.dto.UserRegistrationDto;
import com.gestionproyectos.gestion_tareas.dto.UserUpdateDto;
import com.gestionproyectos.gestion_tareas.enums.Role;
import com.gestionproyectos.gestion_tareas.model.User;
import com.gestionproyectos.gestion_tareas.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * AdminUserController - Gestión completa de usuarios
 *
 * RESPONSABILIDADES:
 * - Crear nuevos usuarios con cualquier rol
 * - Listar usuarios con búsqueda y filtros
 * - Editar información de usuarios
 * - Activar/Desactivar usuarios (soft delete)
 * - Eliminar usuarios permanentemente (hard delete)
 * - Cambiar contraseñas de usuarios (reset administrativo)
 *
 * REFACTORIZACIÓN:
 * Extraído de AdminController.java para cumplir con SRP.
 * Este controlador se enfoca exclusivamente en operaciones CRUD de usuarios.
 *
 * RUTAS:
 * - GET  /admin/crear-usuario              → Formulario de creación
 * - POST /admin/crear-usuario              → Procesar creación
 * - GET  /admin/usuarios                   → Listar con filtros
 * - GET  /admin/usuarios/{id}/editar       → Formulario de edición
 * - POST /admin/usuarios/{id}/editar       → Procesar edición
 * - POST /admin/usuarios/{id}/desactivar   → Soft delete
 * - POST /admin/usuarios/{id}/activar      → Reactivar
 * - POST /admin/usuarios/{id}/eliminar     → Hard delete
 * - GET  /admin/usuarios/{id}/cambiar-password → Formulario cambio password
 * - POST /admin/usuarios/{id}/cambiar-password → Procesar cambio password
 *
 * @author Sistema de Gestión de Tareas
 * @version 2.0
 * @since 2025-11-06
 */
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Slf4j
public class AdminUserController {

    private final UserService userService;

    // ==================== CREAR USUARIO ====================

    /**
     * Muestra formulario de creación de usuario
     *
     * DIFERENCIA CON REGISTRO PÚBLICO:
     * - El admin puede elegir cualquier rol (ADMIN, LIDER, COLABORADOR)
     * - El registro público solo crea COLABORADORES
     *
     * @param model Modelo con DTO vacío y lista de roles
     * @return Vista del formulario
     */
    @GetMapping("/crear-usuario")
    public String showCreateUserForm(Model model) {
        log.debug("Mostrando formulario de creación de usuario");
        model.addAttribute("userDto", new UserRegistrationDto("", "", "", null));
        model.addAttribute("roles", Role.values());
        return "admin/crear-usuario";
    }

    /**
     * Procesa la creación de un nuevo usuario
     *
     * VALIDACIONES:
     * 1. Validación de formato (Jakarta Validation en DTO)
     * 2. Email único (validado en UserService)
     * 3. Contraseña segura (validada con @ValidPassword)
     *
     * FLUJO:
     * 1. Validar DTO
     * 2. Si hay errores → volver al formulario
     * 3. Si no hay errores → UserService.registerUser()
     * 4. Redirigir con mensaje de éxito
     *
     * @param userDto DTO con datos del usuario
     * @param result Resultado de validación
     * @param model Modelo para la vista
     * @param redirectAttributes Para pasar mensajes flash
     * @return Redirección o vista del formulario con errores
     */
    @PostMapping("/crear-usuario")
    public String createUser(
            @Valid @ModelAttribute("userDto") UserRegistrationDto userDto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        log.info("Procesando creación de usuario: {}", userDto.email());

        if (result.hasErrors()) {
            log.warn("Errores de validación al crear usuario: {}", result.getAllErrors());
            model.addAttribute("roles", Role.values());
            return "admin/crear-usuario";
        }

        try {
            userService.registerUser(userDto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Usuario creado exitosamente: " + userDto.email());
            log.info("Usuario creado exitosamente: {} con rol {}",
                    userDto.email(), userDto.role().getDisplayName());
            return "redirect:/admin/crear-usuario";
        } catch (IllegalArgumentException e) {
            log.error("Error al crear usuario: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("roles", Role.values());
            return "admin/crear-usuario";
        }
    }

    // ==================== LISTAR USUARIOS ====================

    /**
     * Lista usuarios con búsqueda, filtros y paginación
     *
     * FILTROS DISPONIBLES:
     * - search: Busca en nombre y email
     * - role: Filtra por rol específico
     * - activeOnly: Muestra solo usuarios activos
     *
     * PAGINACIÓN:
     * - page: Número de página (0-indexed)
     * - size: Elementos por página (default: 10)
     * - sortBy: Campo de ordenamiento (default: fullName)
     * - sortDirection: ASC o DESC
     *
     * OPTIMIZACIÓN:
     * Usa paginación de Spring Data para no cargar todos los usuarios
     * en memoria. Eficiente incluso con miles de usuarios.
     *
     * @param search Texto de búsqueda
     * @param role Rol a filtrar
     * @param activeOnly Si true, solo muestra activos
     * @param page Número de página
     * @param size Tamaño de página
     * @param sortBy Campo de ordenamiento
     * @param sortDirection Dirección de ordenamiento
     * @param model Modelo para la vista
     * @return Vista del listado de usuarios
     */
    @GetMapping("/usuarios")
    public String listUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false, defaultValue = "false") Boolean activeOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fullName") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection,
            Model model) {

        log.debug("Listando usuarios - búsqueda: {}, rol: {}, activos: {}, página: {}, tamaño: {}",
                search, role, activeOnly, page, size);

        Page<UserListDto> userPage = userService.searchUsersPaginated(
                search, role, activeOnly, page, size, sortBy, sortDirection);

        model.addAttribute("userPage", userPage);
        model.addAttribute("users", userPage.getContent());
        model.addAttribute("search", search);
        model.addAttribute("selectedRole", role);
        model.addAttribute("activeOnly", activeOnly);
        model.addAttribute("roles", Role.values());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDirection", sortDirection);

        return "admin/usuarios";
    }

    // ==================== EDITAR USUARIO ====================

    /**
     * Muestra formulario de edición de usuario
     *
     * CAMPOS EDITABLES:
     * - Nombre completo
     * - Rol
     *
     * CAMPOS NO EDITABLES:
     * - Email (es el username, inmutable)
     * - Contraseña (se cambia en endpoint separado)
     *
     * @param userId ID del usuario a editar
     * @param model Modelo con datos del usuario
     * @return Vista del formulario de edición
     */
    @GetMapping("/usuarios/{userId}/editar")
    public String showEditUserForm(@PathVariable String userId, Model model) {
        log.debug("Mostrando formulario de edición para usuario: {}", userId);

        User user = userService.getUserById(userId);
        UserUpdateDto userDto = new UserUpdateDto(
                user.getId(),
                user.getFullName(),
                user.getRole()
        );

        model.addAttribute("userDto", userDto);
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());

        return "admin/editar-usuario";
    }

    /**
     * Procesa la edición de un usuario
     *
     * VALIDACIONES DE NEGOCIO:
     * - No se puede cambiar el rol de un LIDER si tiene proyectos activos
     * - No se puede desactivar el último ADMIN del sistema
     *
     * @param userId ID del usuario
     * @param userDto DTO con datos actualizados
     * @param result Resultado de validación
     * @param model Modelo para la vista
     * @param redirectAttributes Para mensajes flash
     * @return Redirección o vista con errores
     */
    @PostMapping("/usuarios/{userId}/editar")
    public String editUser(
            @PathVariable String userId,
            @Valid @ModelAttribute("userDto") UserUpdateDto userDto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        log.info("Procesando edición de usuario: {}", userId);

        if (result.hasErrors()) {
            User user = userService.getUserById(userId);
            model.addAttribute("user", user);
            model.addAttribute("roles", Role.values());
            return "admin/editar-usuario";
        }

        try {
            userService.updateUser(userDto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Usuario actualizado exitosamente");
            return "redirect:/admin/usuarios";
        } catch (IllegalArgumentException e) {
            User user = userService.getUserById(userId);
            model.addAttribute("user", user);
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("roles", Role.values());
            return "admin/editar-usuario";
        }
    }

    // ==================== ACTIVAR/DESACTIVAR USUARIO ====================

    /**
     * Desactiva un usuario (soft delete)
     *
     * COMPORTAMIENTO:
     * - No elimina el usuario de la BD
     * - Marca el campo 'active' como false
     * - El usuario no podrá hacer login
     * - Se mantiene el historial de proyectos y tareas
     *
     * VALIDACIONES:
     * - No se puede desactivar el último ADMIN
     *
     * @param userId ID del usuario a desactivar
     * @param redirectAttributes Para mensajes flash
     * @return Redirección al listado de usuarios
     */
    @PostMapping("/usuarios/{userId}/desactivar")
    public String deactivateUser(
            @PathVariable String userId,
            RedirectAttributes redirectAttributes) {

        log.info("Desactivando usuario: {}", userId);

        try {
            userService.deactivateUser(userId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Usuario desactivado exitosamente");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admin/usuarios";
    }

    /**
     * Reactiva un usuario previamente desactivado
     *
     * @param userId ID del usuario a reactivar
     * @param redirectAttributes Para mensajes flash
     * @return Redirección al listado de usuarios
     */
    @PostMapping("/usuarios/{userId}/activar")
    public String activateUser(
            @PathVariable String userId,
            RedirectAttributes redirectAttributes) {

        log.info("Activando usuario: {}", userId);

        try {
            userService.activateUser(userId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Usuario reactivado exitosamente");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admin/usuarios";
    }

    // ==================== ELIMINAR USUARIO ====================

    /**
     * Elimina permanentemente un usuario (hard delete)
     *
     * ADVERTENCIA:
     * Esta es una operación DESTRUCTIVA e IRREVERSIBLE.
     * El usuario y todos sus datos serán eliminados de la BD.
     *
     * RECOMENDACIÓN:
     * Usar deactivateUser() en lugar de deleteUser() en la mayoría de casos.
     *
     * VALIDACIONES:
     * - No se puede eliminar el último ADMIN
     * - No se puede eliminar un LIDER con proyectos activos
     *
     * @param userId ID del usuario a eliminar
     * @param redirectAttributes Para mensajes flash
     * @return Redirección al listado de usuarios
     */
    @PostMapping("/usuarios/{userId}/eliminar")
    public String deleteUser(
            @PathVariable String userId,
            RedirectAttributes redirectAttributes) {

        log.warn("Solicitud de eliminación permanente de usuario: {}", userId);

        try {
            User user = userService.getUserById(userId);
            String userEmail = user.getEmail();

            userService.deleteUser(userId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Usuario " + userEmail + " eliminado permanentemente del sistema");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            log.error("Error al eliminar usuario: {}", userId, e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error al eliminar el usuario: " + e.getMessage());
        }

        return "redirect:/admin/usuarios";
    }

    // ==================== CAMBIAR CONTRASEÑA ====================

    /**
     * Muestra formulario para cambiar la contraseña de un usuario
     *
     * USO:
     * - Reset de contraseña olvidada
     * - Cambio forzado por política de seguridad
     * - Primera asignación de contraseña
     *
     * NOTA:
     * No requiere la contraseña actual (es reset administrativo)
     *
     * @param userId ID del usuario
     * @param model Modelo para la vista
     * @param redirectAttributes Para mensajes flash si el usuario no existe
     * @return Vista del formulario o redirección si hay error
     */
    @GetMapping("/usuarios/{userId}/cambiar-password")
    public String showChangePasswordForm(
            @PathVariable String userId,
            Model model,
            RedirectAttributes redirectAttributes) {

        log.info("Mostrando formulario de cambio de contraseña para usuario: {}", userId);

        try {
            User user = userService.getUserById(userId);
            model.addAttribute("user", user);
            model.addAttribute("dto", new AdminPasswordChangeDto(userId, ""));
            return "admin/cambiar-password-usuario";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/usuarios";
        }
    }

    /**
     * Procesa el cambio de contraseña de un usuario
     *
     * SEGURIDAD:
     * - La contraseña se encripta con BCrypt
     * - Se valida que cumpla los requisitos mínimos
     * - Se registra en logs (sin mostrar la contraseña)
     *
     * @param userId ID del usuario
     * @param dto DTO con nueva contraseña
     * @param bindingResult Resultado de validación
     * @param model Modelo para la vista
     * @param redirectAttributes Para mensajes flash
     * @return Redirección o vista con errores
     */
    @PostMapping("/usuarios/{userId}/cambiar-password")
    public String changeUserPassword(
            @PathVariable String userId,
            @Valid @ModelAttribute("dto") AdminPasswordChangeDto dto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        log.info("Procesando cambio de contraseña para usuario: {}", userId);

        // Validar que el ID del path coincida con el ID del DTO
        if (!userId.equals(dto.userId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "ID de usuario inválido");
            return "redirect:/admin/usuarios";
        }

        if (bindingResult.hasErrors()) {
            try {
                User user = userService.getUserById(userId);
                model.addAttribute("user", user);
                return "admin/cambiar-password-usuario";
            } catch (IllegalArgumentException e) {
                redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
                return "redirect:/admin/usuarios";
            }
        }

        try {
            userService.adminChangePassword(dto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Contraseña cambiada exitosamente");
            return "redirect:/admin/usuarios/" + userId;
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            try {
                User user = userService.getUserById(userId);
                model.addAttribute("user", user);
                return "admin/cambiar-password-usuario";
            } catch (IllegalArgumentException ex) {
                return "redirect:/admin/usuarios";
            }
        }
    }

    /**
     * MEJORAS FUTURAS:
     *
     * 1. EXPORTACIÓN DE USUARIOS:
     *    Permitir exportar la lista de usuarios a CSV/Excel
     *
     * 2. IMPORTACIÓN MASIVA:
     *    Permitir importar usuarios desde CSV
     *
     * 3. AUDITORÍA:
     *    Registrar todos los cambios realizados por el admin
     *
     * 4. NOTIFICACIONES:
     *    Enviar email al usuario cuando se cambia su contraseña
     *
     * 5. HISTORIAL:
     *    Mostrar historial de cambios del usuario
     */
}
