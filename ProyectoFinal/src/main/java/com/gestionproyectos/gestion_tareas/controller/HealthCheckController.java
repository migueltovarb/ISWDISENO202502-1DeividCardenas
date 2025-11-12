package com.gestionproyectos.gestion_tareas.controller;

import com.gestionproyectos.gestion_tareas.repository.ProjectRepository;
import com.gestionproyectos.gestion_tareas.repository.TaskRepository;
import com.gestionproyectos.gestion_tareas.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * HealthCheckController - Endpoint para verificar el estado del sistema
 *
 * PROPÓSITO:
 * Proporcionar un endpoint público para diagnosticar problemas y verificar:
 * - Estado de MongoDB
 * - Estado del caché
 * - Métricas de memoria JVM
 * - Estadísticas de la aplicación
 *
 * RUTA: GET /api/health
 *
 * OPTIMIZACIÓN v2.0:
 * - Añadida verificación de caché
 * - Añadidas métricas de JVM
 * - Añadida medición de tiempo de respuesta de MongoDB
 * - Añadida versión de la aplicación
 *
 * @author Sistema de Gestión de Proyectos
 * @version 2.0
 * @since 2025-11-11
 */
@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
@Slf4j
public class HealthCheckController {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final MongoTemplate mongoTemplate;
    private final CacheManager cacheManager;

