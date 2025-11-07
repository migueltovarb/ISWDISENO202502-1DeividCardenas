package com.gestionproyectos.gestion_tareas.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RateLimitService - Servicio de limitación de tasa de peticiones
 *
 * PROPÓSITO:
 * Protege la aplicación contra ataques de fuerza bruta limitando el número
 * de intentos de login desde una misma IP en un período de tiempo.
 *
 * FUNCIONAMIENTO:
 * - Cada IP tiene un "bucket" con un número limitado de tokens
 * - Cada intento de login consume un token
 * - Los tokens se renuevan después de un tiempo determinado
 * - Si no hay tokens disponibles, se bloquea el intento
 *
 * CONFIGURACIÓN ACTUAL:
 * - Máximo: 5 intentos
 * - Ventana: 1 minuto
 * - Bloqueo: 1 minuto después de agotar intentos
 *
 * EJEMPLO DE ATAQUE BLOQUEADO:
 * - IP 192.168.1.100 intenta login 6 veces en 30 segundos
 * - Los primeros 5 intentos se permiten
 * - El 6º intento es bloqueado
 * - La IP queda bloqueada por 1 minuto
 * - Después de 1 minuto, se resetea el contador
 *
 * IMPLEMENTACIÓN:
 * Esta es una implementación en memoria simple. Para producción con
 * múltiples instancias, se debería usar Redis o similar.
 *
 * @author Sistema de Gestión de Tareas
 * @version 1.0
 * @since 2025-11-06
 */
@Service
@Slf4j
public class RateLimitService {

    /**
     * Configuración del rate limiter
     */
    private static final int MAX_ATTEMPTS = 5;           // Máximo de intentos permitidos
    private static final long TIME_WINDOW_SECONDS = 60;   // Ventana de tiempo (1 minuto)

    /**
     * Estructura para almacenar los intentos por IP
     * Key: IP del cliente
     * Value: Información de intentos (contador + timestamp)
     */
    private final Map<String, AttemptInfo> attempts = new ConcurrentHashMap<>();

    /**
     * Verifica si una IP puede hacer un intento de login
     *
     * ALGORITMO:
     * 1. Obtener información de intentos de la IP
     * 2. Si no existe, crear nueva entrada
     * 3. Verificar si ha pasado la ventana de tiempo
     * 4. Si ha pasado, resetear contador
     * 5. Verificar si aún tiene intentos disponibles
     * 6. Si tiene, consumir un intento y permitir
     * 7. Si no tiene, bloquear
     *
     * @param ip Dirección IP del cliente
     * @return true si puede hacer el intento, false si está bloqueado
     */
    public boolean allowRequest(String ip) {
        log.debug("Verificando rate limit para IP: {}", ip);

        // Obtener o crear información de intentos para esta IP
        AttemptInfo info = attempts.computeIfAbsent(ip, k -> new AttemptInfo());

        long now = Instant.now().getEpochSecond();

        synchronized (info) {
            // Si ha pasado la ventana de tiempo, resetear
            if (now - info.windowStart >= TIME_WINDOW_SECONDS) {
                log.debug("Ventana de tiempo expirada para IP {}, reseteando contador", ip);
                info.reset(now);
            }

            // Verificar si aún tiene intentos disponibles
            if (info.count < MAX_ATTEMPTS) {
                info.count++;
                log.debug("Intento {} de {} permitido para IP {}", info.count, MAX_ATTEMPTS, ip);
                return true;
            } else {
                long secondsUntilReset = TIME_WINDOW_SECONDS - (now - info.windowStart);
                log.warn("Rate limit excedido para IP {}. Bloqueado por {} segundos",
                        ip, secondsUntilReset);
                return false;
            }
        }
    }

