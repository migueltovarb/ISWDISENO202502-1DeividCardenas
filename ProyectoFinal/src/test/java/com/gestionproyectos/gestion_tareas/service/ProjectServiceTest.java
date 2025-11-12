package com.gestionproyectos.gestion_tareas.service;

import com.gestionproyectos.gestion_tareas.dto.ProjectCreationDto;
import com.gestionproyectos.gestion_tareas.dto.ProjectUpdateDto;
import com.gestionproyectos.gestion_tareas.enums.ProjectStatus;
import com.gestionproyectos.gestion_tareas.enums.Role;
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

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests Unitarios para ProjectService
 *
 * COBERTURA:
 * - Creación de proyectos (CREATE)
 * - Validación de líder válido
 * - Actualización de proyectos (UPDATE)
 * - Cambio de líder con relaciones bidireccionales
 * - Gestión de colaboradores (ADD/REMOVE)
 * - Sincronización de colaboradores desde tareas
 * - Búsqueda y filtros (READ)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProjectService - Tests Unitarios")
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private ProjectService projectService;

    private ProjectCreationDto validProjectDto;
    private User validLider;
    private User validColaborador;
    private Project validProject;

    @BeforeEach
    void setUp() {
        // Líder válido
        validLider = User.builder()
            .id("lider1")
            .fullName("Líder Test")
            .email("lider@example.com")
            .role(Role.LIDER)
            .active(true)
            .leadProjectIds(new HashSet<>())
            .collaboratorProjectIds(new HashSet<>())
            .build();

        // Colaborador válido
        validColaborador = User.builder()
            .id("colab1")
            .fullName("Colaborador Test")
            .email("colab@example.com")
            .role(Role.COLABORADOR)
            .active(true)
            .leadProjectIds(new HashSet<>())
            .collaboratorProjectIds(new HashSet<>())
            .build();

        // DTO de creación válido
        validProjectDto = new ProjectCreationDto(
            "Proyecto Test",
            "Descripción del proyecto test",
            "lider1"
        );

        // Proyecto válido
        validProject = Project.builder()
            .id("project1")
            .name("Proyecto Test")
            .description("Descripción del proyecto test")
            .leaderId("lider1")
            .collaboratorIds(new HashSet<>())
            .status(ProjectStatus.PLANIFICACION)
            .progress(0)
            .archived(false)
            .build();
    }

    // ==================== TESTS DE CREACIÓN (CREATE) ====================

    @Test
    @DisplayName("createProject - Debe crear proyecto con líder válido")
    void createProject_WithValidLeader_ShouldCreateProject() {
        // Arrange
        when(userRepository.findById("lider1")).thenReturn(Optional.of(validLider));
        when(projectRepository.save(any(Project.class))).thenReturn(validProject);
        when(userRepository.save(any(User.class))).thenReturn(validLider);

        // Act
        projectService.createProject(validProjectDto);

        // Assert
        // Verificar que se guardó el proyecto
        ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(projectCaptor.capture());

        Project savedProject = projectCaptor.getValue();
        assertThat(savedProject.getName()).isEqualTo("Proyecto Test");
        assertThat(savedProject.getLeaderId()).isEqualTo("lider1");
        assertThat(savedProject.getStatus()).isEqualTo(ProjectStatus.PLANIFICACION);
        assertThat(savedProject.getProgress()).isEqualTo(0);
        assertThat(savedProject.isArchived()).isFalse();

        // Verificar que se actualizó la relación bidireccional en el usuario
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getLeadProjectIds()).contains("project1");
    }

    @Test
    @DisplayName("createProject - Debe rechazar líder inexistente")
    void createProject_WithNonExistentLeader_ShouldThrowException() {
        // Arrange
        when(userRepository.findById("invalid")).thenReturn(Optional.empty());

        ProjectCreationDto invalidDto = new ProjectCreationDto(
            "Proyecto Test",
            "Descripción",
            "invalid"
        );

        // Act & Assert
        assertThatThrownBy(() -> projectService.createProject(invalidDto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Líder no encontrado");

        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    @DisplayName("createProject - Debe rechazar usuario sin rol de líder")
    void createProject_WithNonLeaderRole_ShouldThrowException() {
        // Arrange
        User colaborador = User.builder()
            .id("colab1")
            .role(Role.COLABORADOR)
            .build();

        when(userRepository.findById("colab1")).thenReturn(Optional.of(colaborador));

        ProjectCreationDto invalidDto = new ProjectCreationDto(
            "Proyecto Test",
            "Descripción",
            "colab1"
        );

        // Act & Assert
        assertThatThrownBy(() -> projectService.createProject(invalidDto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("no es un líder válido");

        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    @DisplayName("createProject - Debe permitir ADMIN como líder")
    void createProject_WithAdminAsLeader_ShouldCreateProject() {
        // Arrange
        User admin = User.builder()
            .id("admin1")
            .role(Role.ADMIN)
            .leadProjectIds(new HashSet<>())
            .build();

        when(userRepository.findById("admin1")).thenReturn(Optional.of(admin));
        when(projectRepository.save(any(Project.class))).thenReturn(validProject);
        when(userRepository.save(any(User.class))).thenReturn(admin);

        ProjectCreationDto dto = new ProjectCreationDto(
            "Proyecto Admin",
            "Descripción",
            "admin1"
        );

        // Act
        projectService.createProject(dto);

        // Assert - No debe lanzar excepción
        verify(projectRepository).save(any(Project.class));
    }

    // ==================== TESTS DE LECTURA (READ) ====================

    @Test
    @DisplayName("getProjectById - Debe retornar proyecto existente")
    void getProjectById_WithValidId_ShouldReturnProject() {
        // Arrange
        when(projectRepository.findById("project1")).thenReturn(Optional.of(validProject));

        // Act
        Project result = projectService.getProjectById("project1");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("project1");
        assertThat(result.getName()).isEqualTo("Proyecto Test");
    }

    @Test
    @DisplayName("getProjectById - Debe lanzar excepción si no existe")
    void getProjectById_WithInvalidId_ShouldThrowException() {
        // Arrange
        when(projectRepository.findById("invalid")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> projectService.getProjectById("invalid"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Proyecto no encontrado");
    }

    @Test
    @DisplayName("getProjectsForUser - Debe retornar proyectos del usuario")
    void getProjectsForUser_ShouldReturnUserProjects() {
        // Arrange
        List<Project> projects = Arrays.asList(validProject);
        when(projectRepository.findAllByUserId("lider1")).thenReturn(projects);

        // Act
        List<Project> result = projectService.getProjectsForUser("lider1");

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("project1");
    }

    // ==================== TESTS DE ACTUALIZACIÓN (UPDATE) ====================

    @Test
    @DisplayName("updateProject - Debe actualizar información básica")
    void updateProject_WithBasicInfo_ShouldUpdate() {
        // Arrange
        ProjectUpdateDto updateDto = new ProjectUpdateDto(
            "project1",
            "Nombre Actualizado",
            "Nueva Descripción",
            "lider1", // Mismo líder
            ProjectStatus.EN_PROGRESO
        );

        when(projectRepository.findById("project1")).thenReturn(Optional.of(validProject));
        when(projectRepository.save(any(Project.class))).thenReturn(validProject);

        // Act
        projectService.updateProject(updateDto);

        // Assert
        ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(captor.capture());

        Project updated = captor.getValue();
        assertThat(updated.getName()).isEqualTo("Nombre Actualizado");
        assertThat(updated.getDescription()).isEqualTo("Nueva Descripción");
        assertThat(updated.getStatus()).isEqualTo(ProjectStatus.EN_PROGRESO);
    }

    @Test
    @DisplayName("updateProject - Debe cambiar líder correctamente")
    void updateProject_WithNewLeader_ShouldUpdateBidirectionalRelations() {
        // Arrange
        User newLider = User.builder()
            .id("lider2")
            .role(Role.LIDER)
            .leadProjectIds(new HashSet<>())
            .build();

        validLider.addLeadProject("project1"); // Líder actual tiene el proyecto

        ProjectUpdateDto updateDto = new ProjectUpdateDto(
            "project1",
            "Proyecto Test",
            "Descripción",
            "lider2", // Nuevo líder
            ProjectStatus.PLANIFICACION
        );

        when(projectRepository.findById("project1")).thenReturn(Optional.of(validProject));
        when(userRepository.findById("lider1")).thenReturn(Optional.of(validLider));
        when(userRepository.findById("lider2")).thenReturn(Optional.of(newLider));
        when(projectRepository.save(any(Project.class))).thenReturn(validProject);

        // Act
        projectService.updateProject(updateDto);

        // Assert
        // Verificar que se removió del líder anterior
        verify(userRepository).save(argThat(user ->
            user.getId().equals("lider1") &&
            !user.getLeadProjectIds().contains("project1")
        ));

        // Verificar que se añadió al nuevo líder
        verify(userRepository).save(argThat(user ->
            user.getId().equals("lider2") &&
            user.getLeadProjectIds().contains("project1")
        ));

        // Verificar que el proyecto se actualizó
        verify(projectRepository).save(argThat(project ->
            project.getLeaderId().equals("lider2")
        ));
    }

    @Test
    @DisplayName("updateProject - Debe rechazar nuevo líder inválido")
    void updateProject_WithInvalidNewLeader_ShouldThrowException() {
        // Arrange
        User colaborador = User.builder()
            .id("colab1")
            .role(Role.COLABORADOR) // No es LIDER
            .build();

        ProjectUpdateDto updateDto = new ProjectUpdateDto(
            "project1",
            "Proyecto Test",
            "Descripción",
            "colab1", // Colaborador, no líder
            ProjectStatus.PLANIFICACION
        );

        when(projectRepository.findById("project1")).thenReturn(Optional.of(validProject));
        when(userRepository.findById("lider1")).thenReturn(Optional.of(validLider));
        when(userRepository.findById("colab1")).thenReturn(Optional.of(colaborador));

        // Act & Assert
        assertThatThrownBy(() -> projectService.updateProject(updateDto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("no es un líder válido");
    }

    // ==================== TESTS DE COLABORADORES ====================

    @Test
    @DisplayName("addCollaboratorToProject - Debe agregar colaborador correctamente")
    void addCollaboratorToProject_WithValidUser_ShouldAddCollaborator() {
        // Arrange
        when(projectRepository.findById("project1")).thenReturn(Optional.of(validProject));
        when(userRepository.findById("colab1")).thenReturn(Optional.of(validColaborador));
        when(projectRepository.save(any(Project.class))).thenReturn(validProject);
        when(userRepository.save(any(User.class))).thenReturn(validColaborador);

        // Act
        projectService.addCollaboratorToProject("project1", "colab1");

        // Assert
        // Verificar relación bidireccional - Proyecto
        verify(projectRepository).save(argThat(project ->
            project.getCollaboratorIds().contains("colab1")
        ));

        // Verificar relación bidireccional - Usuario
        verify(userRepository).save(argThat(user ->
            user.getCollaboratorProjectIds().contains("project1")
        ));
    }

    @Test
    @DisplayName("addCollaboratorToProject - Debe rechazar líder como colaborador")
    void addCollaboratorToProject_WithProjectLeader_ShouldThrowException() {
        // Arrange
        when(projectRepository.findById("project1")).thenReturn(Optional.of(validProject));
        when(userRepository.findById("lider1")).thenReturn(Optional.of(validLider));

        // Act & Assert
        assertThatThrownBy(() ->
            projectService.addCollaboratorToProject("project1", "lider1")
        )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("líder ya está asignado");

        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    @DisplayName("addCollaboratorToProject - Debe ignorar colaborador duplicado")
    void addCollaboratorToProject_WithExistingCollaborator_ShouldIgnore() {
        // Arrange
        validProject.getCollaboratorIds().add("colab1"); // Ya es colaborador

        when(projectRepository.findById("project1")).thenReturn(Optional.of(validProject));
        when(userRepository.findById("colab1")).thenReturn(Optional.of(validColaborador));

        // Act
        projectService.addCollaboratorToProject("project1", "colab1");

        // Assert - No debe guardar nada
        verify(projectRepository, never()).save(any(Project.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("removeCollaboratorFromProject - Debe remover colaborador correctamente")
    void removeCollaboratorFromProject_WithExistingCollaborator_ShouldRemove() {
        // Arrange
        validProject.getCollaboratorIds().add("colab1");
        validColaborador.getCollaboratorProjectIds().add("project1");

        when(projectRepository.findById("project1")).thenReturn(Optional.of(validProject));
        when(userRepository.findById("colab1")).thenReturn(Optional.of(validColaborador));
        when(projectRepository.save(any(Project.class))).thenReturn(validProject);
        when(userRepository.save(any(User.class))).thenReturn(validColaborador);

        // Act
        projectService.removeCollaboratorFromProject("project1", "colab1");

        // Assert
        // Verificar que se removió del proyecto
        verify(projectRepository).save(argThat(project ->
            !project.getCollaboratorIds().contains("colab1")
        ));

        // Verificar que se removió del usuario
        verify(userRepository).save(argThat(user ->
            !user.getCollaboratorProjectIds().contains("project1")
        ));
    }

    // ==================== TESTS DE SINCRONIZACIÓN ====================

    @Test
    @DisplayName("syncCollaboratorsFromTasks - Debe sincronizar colaboradores de tareas")
    void syncCollaboratorsFromTasks_ShouldAddMissingCollaborators() {
        // Arrange
        Task task1 = Task.builder()
            .id("task1")
            .assignedToId("colab1")
            .build();

        Task task2 = Task.builder()
            .id("task2")
            .assignedToId("colab2")
            .build();

        User colab2 = User.builder()
            .id("colab2")
            .collaboratorProjectIds(new HashSet<>())
            .build();

        when(projectRepository.findById("project1")).thenReturn(Optional.of(validProject));
        when(taskRepository.findByProjectId("project1")).thenReturn(Arrays.asList(task1, task2));
        when(userRepository.findById("colab1")).thenReturn(Optional.of(validColaborador));
        when(userRepository.findById("colab2")).thenReturn(Optional.of(colab2));
        when(projectRepository.save(any(Project.class))).thenReturn(validProject);

        // Act
        int added = projectService.syncCollaboratorsFromTasks("project1");

        // Assert
        assertThat(added).isEqualTo(2); // Debe añadir 2 colaboradores

        // Verificar que se guardó el proyecto con los colaboradores
        verify(projectRepository).save(argThat(project ->
            project.getCollaboratorIds().contains("colab1") &&
            project.getCollaboratorIds().contains("colab2")
        ));
    }

    @Test
    @DisplayName("syncCollaboratorsFromTasks - No debe duplicar colaboradores existentes")
    void syncCollaboratorsFromTasks_ShouldNotDuplicateExisting() {
        // Arrange
        validProject.getCollaboratorIds().add("colab1"); // Ya existe

        Task task1 = Task.builder()
            .id("task1")
            .assignedToId("colab1")
            .build();

        when(projectRepository.findById("project1")).thenReturn(Optional.of(validProject));
        when(taskRepository.findByProjectId("project1")).thenReturn(Collections.singletonList(task1));

        // Act
        int added = projectService.syncCollaboratorsFromTasks("project1");

        // Assert
        assertThat(added).isEqualTo(0); // No debe añadir nada

        // No debe guardar si no hay cambios
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    @DisplayName("syncCollaboratorsFromTasks - No debe añadir al líder como colaborador")
    void syncCollaboratorsFromTasks_ShouldNotAddLeaderAsCollaborator() {
        // Arrange
        Task task1 = Task.builder()
            .id("task1")
            .assignedToId("lider1") // Tarea asignada al líder
            .build();

        when(projectRepository.findById("project1")).thenReturn(Optional.of(validProject));
        when(taskRepository.findByProjectId("project1")).thenReturn(Collections.singletonList(task1));

        // Act
        int added = projectService.syncCollaboratorsFromTasks("project1");

        // Assert
        assertThat(added).isEqualTo(0);

        // No debe guardar cambios
        verify(projectRepository, never()).save(any(Project.class));
    }

    // ==================== TESTS DE BÚSQUEDA Y FILTROS ====================

    @Test
    @DisplayName("searchProjects - Debe buscar por texto en nombre y descripción")
    void searchProjects_WithText_ShouldSearchInNameAndDescription() {
        // Arrange
        when(projectRepository.findByNameOrDescriptionContaining("test"))
            .thenReturn(Collections.singletonList(validProject));

        // Act
        List<Project> results = projectService.searchProjects("test", null, false);

        // Assert
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).contains("Test");
    }

    @Test
    @DisplayName("searchProjects - Debe filtrar por estado")
    void searchProjects_WithStatus_ShouldFilterByStatus() {
        // Arrange
        when(projectRepository.findByStatus(ProjectStatus.PLANIFICACION))
            .thenReturn(Collections.singletonList(validProject));

        // Act
        List<Project> results = projectService.searchProjects(null, ProjectStatus.PLANIFICACION, false);

        // Assert
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getStatus()).isEqualTo(ProjectStatus.PLANIFICACION);
    }

    @Test
    @DisplayName("searchProjects - Debe excluir archivados por defecto")
    void searchProjects_WithoutArchived_ShouldExcludeArchivedProjects() {
        // Arrange
        Project archivedProject = Project.builder()
            .id("archived1")
            .archived(true)
            .build();

        when(projectRepository.findAll())
            .thenReturn(Arrays.asList(validProject, archivedProject));

        // Act
        List<Project> results = projectService.searchProjects(null, null, false);

        // Assert
        assertThat(results).hasSize(1);
        assertThat(results).noneMatch(Project::isArchived);
    }
}
