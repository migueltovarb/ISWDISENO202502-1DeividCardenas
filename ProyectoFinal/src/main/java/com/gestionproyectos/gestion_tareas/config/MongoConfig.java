package com.gestionproyectos.gestion_tareas.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * MongoConfig - Configuración de MongoDB
 *
 * Esta clase configura características específicas de MongoDB que necesitamos
 * en nuestra aplicación, principalmente la funcionalidad de auditoría.
 *
 * AUDITORÍA EN MONGODB:
 * La auditoría permite rastrear automáticamente cuándo se crean y modifican
 * los documentos en la base de datos. Esto es fundamental para:
 * - Trazabilidad: Saber cuándo se creó cada registro
 * - Historial: Rastrear cambios en documentos
 * - Debugging: Identificar cuándo ocurrieron modificaciones
 * - Compliance: Cumplir requisitos de auditoría en sistemas empresariales
 *
 * @EnableMongoAuditing:
 * Esta anotación activa la funcionalidad de auditoría de Spring Data MongoDB.
 * Una vez activada, las siguientes anotaciones funcionarán automáticamente:
 *
 * - @CreatedDate: Se establece automáticamente cuando se crea el documento
 * - @LastModifiedDate: Se actualiza automáticamente en cada modificación
 * - @CreatedBy: (Opcional) Almacena quién creó el documento
 * - @LastModifiedBy: (Opcional) Almacena quién modificó el documento
 *
 * REQUISITOS PARA QUE FUNCIONE:
 * 1. Esta configuración debe tener @EnableMongoAuditing
 * 2. Las entidades deben tener campos anotados con @CreatedDate o @LastModifiedDate
 * 3. Los campos deben ser de tipo LocalDateTime, Instant, o Date
 * 4. La entidad debe ser guardada a través de un Repository de Spring Data
 *
 * EJEMPLO DE USO:
 * En la entidad User.java:
 * ```
 * @CreatedDate
 * private LocalDateTime createdAt;  // Se establece automáticamente al crear
 *
 * @LastModifiedDate
 * private LocalDateTime updatedAt;  // Se actualiza automáticamente al modificar
 * ```
 *
 * IMPORTANTE:
 * - No necesitas establecer manualmente estos campos
 * - Spring Data los maneja automáticamente al llamar repository.save()
 * - Los campos se actualizan antes de persistir en la base de datos
 *
 * AUDITORÍA AVANZADA (no implementada en este proyecto, pero útil conocer):
 * Para rastrear QUIÉN hizo los cambios, necesitarías:
 * 1. Implementar AuditorAware<String> para proporcionar el usuario actual
 * 2. Añadir @EnableMongoAuditing(auditorAwareRef = "auditorProvider")
 * 3. Usar @CreatedBy y @LastModifiedBy en las entidades
 *
 * @author [Tu Nombre]
 * @version 1.0
 * @since 2025-11-03
 */
@Configuration  // Indica que esta clase contiene configuración de Spring
@EnableMongoAuditing  // Activa la auditoría automática de MongoDB
public class MongoConfig {

    /**
     * Esta clase no necesita beans adicionales por ahora.
     *
     * La simple presencia de @EnableMongoAuditing es suficiente para
     * activar la funcionalidad de auditoría en todo el proyecto.
     *
     * POSIBLES EXTENSIONES FUTURAS:
     *
     * 1. Configurar un AuditorAware para rastrear QUIÉN hace los cambios:
     *
     * @Bean
     * public AuditorAware<String> auditorProvider() {
     *     return () -> {
     *         // Obtener el usuario autenticado actual
     *         SecurityContext context = SecurityContextHolder.getContext();
     *         Authentication auth = context.getAuthentication();
     *         if (auth != null && auth.isAuthenticated()) {
     *             return Optional.of(auth.getName());
     *         }
     *         return Optional.of("system");
     *     };
     * }
     *
     * 2. Configurar un MongoTransactionManager para transacciones:
     *
     * @Bean
     * public MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
     *     return new MongoTransactionManager(dbFactory);
     * }
     *
     * 3. Configurar conversores personalizados:
     *
     * @Bean
     * public MongoCustomConversions customConversions() {
     *     return new MongoCustomConversions(Arrays.asList(
     *         new LocalDateToStringConverter(),
     *         new StringToLocalDateConverter()
     *     ));
     * }
     *
     * 4. Configurar validaciones:
     *
     * @Bean
     * public ValidatingMongoEventListener validatingMongoEventListener() {
     *     return new ValidatingMongoEventListener(validator());
     * }
     *
     * BUENAS PRÁCTICAS:
     * - Mantener esta configuración simple y clara
     * - Documentar cualquier bean adicional que se añada
     * - Separar configuraciones complejas en clases dedicadas
     * - No mezclar configuración de MongoDB con otras configuraciones
     */

    // Por ahora, esta clase solo activa la auditoría
    // Se pueden añadir más configuraciones según las necesidades del proyecto
}
