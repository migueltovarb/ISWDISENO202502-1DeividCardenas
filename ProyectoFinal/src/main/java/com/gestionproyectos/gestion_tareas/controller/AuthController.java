package com.gestionproyectos.gestion_tareas.controller;

import com.gestionproyectos.gestion_tareas.dto.UserRegistrationDto;
import com.gestionproyectos.gestion_tareas.enums.Role;
import com.gestionproyectos.gestion_tareas.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * AuthController - Controlador de Autenticación
 *
 * RESPONSABILIDAD:
 * Gestiona las páginas relacionadas con autenticación:
 * - Página de login
 * - Página de registro (opcional)
 * - Mensajes de error y éxito
 *
 * IMPORTANTE:
 * Este controlador solo muestra las VISTAS de autenticación.
 * El procesamiento real del login lo hace Spring Security automáticamente
 * mediante la configuración en SecurityConfig.
 *
 * FLUJO DE LOGIN:
 * 1. Usuario visita GET /login → Este controlador muestra login.html
 * 2. Usuario envía POST /login → Spring Security intercepta y procesa
 * 3. Si es exitoso → Spring Security redirige a /dashboard
 * 4. Si falla → Spring Security redirige a /login?error=true
 * 5. Este controlador detecta el parámetro error y muestra mensaje
 *
 * @author [Tu Nombre]
 * @version 1.0
 * @since 2025-11-03
 */
@Controller  // Marca esta clase como controlador MVC de Spring
@RequiredArgsConstructor  // Lombok: genera constructor con campos final
@Slf4j  // Lombok: logger automático
public class AuthController {

    /**
     * Servicio de gestión de usuarios
     * Inyectado automáticamente por Spring
     */
    private final UserService userService;

    /**
     * Muestra la página de login
     *
     * ANOTACIÓN @GetMapping:
     * - Mapea peticiones HTTP GET a /login
     * - Equivalente a @RequestMapping(value = "/login", method = GET)
     *
     * PARÁMETROS OPCIONALES:
     * - error: Indica si hubo un error de autenticación
     * - logout: Indica si el usuario acaba de cerrar sesión
     *
     * Estos parámetros vienen en la URL:
     * - /login?error=true → credenciales incorrectas
     * - /login?logout=true → sesión cerrada exitosamente
     *
     * MODELO (Model):
     * Es un objeto que transporta datos desde el controlador a la vista.
     * Funciona como un Map de clave-valor.
     *
     * Ejemplo:
     * model.addAttribute("mensaje", "Hola");
     * En Thymeleaf: <p th:text="${mensaje}"></p>  → muestra "Hola"
     *
     * @param error Parámetro opcional que indica error de login
     * @param logout Parámetro opcional que indica logout exitoso
     * @param model Objeto Model para pasar datos a la vista
     * @return String - Nombre de la vista Thymeleaf (login.html)
     */
    @GetMapping("/login")
    public String login(
            // @RequestParam(required = false): Parámetro opcional de la URL
            // Si no está presente en la URL, será null
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            Model model
    ) {
        // Verificar si hay un error de autenticación
        if (error != null) {
            // Añadir mensaje de error al modelo
            // Este mensaje se mostrará en la vista login.html
            model.addAttribute("errorMessage",
                "Email o contraseña incorrectos. Por favor, inténtalo de nuevo.");
        }

        // Verificar si el usuario acaba de cerrar sesión
        if (logout != null) {
            // Añadir mensaje de éxito al modelo
            model.addAttribute("logoutMessage",
                "Has cerrado sesión exitosamente.");
        }

        // Retornar el nombre de la vista
        // Spring busca: src/main/resources/templates/auth/login.html
        // (Thymeleaf añade automáticamente .html al final)
        return "auth/login";
    }

    /**
     * Redirige la página raíz (/) a /login
     *
     * PROPÓSITO:
     * Cuando un usuario visita la raíz del sitio (http://localhost:8080/),
     * lo redirigimos automáticamente a la página de login.
     *
     * ALTERNATIVAS:
     * En lugar de redirigir a /login, podrías:
     * - Mostrar una landing page (página de bienvenida)
     * - Redirigir a /dashboard si ya está autenticado
     * - Mostrar una página de marketing del producto
     *
     * REDIRECT:
     * El prefijo "redirect:" indica a Spring que debe hacer una redirección HTTP.
     * El navegador recibirá un código 302 (Found) y hará una nueva petición a /login.
     *
     * @return String - Redirección a /login
     */
    @GetMapping("/")
    public String home() {
        // Redirigir a la página de login
        return "redirect:/login";
    }

