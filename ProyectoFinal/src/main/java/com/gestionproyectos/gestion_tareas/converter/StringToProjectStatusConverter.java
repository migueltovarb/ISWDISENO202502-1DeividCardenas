package com.gestionproyectos.gestion_tareas.converter;

import com.gestionproyectos.gestion_tareas.enums.ProjectStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * StringToProjectStatusConverter - Convierte Strings a enum ProjectStatus
 *
 * PROPÓSITO:
 * Resolver el problema de conversión cuando los formularios HTML
 * envían el displayName ("En Progreso") en lugar del constant name ("EN_PROGRESO").
 *
 * PROBLEMA QUE RESUELVE:
 * - Por defecto, Spring solo convierte por nombre de constante exacto
 * - Los formularios envían "En Progreso" pero Spring espera "EN_PROGRESO"
 * - Esto causa MethodArgumentTypeMismatchException
 *
 * SOLUCIÓN:
 * Este converter intenta múltiples estrategias de conversión:
 * 1. Por nombre de constante (case-insensitive): "EN_PROGRESO" → ProjectStatus.EN_PROGRESO
 * 2. Por display name: "En Progreso" → ProjectStatus.EN_PROGRESO
 *
 * REGISTRO:
 * Se registra automáticamente en Spring por @Component.
 * Spring lo usa automáticamente para @RequestParam de tipo ProjectStatus.
 *
 * @author Sistema de Gestión de Tareas
 * @version 1.0
 * @since 2025-11-06
 */
@Component
public class StringToProjectStatusConverter implements Converter<String, ProjectStatus> {

    /**
     * Convierte un String a ProjectStatus
     *
     * Estrategia de conversión:
     * 1. Intenta por nombre de constante (case-insensitive)
     * 2. Si falla, intenta por display name
     * 3. Si ambos fallan, retorna null para que Spring lo trate como parámetro ausente
     *
     * @param source String a convertir (puede ser "EN_PROGRESO" o "En Progreso")
     * @return ProjectStatus correspondiente o null si no se puede convertir
     */
    @Override
    public ProjectStatus convert(@NonNull String source) {
        if (source == null || source.trim().isEmpty()) {
            return null;
        }

        String trimmed = source.trim();

        // Estrategia 1: Intentar por nombre de constante (case-insensitive)
        try {
            return ProjectStatus.valueOf(trimmed.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Si falla, intentamos la estrategia 2
        }

        // Estrategia 2: Buscar por display name
        for (ProjectStatus status : ProjectStatus.values()) {
            if (status.getDisplayName().equalsIgnoreCase(trimmed)) {
                return status;
            }
        }

        // Si ninguna estrategia funciona, retornar null
        // Spring lo tratará como parámetro ausente en lugar de error
        return null;
    }
}
