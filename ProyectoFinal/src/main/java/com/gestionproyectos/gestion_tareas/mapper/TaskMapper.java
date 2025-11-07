package com.gestionproyectos.gestion_tareas.mapper;

import com.gestionproyectos.gestion_tareas.dto.TaskCreationDto;
import com.gestionproyectos.gestion_tareas.dto.TaskDetailsDto;
import com.gestionproyectos.gestion_tareas.enums.TaskStatus;
import com.gestionproyectos.gestion_tareas.model.Task;
import com.gestionproyectos.gestion_tareas.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TaskMapper - Conversor entre entidades Task y DTOs
 *
 * PROPÓSITO:
 * Centraliza la lógica de conversión entre Task y sus DTOs.
 *
 * @author Sistema de Gestión de Tareas
 * @version 1.0
 * @since 2025-11-06
 */
@Component
public class TaskMapper {

    /**
     * Convierte TaskCreationDto a Task (nueva entidad)
     *
     * @param dto DTO con datos de creación
     * @param projectId ID del proyecto al que pertenece la tarea
     * @param createdById ID del usuario que crea la tarea
     * @return Task nueva (sin guardar en BD)
     */
    public Task toEntity(TaskCreationDto dto, String projectId, String createdById) {
        if (dto == null) {
            return null;
        }

        return Task.builder()
            .title(dto.title())
            .description(dto.description())
            .projectId(projectId)
            .assignedToId(dto.assignedToId())
            .createdById(createdById)
            .status(TaskStatus.PENDIENTE)
            .priority(dto.priority())
            .dueDate(dto.dueDate())
            .build();
    }

    /**
     * Convierte Task a TaskDetailsDto
     *
     * @param task Entidad Task
     * @param assignedUser Usuario asignado (puede ser null)
     * @param createdByUser Usuario que creó la tarea (puede ser null)
     * @return TaskDetailsDto con información completa
     */
    public TaskDetailsDto toDetailsDto(
            Task task,
            User assignedUser,
            User createdByUser) {

        if (task == null) {
            return null;
        }

        return TaskDetailsDto.fromTask(task, assignedUser, createdByUser);
    }

    /**
     * Convierte lista de Tasks a lista de TaskDetailsDtos
     *
     * @param tasks Lista de entidades Task
     * @return Lista de TaskDetailsDtos
     */
    public List<TaskDetailsDto> toDetailsDtoList(List<Task> tasks) {
        if (tasks == null) {
            return List.of();
        }

        return tasks.stream()
            .map(task -> TaskDetailsDto.fromTask(task, null, null))
            .collect(Collectors.toList());
    }
}