    /**
     * Muestra el formulario de registro público
     *
     * RUTA: GET /register
     *
     * PROPÓSITO:
     * Permite que nuevos usuarios se registren en el sistema como COLABORADORES.
     * Esta es una ruta pública (no requiere autenticación).
     *
     * @param model Modelo para pasar datos a la vista
     * @return String - Nombre de la vista de registro
     */
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        log.debug("Mostrando formulario de registro público");

        // Crear un DTO vacío para el formulario
        model.addAttribute("userDto", new UserRegistrationDto("", "", "", Role.COLABORADOR));

        return "auth/register";
    }

    /**
     * Procesa el formulario de registro público
     *
     * RUTA: POST /register
     *
     * PROCESO:
     * 1. Validar el DTO
     * 2. Si hay errores, volver al formulario
     * 3. Forzar el rol a COLABORADOR (seguridad)
     * 4. Llamar al servicio para crear el usuario
     * 5. Redirigir al login con mensaje de éxito
     *
     * SEGURIDAD:
     * Siempre se asigna rol COLABORADOR, independientemente de lo que venga
     * en el formulario. Solo el ADMIN puede crear usuarios con otros roles.
     *
     * @param userDto DTO con los datos del formulario
     * @param result Resultado de la validación
     * @param model Modelo para la vista
     * @param redirectAttributes Atributos para la redirección
     * @return String - Vista o redirección
     */
    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("userDto") UserRegistrationDto userDto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        log.info("Procesando registro público para: {}", userDto.email());

        // 1. VERIFICAR ERRORES DE VALIDACIÓN
        if (result.hasErrors()) {
            log.warn("Errores de validación en registro: {}", result.getAllErrors());
            return "auth/register";
        }

        // 2. FORZAR ROL A COLABORADOR (seguridad)
        // Incluso si alguien modifica el HTML y envía otro rol, se fuerza a COLABORADOR
        UserRegistrationDto safeDto = new UserRegistrationDto(
                userDto.fullName(),
                userDto.email(),
                userDto.password(),
                Role.COLABORADOR  // SIEMPRE COLABORADOR en registro público
        );

        // 3. INTENTAR REGISTRAR EL USUARIO
        try {
            userService.registerUser(safeDto);

            // 4. ÉXITO: Redirigir al login con mensaje
            redirectAttributes.addFlashAttribute("successMessage",
                    "¡Registro exitoso! Ya puedes iniciar sesión.");

            log.info("Usuario registrado exitosamente: {}", safeDto.email());

            return "redirect:/login";

        } catch (IllegalArgumentException e) {
            // 5. ERROR DE NEGOCIO (email duplicado)
            log.error("Error al registrar usuario: {}", e.getMessage());

            model.addAttribute("errorMessage", e.getMessage());
            return "auth/register";
        }
    }

    /*
    /**
     * Muestra la página de "Olvidé mi contraseña"
     *
     * @GetMapping("/forgot-password")
     * public String forgotPassword() {
     *     return "auth/forgot-password";
     * }
     */

    /*
    /**
     * Procesa la solicitud de reseteo de contraseña
     *
     * @PostMapping("/forgot-password")
     * public String processForgotPassword(
     *         @RequestParam String email,
     *         Model model
     * ) {
     *     // Generar token de reseteo
     *     // Enviar email con enlace de reseteo
     *     // Guardar token en BD con expiración
     *     return "redirect:/login?resetSent=true";
     * }
     */

    /**
     * NOTAS PARA TU PROFESOR:
     *
     * 1. SEPARACIÓN DE RESPONSABILIDADES:
     *    - AuthController: Solo muestra vistas
     *    - Spring Security: Procesa la autenticación real
     *    - CustomUserDetailsService: Carga datos del usuario
     *    - SecurityConfig: Define reglas de seguridad
     *
     * 2. REDIRECT vs FORWARD:
     *    - redirect: Nueva petición HTTP (URL cambia en navegador)
     *    - forward: Misma petición (URL no cambia)
     *    Usamos redirect después de POST para evitar reenvío de formulario
     *
     * 3. MODEL vs MODEL_AND_VIEW:
     *    - Model: Solo transporta datos a la vista
     *    - ModelAndView: Transporta datos Y especifica la vista
     *    Usamos Model porque es más simple y claro
     *
     * 4. PARÁMETROS OPCIONALES:
     *    @RequestParam(required = false) permite que el parámetro sea opcional
     *    Si el parámetro no está en la URL, su valor será null
     *
     * 5. MANEJO DE ERRORES:
     *    Spring Security añade automáticamente ?error=true en caso de fallo
     *    Nosotros solo detectamos ese parámetro y mostramos el mensaje
     *
     * 6. BUENAS PRÁCTICAS:
     *    ✓ Controlador simple y enfocado en una sola responsabilidad
     *    ✓ Uso de nombres descriptivos para atributos del modelo
     *    ✓ Mensajes de error claros y amigables para el usuario
     *    ✓ Código limpio y bien documentado
     */
}
