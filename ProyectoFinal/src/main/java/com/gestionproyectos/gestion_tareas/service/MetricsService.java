package com.gestionproyectos.gestion_tareas.service;

import com.gestionproyectos.gestion_tareas.enums.TaskPriority;
import com.gestionproyectos.gestion_tareas.enums.TaskStatus;
import com.gestionproyectos.gestion_tareas.model.Project;
import com.gestionproyectos.gestion_tareas.model.Task;
import com.gestionproyectos.gestion_tareas.repository.ProjectRepository;
import com.gestionproyectos.gestion_tareas.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * MetricsService - Servicio para generar métricas y datos para gráficos
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MetricsService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    /**
     * Obtiene métricas generales del usuario (líder o colaborador)
     *
     * @param userId ID del usuario
     * @return Map con las métricas
     */
    public Map<String, Object> getUserMetrics(String userId) {
        Map<String, Object> metrics = new HashMap<>();

        // Obtener todas las tareas del usuario
        List<Task> tasks = taskRepository.findByAssignedToId(userId);

        // Contar tareas por estado
        Map<TaskStatus, Long> tasksByStatus = tasks.stream()
                .collect(Collectors.groupingBy(Task::getStatus, Collectors.counting()));

        // Contar tareas por prioridad
        Map<TaskPriority, Long> tasksByPriority = tasks.stream()
                .collect(Collectors.groupingBy(Task::getPriority, Collectors.counting()));

        // Calcular productividad (tareas completadas vs total)
        long completedTasks = tasksByStatus.getOrDefault(TaskStatus.COMPLETADA, 0L);
        double productivity = tasks.isEmpty() ? 0 : (completedTasks * 100.0) / tasks.size();

        // Tareas vencidas
        long overdueTasks = tasks.stream().filter(Task::isOverdue).count();

        metrics.put("totalTasks", tasks.size());
        metrics.put("completedTasks", completedTasks);
        metrics.put("pendingTasks", tasksByStatus.getOrDefault(TaskStatus.PENDIENTE, 0L));
        metrics.put("inProgressTasks", tasksByStatus.getOrDefault(TaskStatus.EN_PROGRESO, 0L));
        metrics.put("overdueTasks", overdueTasks);
        metrics.put("productivity", Math.round(productivity * 100.0) / 100.0);
        metrics.put("tasksByStatus", tasksByStatus);
        metrics.put("tasksByPriority", tasksByPriority);

        return metrics;
    }

    /**
     * Obtiene métricas de proyectos del líder
     *
     * @param leaderId ID del líder
     * @return Map con las métricas de proyectos
     */
    public Map<String, Object> getLeaderProjectMetrics(String leaderId) {
        Map<String, Object> metrics = new HashMap<>();

        // Obtener proyectos del líder
        List<Project> projects = projectRepository.findByLeaderId(leaderId);

        // Calcular métricas
        int totalProjects = projects.size();
        int activeProjects = (int) projects.stream()
                .filter(p -> !p.isCancelled() && !p.isCompleted())
                .count();
        int completedProjects = (int) projects.stream()
                .filter(Project::isCompleted)
                .count();

        // Progreso promedio de todos los proyectos
        double averageProgress = projects.stream()
                .mapToInt(Project::getProgress)
                .average()
                .orElse(0.0);

        // Total de tareas en todos los proyectos
        List<String> projectIds = projects.stream()
                .map(Project::getId)
                .toList();

        long totalTasks = projectIds.stream()
                .mapToLong(taskRepository::countByProjectId)
                .sum();

        metrics.put("totalProjects", totalProjects);
        metrics.put("activeProjects", activeProjects);
        metrics.put("completedProjects", completedProjects);
        metrics.put("averageProgress", Math.round(averageProgress * 100.0) / 100.0);
        metrics.put("totalTasks", totalTasks);

        return metrics;
    }

    /**
     * Obtiene datos para gráfico de tareas por estado
     *
     * @param projectId ID del proyecto (null para todas las tareas del usuario)
     * @param userId ID del usuario (null para todas las tareas del proyecto)
     * @return Map con labels y datos
     */
    public Map<String, Object> getTasksByStatusChartData(String projectId, String userId) {
        List<Task> tasks;

        if (projectId != null) {
            tasks = taskRepository.findByProjectId(projectId);
        } else if (userId != null) {
            tasks = taskRepository.findByAssignedToId(userId);
        } else {
            return Collections.emptyMap();
        }

        Map<TaskStatus, Long> tasksByStatus = tasks.stream()
                .collect(Collectors.groupingBy(Task::getStatus, Collectors.counting()));

        List<String> labels = Arrays.stream(TaskStatus.values())
                .map(TaskStatus::getDisplayName)
                .toList();

        List<Long> data = Arrays.stream(TaskStatus.values())
                .map(status -> tasksByStatus.getOrDefault(status, 0L))
                .toList();

        Map<String, Object> chartData = new HashMap<>();
        chartData.put("labels", labels);
        chartData.put("data", data);
        chartData.put("backgroundColor", Arrays.asList(
                "#9CA3AF", // Pendiente - gris
                "#3B82F6", // En Progreso - azul
                "#EF4444", // Bloqueada - rojo
                "#F59E0B", // En Revisión - amarillo
                "#10B981"  // Completada - verde
        ));

        return chartData;
    }

    /**
     * Obtiene datos para gráfico de tareas por prioridad
     *
     * @param projectId ID del proyecto
     * @return Map con labels y datos
     */
    public Map<String, Object> getTasksByPriorityChartData(String projectId) {
        List<Task> tasks = taskRepository.findByProjectId(projectId);

        Map<TaskPriority, Long> tasksByPriority = tasks.stream()
                .collect(Collectors.groupingBy(Task::getPriority, Collectors.counting()));

        List<String> labels = Arrays.stream(TaskPriority.values())
                .map(TaskPriority::getDisplayName)
                .toList();

        List<Long> data = Arrays.stream(TaskPriority.values())
                .map(priority -> tasksByPriority.getOrDefault(priority, 0L))
                .toList();

        Map<String, Object> chartData = new HashMap<>();
        chartData.put("labels", labels);
        chartData.put("data", data);
        chartData.put("backgroundColor", Arrays.asList(
                "#9CA3AF", // Baja - gris
                "#3B82F6", // Media - azul
                "#F59E0B", // Alta - naranja
                "#EF4444"  // Crítica - rojo
        ));

        return chartData;
    }

    /**
     * Obtiene datos para gráfico de progreso de proyectos
     *
     * @param leaderId ID del líder
     * @return Map con labels y datos
     */
    public Map<String, Object> getProjectProgressChartData(String leaderId) {
        List<Project> projects = projectRepository.findByLeaderId(leaderId);

        List<String> labels = projects.stream()
                .map(Project::getName)
                .limit(10) // Limitar a 10 proyectos para que el gráfico sea legible
                .toList();

        List<Integer> data = projects.stream()
                .map(Project::getProgress)
                .limit(10)
                .toList();

        Map<String, Object> chartData = new HashMap<>();
        chartData.put("labels", labels);
        chartData.put("data", data);

        return chartData;
    }

    /**
     * Obtiene estadísticas de velocidad de completado de tareas
     *
     * @param projectId ID del proyecto
     * @return Map con estadísticas
     */
    public Map<String, Object> getTaskVelocityStats(String projectId) {
        List<Task> completedTasks = taskRepository.findByProjectIdAndStatus(projectId, TaskStatus.COMPLETADA);

        if (completedTasks.isEmpty()) {
            return Map.of(
                    "averageDaysToComplete", 0.0,
                    "tasksCompletedLast7Days", 0,
                    "tasksCompletedLast30Days", 0
            );
        }

        // Calcular promedio de días para completar
        double avgDays = completedTasks.stream()
                .filter(t -> t.getCreatedAt() != null && t.getCompletedAt() != null)
                .mapToLong(t -> ChronoUnit.DAYS.between(
                        t.getCreatedAt().toLocalDate(),
                        t.getCompletedAt().toLocalDate()
                ))
                .average()
                .orElse(0.0);

        // Tareas completadas en los últimos 7 días
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);
        long tasksLast7Days = completedTasks.stream()
                .filter(t -> t.getCompletedAt() != null)
                .filter(t -> t.getCompletedAt().toLocalDate().isAfter(sevenDaysAgo))
                .count();

        // Tareas completadas en los últimos 30 días
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        long tasksLast30Days = completedTasks.stream()
                .filter(t -> t.getCompletedAt() != null)
                .filter(t -> t.getCompletedAt().toLocalDate().isAfter(thirtyDaysAgo))
                .count();

        return Map.of(
                "averageDaysToComplete", Math.round(avgDays * 100.0) / 100.0,
                "tasksCompletedLast7Days", tasksLast7Days,
                "tasksCompletedLast30Days", tasksLast30Days
        );
    }
}
