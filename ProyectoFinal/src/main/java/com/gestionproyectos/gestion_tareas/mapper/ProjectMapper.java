package com.gestionproyectos.gestion_tareas.mapper;

import com.gestionproyectos.gestion_tareas.dto.ProjectCreationDto;
import com.gestionproyectos.gestion_tareas.dto.ProjectDetailsDto;
import com.gestionproyectos.gestion_tareas.dto.TaskDetailsDto;
import com.gestionproyectos.gestion_tareas.enums.ProjectStatus;
import com.gestionproyectos.gestion_tareas.model.Project;
import com.gestionproyectos.gestion_tareas.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ProjectMapper - Conversor entre entidades Project y DTOs
 *
 * PROPÓSITO:
 * Centraliza la lógica de conversión entre Project y sus DTOs.
 *
 * @author Sistema de Gestión de Tareas
 * @version 1.0
 * @since 2025-11-06
 */
@Component
public class ProjectMapper {

    /**
     * Convierte ProjectCreationDto a Project (nueva entidad)
     *
     * @param dto DTO con datos de creación
     * @return Project nuevo (sin guardar en BD)
     */
    public Project toEntity(ProjectCreationDto dto) {
        if (dto == null) {
            return null;
        }

        return Project.builder()
            .name(dto.name())
            .description(dto.description())
            .leaderId(dto.leaderId())
            .status(ProjectStatus.PLANIFICACION)
            .archived(false)
            .progress(0)
            .build();
    }

    /**
     * Convierte Project a ProjectDetailsDto
     *
     * @param project Entidad Project
     * @param leaderName Nombre del líder del proyecto
     * @param tasks Lista de tareas del proyecto
     * @return ProjectDetailsDto con información completa
     */
    public ProjectDetailsDto toDetailsDto(
            Project project,
            String leaderName,
            List<TaskDetailsDto> tasks) {

        if (project == null) {
            return null;
        }

        return new ProjectDetailsDto(
            project.getId(),
            project.getName(),
            project.getDescription(),
            project.getStatus(),
            project.getProgress(),
            leaderName != null ? leaderName : "Sin líder",
            tasks != null ? tasks : List.of(),
            project.getStartDate(),
            project.getEndDate()
        );
    }

    /**
     * Convierte lista de Projects a lista de ProjectDetailsDtos
     *
     * @param projects Lista de entidades Project
     * @return Lista de ProjectDetailsDtos
     */
    public List<ProjectDetailsDto> toDetailsDtoList(List<Project> projects) {
        if (projects == null) {
            return List.of();
        }

        return projects.stream()
            .map(project -> toDetailsDto(project, null, null))
            .collect(Collectors.toList());
    }
}
