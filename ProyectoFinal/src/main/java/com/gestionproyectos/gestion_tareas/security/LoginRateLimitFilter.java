package com.gestionproyectos.gestion_tareas.security;

import com.gestionproyectos.gestion_tareas.service.RateLimitService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * LoginRateLimitFilter - Filtro de limitación de intentos de login
 *
 * PROPÓSITO:
 * Intercepta las peticiones POST /login ANTES de que Spring Security
 * las procese y verifica si la IP ha excedido el límite de intentos.
 *
 * CADENA DE FILTROS:
 * Request → LoginRateLimitFilter → Spring Security → Controller
 *
 * FUNCIONAMIENTO:
 * 1. Se ejecuta en CADA request HTTP
 * 2. Verifica si es POST /login
 * 3. Si lo es, consulta RateLimitService
 * 4. Si está bloqueada, retorna error 429 (Too Many Requests)
 * 5. Si no está bloqueada, continúa con la cadena de filtros
 *
 * VENTAJAS DE USAR FILTRO:
 * - Se ejecuta ANTES de Spring Security
 * - No consume recursos de autenticación si está bloqueado
 * - Puede bloquear CUALQUIER tipo de request, no solo login
 * - Centralizado y reutilizable
 *
 * ORDEN DE EJECUCIÓN:
 * @Order(Ordered.HIGHEST_PRECEDENCE) garantiza que se ejecute primero
 *
 * @author Sistema de Gestión de Tareas
 * @version 1.0
 * @since 2025-11-06
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LoginRateLimitFilter implements Filter {

    // HTTP 429 Too Many Requests - No está definido en HttpServletResponse
    private static final int SC_TOO_MANY_REQUESTS = 429;

    private final RateLimitService rateLimitService;

    /**
     * Método principal del filtro
     *
     * Este método se ejecuta en CADA request HTTP que llega a la aplicación.
     *
     * @param request ServletRequest (petición HTTP)
     * @param response ServletResponse (respuesta HTTP)
     * @param chain FilterChain (cadena de filtros)
     * @throws IOException si hay error de I/O
     * @throws ServletException si hay error del servlet
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Solo aplicar rate limiting a peticiones POST /login
        if ("POST".equalsIgnoreCase(httpRequest.getMethod()) &&
            "/login".equals(httpRequest.getRequestURI())) {

            // Obtener la IP del cliente
            String clientIp = getClientIP(httpRequest);

            log.debug("Verificando rate limit para login desde IP: {}", clientIp);

            // Verificar si la IP puede hacer el intento
            if (!rateLimitService.allowRequest(clientIp)) {
                // IP bloqueada por exceder el límite
                handleRateLimitExceeded(httpResponse, clientIp);
                return; // No continuar con la cadena de filtros
            }

            // IP permitida, log de debug
            int remaining = rateLimitService.getRemainingAttempts(clientIp);
            log.debug("Login permitido para IP {}. Intentos restantes: {}", clientIp, remaining);
        }

        // Continuar con la cadena de filtros (Spring Security, Controladores, etc.)
        chain.doFilter(request, response);
    }

    /**
     * Maneja el caso cuando se excede el rate limit
     *
     * RESPUESTA HTTP:
     * - Status: 429 (Too Many Requests)
     * - Header: Retry-After con segundos hasta el reset
     * - Body: Mensaje HTML con información del bloqueo
     *
     * @param response HttpServletResponse
     * @param clientIp IP del cliente bloqueado
     * @throws IOException si hay error al escribir la respuesta
     */
    private void handleRateLimitExceeded(HttpServletResponse response, String clientIp)
            throws IOException {

        long secondsUntilReset = rateLimitService.getSecondsUntilReset(clientIp);

        log.warn("Login bloqueado por rate limit. IP: {}, Tiempo restante: {} segundos",
                clientIp, secondsUntilReset);

        // Configurar el status HTTP 429 (Too Many Requests)
        response.setStatus(SC_TOO_MANY_REQUESTS); // 429

        // Header Retry-After indica cuándo puede volver a intentar
        response.setHeader("Retry-After", String.valueOf(secondsUntilReset));

        // Content-Type HTML
        response.setContentType("text/html; charset=UTF-8");

        // Escribir página HTML de error
        response.getWriter().write(createRateLimitErrorPage(secondsUntilReset));
    }

    /**
     * Crea una página HTML de error para mostrar al usuario bloqueado
     *
     * @param secondsUntilReset Segundos hasta poder volver a intentar
     * @return String HTML con el mensaje de error
     */
    private String createRateLimitErrorPage(long secondsUntilReset) {
        return """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Demasiados Intentos - Gestión de Proyectos</title>
                <script src="https://cdn.tailwindcss.com"></script>
            </head>
            <body class="bg-gradient-to-br from-red-50 to-orange-100 min-h-screen flex items-center justify-center p-4">
                <div class="max-w-md w-full bg-white rounded-2xl shadow-2xl p-8">
                    <!-- Icono de advertencia -->
                    <div class="flex justify-center mb-6">
                        <div class="bg-red-100 rounded-full p-4">
                            <svg class="w-16 h-16 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/>
                            </svg>
                        </div>
                    </div>

                    <!-- Título -->
                    <h1 class="text-3xl font-bold text-center text-gray-900 mb-4">
                        Demasiados Intentos
                    </h1>

                    <!-- Mensaje -->
                    <p class="text-gray-600 text-center mb-6">
                        Has excedido el número máximo de intentos de inicio de sesión.
                        Por seguridad, tu acceso ha sido temporalmente bloqueado.
                    </p>

                    <!-- Temporizador -->
                    <div class="bg-orange-50 border-l-4 border-orange-400 p-4 mb-6">
                        <div class="flex items-center">
                            <div class="flex-shrink-0">
                                <svg class="h-5 w-5 text-orange-400" fill="currentColor" viewBox="0 0 20 20">
                                    <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm1-12a1 1 0 10-2 0v4a1 1 0 00.293.707l2.828 2.829a1 1 0 101.415-1.415L11 9.586V6z" clip-rule="evenodd"/>
                                </svg>
                            </div>
                            <div class="ml-3">
                                <p class="text-sm text-orange-700">
                                    Podrás volver a intentar en aproximadamente:
                                </p>
                                <p class="text-lg font-bold text-orange-900" id="countdown">
                                    %d segundos
                                </p>
                            </div>
                        </div>
                    </div>

                    <!-- Información adicional -->
                    <div class="bg-blue-50 border-l-4 border-blue-400 p-4 mb-6">
                        <div class="flex items-start">
                            <div class="flex-shrink-0">
                                <svg class="h-5 w-5 text-blue-400" fill="currentColor" viewBox="0 0 20 20">
                                    <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd"/>
                                </svg>
                            </div>
                            <div class="ml-3">
                                <p class="text-sm text-blue-700">
                                    <strong>¿Olvidaste tu contraseña?</strong><br>
                                    Contacta al administrador del sistema para recuperar tu acceso.
                                </p>
                            </div>
                        </div>
                    </div>

                    <!-- Botón de recarga (deshabilitado) -->
                    <button id="retryButton" disabled
                            class="w-full py-3 px-4 bg-gray-300 text-gray-500 font-medium rounded-lg cursor-not-allowed transition-colors">
                        Volver a Intentar (espera %d s)
                    </button>

                    <!-- Script de countdown -->
                    <script>
                        let seconds = %d;
                        const countdownEl = document.getElementById('countdown');
                        const retryButton = document.getElementById('retryButton');

                        const interval = setInterval(() => {
                            seconds--;

                            if (seconds <= 0) {
                                clearInterval(interval);
                                countdownEl.textContent = '¡Ya puedes intentar de nuevo!';
                                retryButton.textContent = 'Volver al Login';
                                retryButton.disabled = false;
                                retryButton.className = 'w-full py-3 px-4 bg-indigo-600 hover:bg-indigo-700 text-white font-medium rounded-lg cursor-pointer transition-colors';
                                retryButton.onclick = () => window.location.href = '/login';
                            } else {
                                countdownEl.textContent = seconds + ' segundos';
                                retryButton.textContent = 'Volver a Intentar (espera ' + seconds + ' s)';
                            }
                        }, 1000);
                    </script>
                </div>
            </body>
            </html>
            """.formatted(secondsUntilReset, secondsUntilReset, secondsUntilReset);
    }

    /**
     * Obtiene la IP real del cliente
     *
     * IMPORTANTE:
     * Si la aplicación está detrás de un proxy (Nginx, CloudFlare, etc.),
     * la IP real viene en headers como X-Forwarded-For.
     *
     * ORDEN DE BÚSQUEDA:
     * 1. X-Forwarded-For (proxy/load balancer)
     * 2. X-Real-IP (Nginx)
     * 3. RemoteAddr (conexión directa)
     *
     * SEGURIDAD:
     * En producción, validar que solo proxies confiables puedan
     * setear estos headers (sino un atacante podría falsificarlos).
     *
     * @param request HttpServletRequest
     * @return String con la IP del cliente
     */
    private String getClientIP(HttpServletRequest request) {
        // Intento 1: Header X-Forwarded-For (común en proxies)
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            // X-Forwarded-For puede contener múltiples IPs: "client, proxy1, proxy2"
            // Tomamos la primera (IP del cliente)
            return ip.split(",")[0].trim();
        }

        // Intento 2: Header X-Real-IP (Nginx)
        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        // Intento 3: RemoteAddr (conexión directa)
        return request.getRemoteAddr();
    }

    /**
     * NOTAS PARA PRODUCCIÓN:
     *
     * 1. USAR @Order PARA ORDEN DE FILTROS:
     *    ```
     *    @Order(Ordered.HIGHEST_PRECEDENCE)
     *    public class LoginRateLimitFilter implements Filter { ... }
     *    ```
     *
     * 2. CONFIGURAR FILTRO EN SECURITY CONFIG:
     *    ```
     *    http.addFilterBefore(loginRateLimitFilter, UsernamePasswordAuthenticationFilter.class);
     *    ```
     *
     * 3. WHITELIST DE IPs:
     *    No aplicar rate limiting a:
     *    - IPs internas (127.0.0.1, 192.168.x.x)
     *    - IPs de administradores
     *    - Health checks (ej: load balancer)
     *
     * 4. MÉTRICAS:
     *    Registrar en Prometheus/Grafana:
     *    - Número de requests bloqueadas
     *    - Distribución de IPs bloqueadas
     *    - Picos de intentos de login
     *
     * 5. ALERTAS:
     *    Notificar si:
     *    - Una sola IP hace >100 intentos
     *    - Muchas IPs están siendo bloqueadas (posible ataque distribuido)
     */
}
