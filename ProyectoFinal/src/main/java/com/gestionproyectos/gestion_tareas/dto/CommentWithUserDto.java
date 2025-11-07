package com.gestionproyectos.gestion_tareas.dto;

import java.time.LocalDateTime;

/**
 * CommentWithUserDto - DTO para mostrar comentarios con información del usuario
 *
 * @param userId ID del usuario
 * @param userName Nombre completo del usuario
 * @param text Contenido del comentario
 * @param createdAt Fecha y hora del comentario
 */
public record CommentWithUserDto(
    String userId,
    String userName,
    String text,
    LocalDateTime createdAt
) {}
