package com.gestionproyectos.gestion_tareas.service;

import com.gestionproyectos.gestion_tareas.dto.TaskCreationDto;
import com.gestionproyectos.gestion_tareas.enums.ProjectStatus;
import com.gestionproyectos.gestion_tareas.enums.Role;
import com.gestionproyectos.gestion_tareas.enums.TaskPriority;
import com.gestionproyectos.gestion_tareas.enums.TaskStatus;
import com.gestionproyectos.gestion_tareas.model.Project;
import com.gestionproyectos.gestion_tareas.model.Task;
import com.gestionproyectos.gestion_tareas.model.User;
import com.gestionproyectos.gestion_tareas.repository.ProjectRepository;
import com.gestionproyectos.gestion_tareas.repository.TaskRepository;
import com.gestionproyectos.gestion_tareas.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests Unitarios para TaskService
 *
 * COBERTURA:
 * - Creación de tareas (CREATE)
 * - Validación de proyecto no cancelado
 * - Validación de responsable activo
 * - Actualización de estado con transiciones válidas (UPDATE)
 * - Validación de permisos para cambiar estado
 * - Comentarios en tareas
 * - Actualización automática de progreso del proyecto
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TaskService - Tests Unitarios")
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    private TaskCreationDto validTaskDto;
    private Project validProject;
    private User validColaborador;
    private User validLider;
    private Task validTask;

    @BeforeEach
    void setUp() {
        // Colaborador válido
        validColaborador = User.builder()
            .id("colab1")
            .fullName("Colaborador Test")
            .email("colab@example.com")
            .role(Role.COLABORADOR)
            .active(true)
            .collaboratorProjectIds(new HashSet<>())
            .build();

        // Líder válido
        validLider = User.builder()
            .id("lider1")
            .fullName("Líder Test")
            .email("lider@example.com")
            .role(Role.LIDER)
            .active(true)
            .build();

        // Proyecto válido
        validProject = Project.builder()
            .id("project1")
            .name("Proyecto Test")
            .leaderId("lider1")
            .collaboratorIds(new HashSet<>())
            .status(ProjectStatus.EN_PROGRESO)
            .build();

        // DTO de creación de tarea válido
        validTaskDto = new TaskCreationDto(
            "Tarea Test",
            "Descripción de la tarea test",
            "colab1",
            TaskPriority.MEDIA,
            LocalDate.now().plusDays(7)
        );

        // Tarea válida
        validTask = Task.builder()
            .id("task1")
            .title("Tarea Test")
            .description("Descripción")
            .projectId("project1")
            .assignedToId("colab1")
            .createdById("lider1")
            .status(TaskStatus.PENDIENTE)
            .priority(TaskPriority.MEDIA)
            .build();
    }

    // ==================== TESTS DE CREACIÓN (CREATE) ====================

    @Test
    @DisplayName("createTask - Debe crear tarea con datos válidos")
    void createTask_WithValidData_ShouldCreateTask() {
        // Arrange
        when(projectRepository.findById("project1")).thenReturn(Optional.of(validProject));
        when(userRepository.findById("colab1")).thenReturn(Optional.of(validColaborador));
        when(taskRepository.save(any(Task.class))).thenReturn(validTask);
        when(taskRepository.countByProjectId(anyString())).thenReturn(0L);
        when(projectRepository.save(any(Project.class))).thenReturn(validProject);

        // Act
        taskService.createTask(validTaskDto, "project1", "lider1");

        // Assert
        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(taskCaptor.capture());

        Task savedTask = taskCaptor.getValue();
        assertThat(savedTask.getTitle()).isEqualTo("Tarea Test");
        assertThat(savedTask.getProjectId()).isEqualTo("project1");
        assertThat(savedTask.getAssignedToId()).isEqualTo("colab1");
        assertThat(savedTask.getCreatedById()).isEqualTo("lider1");
        assertThat(savedTask.getStatus()).isEqualTo(TaskStatus.PENDIENTE);
    }

    @Test
    @DisplayName("createTask - Debe rechazar proyecto inexistente")
    void createTask_WithNonExistentProject_ShouldThrowException() {
        // Arrange
        when(projectRepository.findById("invalid")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
            taskService.createTask(validTaskDto, "invalid", "lider1")
        )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Proyecto no encontrado");

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("createTask - Debe rechazar proyecto cancelado")
    void createTask_WithCancelledProject_ShouldThrowException() {
        // Arrange
        validProject.setStatus(ProjectStatus.CANCELADO);

        when(projectRepository.findById("project1")).thenReturn(Optional.of(validProject));

        // Act & Assert
        assertThatThrownBy(() ->
            taskService.createTask(validTaskDto, "project1", "lider1")
        )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("proyecto cancelado");

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("createTask - Debe rechazar responsable inexistente")
    void createTask_WithNonExistentAssignee_ShouldThrowException() {
        // Arrange
        when(projectRepository.findById("project1")).thenReturn(Optional.of(validProject));
        when(userRepository.findById("colab1")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
            taskService.createTask(validTaskDto, "project1", "lider1")
        )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("responsable no encontrado");

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("createTask - Debe rechazar responsable inactivo")
    void createTask_WithInactiveAssignee_ShouldThrowException() {
        // Arrange
        validColaborador.deactivate(); // Desactivar

        when(projectRepository.findById("project1")).thenReturn(Optional.of(validProject));
        when(userRepository.findById("colab1")).thenReturn(Optional.of(validColaborador));

        // Act & Assert
        assertThatThrownBy(() ->
            taskService.createTask(validTaskDto, "project1", "lider1")
        )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("no está activo");

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("createTask - Debe añadir colaborador al proyecto automáticamente")
    void createTask_ShouldAddCollaboratorToProjectAutomatically() {
        // Arrange
        when(projectRepository.findById("project1")).thenReturn(Optional.of(validProject));
        when(userRepository.findById("colab1")).thenReturn(Optional.of(validColaborador));
        when(taskRepository.save(any(Task.class))).thenReturn(validTask);
        when(taskRepository.countByProjectId(anyString())).thenReturn(0L);
        when(projectRepository.save(any(Project.class))).thenReturn(validProject);

        // Act
        taskService.createTask(validTaskDto, "project1", "lider1");

        // Assert
        // Verificar que se guardó el proyecto con el colaborador añadido
        ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(projectCaptor.capture());

        Project savedProject = projectCaptor.getValue();
        assertThat(savedProject.getCollaboratorIds()).contains("colab1");
    }

    @Test
    @DisplayName("createTask - No debe duplicar colaborador si ya existe")
    void createTask_ShouldNotDuplicateExistingCollaborator() {
        // Arrange
        validProject.getCollaboratorIds().add("colab1"); // Ya es colaborador

        when(projectRepository.findById("project1")).thenReturn(Optional.of(validProject));
        when(userRepository.findById("colab1")).thenReturn(Optional.of(validColaborador));
        when(taskRepository.save(any(Task.class))).thenReturn(validTask);
        when(taskRepository.countByProjectId(anyString())).thenReturn(0L);
        when(projectRepository.save(any(Project.class))).thenReturn(validProject);

        // Act
        taskService.createTask(validTaskDto, "project1", "lider1");

        // Assert
        // El colaborador debe aparecer solo una vez
        ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository, atLeastOnce()).save(projectCaptor.capture());

        long count = projectCaptor.getValue().getCollaboratorIds().stream()
            .filter(id -> id.equals("colab1"))
            .count();

        assertThat(count).isEqualTo(1);
    }

    // ==================== TESTS DE ACTUALIZACIÓN DE ESTADO ====================

    @Test
    @DisplayName("updateTaskStatus - Debe actualizar estado con transición válida")
    void updateTaskStatus_WithValidTransition_ShouldUpdateStatus() {
        // Arrange
        validTask.setStatus(TaskStatus.PENDIENTE);

        when(taskRepository.findById("task1")).thenReturn(Optional.of(validTask));
        when(taskRepository.save(any(Task.class))).thenReturn(validTask);
        when(taskRepository.countByProjectId(anyString())).thenReturn(1L);
        when(taskRepository.countByProjectIdAndStatus(anyString(), any())).thenReturn(0L);
        when(projectRepository.findById("project1")).thenReturn(Optional.of(validProject));
        when(projectRepository.save(any(Project.class))).thenReturn(validProject);

        // Act - Transición válida: PENDIENTE → EN_PROGRESO
        taskService.updateTaskStatus("task1", TaskStatus.EN_PROGRESO, validColaborador);

        // Assert
        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(taskCaptor.capture());

        assertThat(taskCaptor.getValue().getStatus()).isEqualTo(TaskStatus.EN_PROGRESO);
    }

    @Test
    @DisplayName("updateTaskStatus - Debe rechazar transición inválida")
    void updateTaskStatus_WithInvalidTransition_ShouldThrowException() {
        // Arrange
        validTask.setStatus(TaskStatus.PENDIENTE);

        when(taskRepository.findById("task1")).thenReturn(Optional.of(validTask));

        // Act & Assert - Transición inválida: PENDIENTE → COMPLETADA
        assertThatThrownBy(() ->
            taskService.updateTaskStatus("task1", TaskStatus.COMPLETADA, validColaborador)
        )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Transición de estado inválida");

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("updateTaskStatus - Debe rechazar usuario sin permisos")
    void updateTaskStatus_WithUnauthorizedUser_ShouldThrowException() {
        // Arrange
        User otroUsuario = User.builder()
            .id("otro1")
            .role(Role.COLABORADOR)
            .build();

        when(taskRepository.findById("task1")).thenReturn(Optional.of(validTask));

        // Act & Assert
        assertThatThrownBy(() ->
            taskService.updateTaskStatus("task1", TaskStatus.EN_PROGRESO, otroUsuario)
        )
            .isInstanceOf(SecurityException.class)
            .hasMessageContaining("No tienes permisos");

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("updateTaskStatus - Debe permitir al asignado cambiar estado")
    void updateTaskStatus_WithAssignedUser_ShouldAllow() {
        // Arrange
        validTask.setStatus(TaskStatus.PENDIENTE);

        when(taskRepository.findById("task1")).thenReturn(Optional.of(validTask));
        when(taskRepository.save(any(Task.class))).thenReturn(validTask);
        when(taskRepository.countByProjectId(anyString())).thenReturn(1L);
        when(taskRepository.countByProjectIdAndStatus(anyString(), any())).thenReturn(0L);
        when(projectRepository.findById("project1")).thenReturn(Optional.of(validProject));
        when(projectRepository.save(any(Project.class))).thenReturn(validProject);

        // Act - El colaborador asignado puede cambiar el estado
        taskService.updateTaskStatus("task1", TaskStatus.EN_PROGRESO, validColaborador);

        // Assert - No debe lanzar excepción
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("updateTaskStatus - Debe permitir a LIDER cambiar cualquier tarea")
    void updateTaskStatus_WithLeader_ShouldAllow() {
        // Arrange
        validTask.setStatus(TaskStatus.PENDIENTE);

        when(taskRepository.findById("task1")).thenReturn(Optional.of(validTask));
        when(taskRepository.save(any(Task.class))).thenReturn(validTask);
        when(taskRepository.countByProjectId(anyString())).thenReturn(1L);
        when(taskRepository.countByProjectIdAndStatus(anyString(), any())).thenReturn(0L);
        when(projectRepository.findById("project1")).thenReturn(Optional.of(validProject));
        when(projectRepository.save(any(Project.class))).thenReturn(validProject);

        // Act - El líder puede cambiar el estado aunque no sea el asignado
        taskService.updateTaskStatus("task1", TaskStatus.EN_PROGRESO, validLider);

        // Assert - No debe lanzar excepción
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("updateTaskStatus - Debe permitir a ADMIN cambiar cualquier tarea")
    void updateTaskStatus_WithAdmin_ShouldAllow() {
        // Arrange
        User admin = User.builder()
            .id("admin1")
            .role(Role.ADMIN)
            .build();

        validTask.setStatus(TaskStatus.PENDIENTE);

        when(taskRepository.findById("task1")).thenReturn(Optional.of(validTask));
        when(taskRepository.save(any(Task.class))).thenReturn(validTask);
        when(taskRepository.countByProjectId(anyString())).thenReturn(1L);
        when(taskRepository.countByProjectIdAndStatus(anyString(), any())).thenReturn(0L);
        when(projectRepository.findById("project1")).thenReturn(Optional.of(validProject));
        when(projectRepository.save(any(Project.class))).thenReturn(validProject);

        // Act
        taskService.updateTaskStatus("task1", TaskStatus.EN_PROGRESO, admin);

        // Assert - No debe lanzar excepción
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("updateTaskStatus - Debe actualizar progreso del proyecto")
    void updateTaskStatus_ShouldUpdateProjectProgress() {
        // Arrange
        validTask.setStatus(TaskStatus.EN_REVISION);

        when(taskRepository.findById("task1")).thenReturn(Optional.of(validTask));
        when(taskRepository.save(any(Task.class))).thenReturn(validTask);
        when(taskRepository.countByProjectId("project1")).thenReturn(10L); // 10 tareas totales
        when(taskRepository.countByProjectIdAndStatus("project1", TaskStatus.COMPLETADA))
            .thenReturn(3L); // 3 completadas

        when(projectRepository.findById("project1")).thenReturn(Optional.of(validProject));
        when(projectRepository.save(any(Project.class))).thenReturn(validProject);

        // Act - Completar tarea
        taskService.updateTaskStatus("task1", TaskStatus.COMPLETADA, validColaborador);

        // Assert - El proyecto debe actualizarse
        ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(projectCaptor.capture());

        // El progreso debe recalcularse
        verify(projectRepository, atLeastOnce()).save(any(Project.class));
    }

    // ==================== TESTS DE COMENTARIOS ====================

    @Test
    @DisplayName("addCommentToTask - Debe añadir comentario con datos válidos")
    void addCommentToTask_WithValidData_ShouldAddComment() {
        // Arrange
        when(taskRepository.findById("task1")).thenReturn(Optional.of(validTask));
        when(userRepository.findById("colab1")).thenReturn(Optional.of(validColaborador));
        when(taskRepository.save(any(Task.class))).thenReturn(validTask);

        // Act
        taskService.addCommentToTask("task1", "colab1", "Este es un comentario");

        // Assert
        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(taskCaptor.capture());

        Task savedTask = taskCaptor.getValue();
        assertThat(savedTask.getComments()).hasSize(1);
        assertThat(savedTask.getComments().get(0).getText()).isEqualTo("Este es un comentario");
        assertThat(savedTask.getComments().get(0).getUserId()).isEqualTo("colab1");
    }

    @Test
    @DisplayName("addCommentToTask - Debe rechazar comentario vacío")
    void addCommentToTask_WithEmptyComment_ShouldThrowException() {
        // Arrange
        when(taskRepository.findById("task1")).thenReturn(Optional.of(validTask));
        when(userRepository.findById("colab1")).thenReturn(Optional.of(validColaborador));

        // Act & Assert
        assertThatThrownBy(() ->
            taskService.addCommentToTask("task1", "colab1", "   ")
        )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("vacío");

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("addCommentToTask - Debe rechazar usuario inexistente")
    void addCommentToTask_WithNonExistentUser_ShouldThrowException() {
        // Arrange
        when(taskRepository.findById("task1")).thenReturn(Optional.of(validTask));
        when(userRepository.findById("invalid")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
            taskService.addCommentToTask("task1", "invalid", "Comentario")
        )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Usuario no encontrado");

        verify(taskRepository, never()).save(any(Task.class));
    }

    // ==================== TESTS DE LECTURA ====================

    @Test
    @DisplayName("getTaskById - Debe retornar tarea existente")
    void getTaskById_WithValidId_ShouldReturnTask() {
        // Arrange
        when(taskRepository.findById("task1")).thenReturn(Optional.of(validTask));

        // Act
        Task result = taskService.getTaskById("task1");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("task1");
        assertThat(result.getTitle()).isEqualTo("Tarea Test");
    }

    @Test
    @DisplayName("getTaskById - Debe lanzar excepción si no existe")
    void getTaskById_WithInvalidId_ShouldThrowException() {
        // Arrange
        when(taskRepository.findById("invalid")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> taskService.getTaskById("invalid"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Tarea no encontrada");
    }

    // ==================== TESTS DE VALIDACIÓN DE TRANSICIONES ====================

    @Test
    @DisplayName("Validar todas las transiciones válidas de PENDIENTE")
    void validateTransitions_FromPendiente() {
        // Arrange
        validTask.setStatus(TaskStatus.PENDIENTE);

        when(taskRepository.findById("task1")).thenReturn(Optional.of(validTask));
        when(taskRepository.save(any(Task.class))).thenReturn(validTask);
        when(taskRepository.countByProjectId(anyString())).thenReturn(1L);
        when(taskRepository.countByProjectIdAndStatus(anyString(), any())).thenReturn(0L);
        when(projectRepository.findById("project1")).thenReturn(Optional.of(validProject));
        when(projectRepository.save(any(Project.class))).thenReturn(validProject);

        // Act & Assert - PENDIENTE → EN_PROGRESO (VÁLIDA)
        assertThatCode(() ->
            taskService.updateTaskStatus("task1", TaskStatus.EN_PROGRESO, validColaborador)
        ).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Validar transiciones válidas de EN_PROGRESO")
    void validateTransitions_FromEnProgreso() {
        // Arrange
        validTask.setStatus(TaskStatus.EN_PROGRESO);

        when(taskRepository.findById("task1")).thenReturn(Optional.of(validTask));
        when(taskRepository.save(any(Task.class))).thenReturn(validTask);
        when(taskRepository.countByProjectId(anyString())).thenReturn(1L);
        when(taskRepository.countByProjectIdAndStatus(anyString(), any())).thenReturn(0L);
        when(projectRepository.findById("project1")).thenReturn(Optional.of(validProject));
        when(projectRepository.save(any(Project.class))).thenReturn(validProject);

        // EN_PROGRESO → EN_REVISION (VÁLIDA)
        assertThatCode(() ->
            taskService.updateTaskStatus("task1", TaskStatus.EN_REVISION, validColaborador)
        ).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Validar transición válida BLOQUEADA → EN_PROGRESO")
    void validateTransitions_FromBloqueada() {
        // Arrange
        validTask.setStatus(TaskStatus.BLOQUEADA);

        when(taskRepository.findById("task1")).thenReturn(Optional.of(validTask));
        when(taskRepository.save(any(Task.class))).thenReturn(validTask);
        when(taskRepository.countByProjectId(anyString())).thenReturn(1L);
        when(taskRepository.countByProjectIdAndStatus(anyString(), any())).thenReturn(0L);
        when(projectRepository.findById("project1")).thenReturn(Optional.of(validProject));
        when(projectRepository.save(any(Project.class))).thenReturn(validProject);

        // BLOQUEADA → EN_PROGRESO (VÁLIDA)
        assertThatCode(() ->
            taskService.updateTaskStatus("task1", TaskStatus.EN_PROGRESO, validColaborador)
        ).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Validar que COMPLETADA no permite transiciones")
    void validateTransitions_FromCompletada() {
        // Arrange
        validTask.setStatus(TaskStatus.COMPLETADA);

        when(taskRepository.findById("task1")).thenReturn(Optional.of(validTask));

        // Act & Assert - COMPLETADA no puede cambiar a ningún estado
        assertThatThrownBy(() ->
            taskService.updateTaskStatus("task1", TaskStatus.EN_PROGRESO, validColaborador)
        )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Transición de estado inválida");
    }
}
