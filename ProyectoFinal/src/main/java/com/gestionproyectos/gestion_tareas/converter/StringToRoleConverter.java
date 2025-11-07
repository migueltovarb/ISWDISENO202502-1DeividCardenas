package com.gestionproyectos.gestion_tareas.converter;

import com.gestionproyectos.gestion_tareas.enums.Role;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * StringToRoleConverter - Convierte Strings a enum Role
 *
 * PROPÓSITO:
 * Resolver el problema de conversión cuando los formularios HTML
 * envían el displayName ("Colaborador") en lugar del constant name ("COLABORADOR").
 *
 * PROBLEMA QUE RESUELVE:
 * - Por defecto, Spring solo convierte por nombre de constante exacto
 * - Los formularios envían "Colaborador" pero Spring espera "COLABORADOR"
 * - Esto causa MethodArgumentTypeMismatchException
 *
 * SOLUCIÓN:
 * Este converter intenta múltiples estrategias de conversión:
 * 1. Por nombre de constante (case-insensitive): "COLABORADOR" → Role.COLABORADOR
 * 2. Por display name: "Colaborador" → Role.COLABORADOR
 *
 * REGISTRO:
 * Se registra automáticamente en Spring por @Component.
 * Spring lo usa automáticamente para @RequestParam de tipo Role.
 *
 * @author Sistema de Gestión de Tareas
 * @version 1.0
 * @since 2025-11-06
 */
@Component
public class StringToRoleConverter implements Converter<String, Role> {

    /**
     * Convierte un String a Role
     *
     * Estrategia de conversión:
     * 1. Intenta por nombre de constante (case-insensitive)
     * 2. Si falla, intenta por display name
     * 3. Si ambos fallan, retorna null para que Spring lo trate como parámetro ausente
     *
     * @param source String a convertir (puede ser "COLABORADOR" o "Colaborador")
     * @return Role correspondiente o null si no se puede convertir
     */
    @Override
    public Role convert(@NonNull String source) {
        if (source == null || source.trim().isEmpty()) {
            return null;
        }

        String trimmed = source.trim();

        // Estrategia 1: Intentar por nombre de constante (case-insensitive)
        try {
            return Role.valueOf(trimmed.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Si falla, intentamos la estrategia 2
        }

        // Estrategia 2: Buscar por display name
        for (Role role : Role.values()) {
            if (role.getDisplayName().equalsIgnoreCase(trimmed)) {
                return role;
            }
        }

        // Si ninguna estrategia funciona, retornar null
        // Spring lo tratará como parámetro ausente en lugar de error
        return null;
    }
}