    /**
     * Verifica el estado completo del sistema
     *
     * @return ResponseEntity con información detallada del estado
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> checkHealth() {
        log.info("=".repeat(60));
        log.info("VERIFICACIÓN DE SALUD DEL SISTEMA");
        log.info("=".repeat(60));

        long startTime = System.currentTimeMillis();
        Map<String, Object> health = new HashMap<>();

        try {
            // 1. INFORMACIÓN GENERAL
            health.put("status", "UP");
            health.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            health.put("application", "Sistema de Gestión de Proyectos y Tareas");
            health.put("version", "2.0.0");

            // 2. VERIFICAR MONGODB
            long mongoStartTime = System.currentTimeMillis();
            String dbName = mongoTemplate.getDb().getName();
            long mongoResponseTime = System.currentTimeMillis() - mongoStartTime;

            Map<String, Object> mongoHealth = new HashMap<>();
            mongoHealth.put("status", "UP");
            mongoHealth.put("database", dbName);
            mongoHealth.put("responseTime", mongoResponseTime + "ms");

            // Contar documentos en cada colección
            long userCount = userRepository.count();
            long projectCount = projectRepository.count();
            long taskCount = taskRepository.count();

            mongoHealth.put("collections", Map.of(
                "users", userCount,
                "projects", projectCount,
                "tasks", taskCount,
                "total", userCount + projectCount + taskCount
            ));

            // Colecciones disponibles
            var collectionNames = mongoTemplate.getDb().listCollectionNames();
            mongoHealth.put("availableCollections", collectionNames.into(new java.util.ArrayList<>()));

            health.put("mongodb", mongoHealth);

            log.info("✓ MongoDB: UP ({}ms)", mongoResponseTime);
            log.info("  Base de datos: {}", dbName);
            log.info("  Usuarios: {}, Proyectos: {}, Tareas: {}", userCount, projectCount, taskCount);

            // 3. VERIFICAR CACHÉ
            Map<String, Object> cacheHealth = new HashMap<>();
            try {
                var cacheNames = cacheManager.getCacheNames();
                cacheHealth.put("status", "UP");
                cacheHealth.put("names", cacheNames);
                cacheHealth.put("count", cacheNames.size());

                // Información de cada caché
                Map<String, String> cacheInfo = new HashMap<>();
                for (String cacheName : cacheNames) {
                    var cache = cacheManager.getCache(cacheName);
                    cacheInfo.put(cacheName, cache != null ? "ACTIVE" : "INACTIVE");
                }
                cacheHealth.put("details", cacheInfo);

                log.info("✓ Caché: UP ({} cachés configurados)", cacheNames.size());
            } catch (Exception e) {
                cacheHealth.put("status", "ERROR");
                cacheHealth.put("error", e.getMessage());
                log.warn("⚠ Error al verificar caché: {}", e.getMessage());
            }
            health.put("cache", cacheHealth);

            // 4. MÉTRICAS DE JVM
            Runtime runtime = Runtime.getRuntime();
            long maxMemory = runtime.maxMemory();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;

            Map<String, Object> jvmHealth = new HashMap<>();
            jvmHealth.put("maxMemory", formatBytes(maxMemory));
            jvmHealth.put("totalMemory", formatBytes(totalMemory));
            jvmHealth.put("usedMemory", formatBytes(usedMemory));
            jvmHealth.put("freeMemory", formatBytes(freeMemory));
            jvmHealth.put("memoryUsagePercent", String.format("%.2f%%", (usedMemory * 100.0) / maxMemory));
            jvmHealth.put("availableProcessors", runtime.availableProcessors());

            health.put("jvm", jvmHealth);

            log.info("✓ JVM: Memoria usada {}/{} ({:.2f}%)",
                formatBytes(usedMemory),
                formatBytes(maxMemory),
                (usedMemory * 100.0) / maxMemory);

            // 5. VALIDACIONES Y WARNINGS
            if (userCount == 0) {
                health.put("warning", "No hay usuarios. DataSeeder debería haber creado usuarios iniciales.");
                log.warn("⚠ No se encontraron usuarios en la base de datos");
            }

            // Tiempo total de verificación
            long totalTime = System.currentTimeMillis() - startTime;
            health.put("healthCheckDuration", totalTime + "ms");

            log.info("✓ Verificación completada en {}ms", totalTime);
            log.info("=".repeat(60));

            return ResponseEntity.ok(health);

        } catch (Exception e) {
            log.error("=".repeat(60));
            log.error("ERROR EN VERIFICACIÓN DE SALUD");
            log.error("Error: {}", e.getMessage(), e);
            log.error("=".repeat(60));

            health.put("status", "DOWN");
            health.put("error", e.getMessage());
            health.put("errorType", e.getClass().getSimpleName());
            health.put("message", "Error al verificar el estado del sistema");

            return ResponseEntity.status(503).body(health);
        }
    }

    /**
     * Formatea bytes a formato legible (KB, MB, GB)
     *
     * @param bytes Cantidad de bytes
     * @return String formateado
     */
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        char unit = "KMGT".charAt(exp - 1);
        return String.format("%.2f %sB", bytes / Math.pow(1024, exp), unit);
    }

    /**
     * Endpoint para forzar la recarga de datos de prueba
     *
     * IMPORTANTE: Solo usar en desarrollo
     *
     * @return ResponseEntity con el resultado
     */
    @GetMapping("/reset-data")
    public ResponseEntity<Map<String, Object>> resetData() {
        log.warn("=".repeat(60));
        log.warn("SOLICITUD DE RESET DE DATOS");
        log.warn("=".repeat(60));

        Map<String, Object> result = new HashMap<>();

        try {
            long usersBefore = userRepository.count();
            long projectsBefore = projectRepository.count();
            long tasksBefore = taskRepository.count();

            result.put("before", Map.of(
                "users", usersBefore,
                "projects", projectsBefore,
                "tasks", tasksBefore
            ));

            // Eliminar todos los datos
            userRepository.deleteAll();
            projectRepository.deleteAll();
            taskRepository.deleteAll();

            log.info("✓ Datos eliminados correctamente");
            log.info("  - Usuarios eliminados: {}", usersBefore);
            log.info("  - Proyectos eliminados: {}", projectsBefore);
            log.info("  - Tareas eliminadas: {}", tasksBefore);

            result.put("status", "SUCCESS");
            result.put("message", "Todos los datos han sido eliminados. Reinicia la aplicación para que DataSeeder cargue nuevos datos.");

            log.warn("⚠ IMPORTANTE: Reinicia la aplicación para que DataSeeder cargue los datos iniciales");
            log.warn("=".repeat(60));

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("ERROR al resetear datos: {}", e.getMessage(), e);
            result.put("status", "ERROR");
            result.put("error", e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }
}
