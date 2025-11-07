package com.gestionproyectos.gestion_tareas.controller;

import com.gestionproyectos.gestion_tareas.repository.ProjectRepository;
import com.gestionproyectos.gestion_tareas.repository.TaskRepository;
import com.gestionproyectos.gestion_tareas.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * HealthCheckController - Endpoint para verificar el estado de MongoDB
 *
 * PROPÓSITO:
 * Proporcionar un endpoint público para diagnosticar problemas de conexión
 * y verificar el estado de la base de datos.
 *
 * RUTA: GET /api/health
 *
 * @author Sistema de Gestión de Proyectos
 * @version 1.0
 * @since 2025-11-06
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

    /**
     * Verifica el estado de la base de datos MongoDB
     *
     * @return ResponseEntity con información del estado
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> checkHealth() {
        log.info("=".repeat(60));
        log.info("VERIFICACIÓN DE SALUD DE MONGODB");
        log.info("=".repeat(60));

        Map<String, Object> health = new HashMap<>();

        try {
            // Verificar conexión a MongoDB
            String dbName = mongoTemplate.getDb().getName();
            health.put("status", "UP");
            health.put("database", dbName);

            log.info("✓ Conexión a MongoDB exitosa");
            log.info("  Base de datos: {}", dbName);

            // Contar documentos en cada colección
            long userCount = userRepository.count();
            long projectCount = projectRepository.count();
            long taskCount = taskRepository.count();

            health.put("collections", Map.of(
                "users", userCount,
                "projects", projectCount,
                "tasks", taskCount
            ));

            log.info("Estado de las colecciones:");
            log.info("  - Usuarios: {}", userCount);
            log.info("  - Proyectos: {}", projectCount);
            log.info("  - Tareas: {}", taskCount);

            // Verificar si DataSeeder debería ejecutarse
            if (userCount == 0) {
                health.put("warning", "No hay usuarios en la base de datos. DataSeeder debería haber creado usuarios iniciales.");
                log.warn("⚠ No se encontraron usuarios. DataSeeder no se ha ejecutado.");
            } else {
                health.put("message", "Base de datos poblada correctamente");
                log.info("✓ Base de datos poblada correctamente");
            }

            // Información de colecciones disponibles
            var collectionNames = mongoTemplate.getDb().listCollectionNames();
            health.put("availableCollections", collectionNames.into(new java.util.ArrayList<>()));

            log.info("=".repeat(60));
            return ResponseEntity.ok(health);

        } catch (Exception e) {
            log.error("=".repeat(60));
            log.error("ERROR AL CONECTAR CON MONGODB");
            log.error("Error: {}", e.getMessage(), e);
            log.error("=".repeat(60));

            health.put("status", "DOWN");
            health.put("error", e.getMessage());
            health.put("message", "No se pudo conectar a MongoDB. Verifica la URI de conexión.");

            return ResponseEntity.status(503).body(health);
        }
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
