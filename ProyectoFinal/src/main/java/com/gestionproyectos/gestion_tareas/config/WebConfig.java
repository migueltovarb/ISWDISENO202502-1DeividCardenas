package com.gestionproyectos.gestion_tareas.config;

import com.gestionproyectos.gestion_tareas.converter.StringToProjectStatusConverter;
import com.gestionproyectos.gestion_tareas.converter.StringToRoleConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebConfig - Configuración de Spring MVC
 *
 * PROPÓSITO:
 * Personalizar la configuración de Spring MVC para registrar
 * converters personalizados que manejan la conversión de enums.
 *
 * POR QUÉ ES NECESARIO:
 * - Los formularios HTML envían display names ("Colaborador")
 * - Spring por defecto solo convierte constant names ("COLABORADOR")
 * - Necesitamos converters personalizados para ambos casos
 *
 * PATRÓN: Configuration Pattern
 * - Centraliza la configuración de Spring MVC
 * - Registra converters personalizados
 * - Se ejecuta automáticamente al iniciar la aplicación
 *
 * @author Sistema de Gestión de Tareas
 * @version 1.0
 * @since 2025-11-06
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final StringToRoleConverter stringToRoleConverter;
    private final StringToProjectStatusConverter stringToProjectStatusConverter;

    /**
     * Registra converters personalizados
     *
     * Este método se ejecuta automáticamente durante la inicialización
     * de Spring MVC. Los converters registrados aquí se usarán para
     * convertir parámetros de request (@RequestParam) automáticamente.
     *
     * CONVERTERS REGISTRADOS:
     * - StringToRoleConverter: Convierte "Colaborador" → Role.COLABORADOR
     * - StringToProjectStatusConverter: Convierte "En Progreso" → ProjectStatus.EN_PROGRESO
     *
     * @param registry Registro de formatters y converters de Spring
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(stringToRoleConverter);
        registry.addConverter(stringToProjectStatusConverter);
    }
}