    /**
     * Obtiene el número de intentos restantes para una IP
     *
     * @param ip Dirección IP del cliente
     * @return Número de intentos restantes
     */
    public int getRemainingAttempts(String ip) {
        AttemptInfo info = attempts.get(ip);
        if (info == null) {
            return MAX_ATTEMPTS;
        }

        long now = Instant.now().getEpochSecond();

        synchronized (info) {
            // Si ha pasado la ventana de tiempo, todos los intentos están disponibles
            if (now - info.windowStart >= TIME_WINDOW_SECONDS) {
                return MAX_ATTEMPTS;
            }

            return MAX_ATTEMPTS - info.count;
        }
    }

    /**
     * Obtiene el tiempo restante de bloqueo para una IP (en segundos)
     *
     * @param ip Dirección IP del cliente
     * @return Segundos hasta el reset, o 0 si no está bloqueada
     */
    public long getSecondsUntilReset(String ip) {
        AttemptInfo info = attempts.get(ip);
        if (info == null || info.count < MAX_ATTEMPTS) {
            return 0;
        }

        long now = Instant.now().getEpochSecond();
        long elapsed = now - info.windowStart;

        if (elapsed >= TIME_WINDOW_SECONDS) {
            return 0;
        }

        return TIME_WINDOW_SECONDS - elapsed;
    }

    /**
     * Resetea manualmente el contador de una IP
     * Útil para desbloquear un usuario legítimo bloqueado por error
     *
     * @param ip Dirección IP a resetear
     */
    public void resetIp(String ip) {
        log.info("Reseteando manualmente rate limit para IP: {}", ip);
        attempts.remove(ip);
    }

    /**
     * Limpia entradas antiguas del mapa para evitar memory leaks
     * Debería ejecutarse periódicamente (ej: cada hora)
     */
    public void cleanup() {
        long now = Instant.now().getEpochSecond();
        long beforeSize = attempts.size();

        attempts.entrySet().removeIf(entry -> {
            long age = now - entry.getValue().windowStart;
            return age > TIME_WINDOW_SECONDS * 2; // 2 minutos sin actividad
        });

        long removed = beforeSize - attempts.size();
        if (removed > 0) {
            log.debug("Rate limiter cleanup: {} entradas antiguas eliminadas", removed);
        }
    }

    /**
     * Clase interna para almacenar información de intentos
     */
    private static class AttemptInfo {
        private int count = 0;                    // Número de intentos en la ventana actual
        private long windowStart;                 // Timestamp del inicio de la ventana

        public AttemptInfo() {
            this.windowStart = Instant.now().getEpochSecond();
        }

        public void reset(long now) {
            this.count = 0;
            this.windowStart = now;
        }
    }

    /**
     * MEJORAS PARA PRODUCCIÓN:
     *
     * 1. USAR REDIS:
     *    Para aplicaciones con múltiples instancias, usar Redis:
     *    ```
     *    @Autowired
     *    private RedisTemplate<String, String> redisTemplate;
     *
     *    public boolean allowRequest(String ip) {
     *        String key = "rate_limit:" + ip;
     *        Long count = redisTemplate.opsForValue().increment(key);
     *        if (count == 1) {
     *            redisTemplate.expire(key, TIME_WINDOW_SECONDS, TimeUnit.SECONDS);
     *        }
     *        return count <= MAX_ATTEMPTS;
     *    }
     *    ```
     *
     * 2. SLIDING WINDOW:
     *    Implementar ventana deslizante en lugar de ventana fija
     *
     * 3. WHITELIST:
     *    Permitir lista blanca de IPs (admins, servidores internos)
     *
     * 4. BLACKLIST PERMANENTE:
     *    Bloquear permanentemente IPs con comportamiento sospechoso
     *
     * 5. MÉTRICAS:
     *    Exponer métricas con Micrometer:
     *    - Número de requests bloqueadas
     *    - IPs bloqueadas
     *    - Tasa de bloqueos
     *
     * 6. NOTIFICACIONES:
     *    Alertar al admin si una IP supera un umbral (ej: 50 intentos)
     *
     * 7. CAPTCHA:
     *    Después de X intentos, requerir CAPTCHA en lugar de bloquear
     *
     * 8. RATE LIMIT POR USUARIO:
     *    Además de por IP, limitar por username
     */
}
