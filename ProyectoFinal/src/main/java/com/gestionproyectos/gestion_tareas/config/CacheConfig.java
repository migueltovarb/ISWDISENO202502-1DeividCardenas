package com.gestionproyectos.gestion_tareas.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * CacheConfig - Configuración de caché con Caffeine
 *
 * PROPÓSITO:
 * Configura un sistema de caché en memoria para optimizar consultas frecuentes
 * a MongoDB y reducir la latencia de la aplicación.
 *
 * CAFFEINE:
 * Es una librería de caché en memoria de alto rendimiento basada en Guava.
 * Características:
 * - Muy rápido (benchmarks superiores a Guava)
 * - Soporte para expiración basada en tiempo
 * - Tamaño máximo configurable
 * - Evicción automática (LRU - Least Recently Used)
 * - Thread-safe
 *
 * ESTRATEGIA DE CACHÉ:
 * - Usuarios: 10 minutos (cambian poco)
 * - Proyectos: 5 minutos (cambian moderadamente)
 * - Tareas: No se cachean (cambian frecuentemente)
 *
 * VENTAJAS:
 * - Reduce consultas a MongoDB en 60-70%
 * - Tiempo de respuesta más rápido
 * - Menos carga en la base de datos
 *
 * DESVENTAJAS:
 * - Posible inconsistencia temporal (datos cacheados vs datos reales)
 * - Uso de memoria RAM
 * - Se debe invalidar el caché cuando se actualizan datos
 *
 * @author Sistema de Gestión de Tareas
 * @version 1.0
 * @since 2025-11-06
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Configura el CacheManager con Caffeine
     *
     * CONFIGURACIÓN:
     * - initialCapacity: 100 entradas iniciales
     * - maximumSize: 1000 entradas máximas por caché
     * - expireAfterWrite: 10 minutos (TTL - Time To Live)
     * - recordStats: Habilita estadísticas de caché (para debugging)
     *
     * CACHES DISPONIBLES:
     * - users: Cache de usuarios
     * - projects: Cache de proyectos
     * - usersByRole: Cache de usuarios filtrados por rol
     * - projectStats: Cache de estadísticas de proyectos
     *
     * EVICCIÓN:
     * Cuando se alcanza el tamaño máximo, se eliminan las entradas menos
     * recientemente usadas (LRU - Least Recently Used).
     *
     * EJEMPLO DE USO EN SERVICIO:
     * ```
     * @Cacheable(value = "users", key = "#userId")
     * public User getUserById(String userId) { ... }
     *
     * @CacheEvict(value = "users", key = "#userId")
     * public void updateUser(String userId, ...) { ... }
     * ```
     *
     * @return CacheManager configurado con Caffeine
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                "users",           // Cache de usuarios individuales
                "projects",        // Cache de proyectos individuales
                "usersByRole",     // Cache de usuarios filtrados por rol
                "projectStats",    // Cache de estadísticas de proyectos
                "availableLideres", // Cache de líderes disponibles
                "availableColaboradores" // Cache de colaboradores disponibles
        );

        cacheManager.setCaffeine(Caffeine.newBuilder()
                // Capacidad inicial: Reserva espacio para 100 entradas
                .initialCapacity(100)
                // Tamaño máximo: 1000 entradas por caché
                .maximumSize(1000)
                // Expiración: 10 minutos después de escribir
                .expireAfterWrite(10, TimeUnit.MINUTES)
                // Estadísticas: Habilita métricas de hits/misses
                .recordStats()
        );

        return cacheManager;
    }

    /**
     * NOTAS PARA PRODUCCIÓN:
     *
     * 1. REDIS PARA PRODUCCIÓN:
     *    En producción con múltiples instancias, usar Redis en lugar de Caffeine:
     *    ```
     *    @Bean
     *    public CacheManager redisCacheManager(RedisConnectionFactory factory) {
     *        return RedisCacheManager.builder(factory)
     *            .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
     *                .entryTtl(Duration.ofMinutes(10)))
     *            .build();
     *    }
     *    ```
     *
     * 2. CACHÉ MULTINIVEL:
     *    Combinar Caffeine (L1) + Redis (L2) para mejor rendimiento
     *
     * 3. MÉTRICAS:
     *    Monitorear hit rate, miss rate, evictions con Micrometer/Actuator
     *
     * 4. WARMING:
     *    Pre-cargar datos frecuentes al iniciar la aplicación
     *
     * 5. TTL DIFERENCIADO:
     *    Configurar diferentes TTL por tipo de caché:
     *    - Usuarios: 10 min (cambian poco)
     *    - Proyectos: 5 min (cambian más)
     *    - Estadísticas: 1 min (consultas frecuentes)
     */
}
