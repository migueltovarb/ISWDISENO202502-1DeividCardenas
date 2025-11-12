package com.gestionproyectos.gestion_tareas.service;

import com.gestionproyectos.gestion_tareas.dto.AdminPasswordChangeDto;
import com.gestionproyectos.gestion_tareas.dto.UserRegistrationDto;
import com.gestionproyectos.gestion_tareas.dto.UserUpdateDto;
import com.gestionproyectos.gestion_tareas.enums.Role;
import com.gestionproyectos.gestion_tareas.model.User;
import com.gestionproyectos.gestion_tareas.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests Unitarios para UserService
 *
 * COBERTURA:
 * - Registro de usuarios (CREATE)
 * - Validación de email único
 * - Validación de último ADMIN
 * - Activar/Desactivar usuarios (UPDATE)
 * - Eliminación de usuarios (DELETE)
 * - Cambio de contraseña
 * - Cambio de roles
 *
 * PATRÓN: AAA (Arrange, Act, Assert)
 * - Arrange: Configurar mocks y datos de prueba
 * - Act: Ejecutar el método a probar
 * - Assert: Verificar el resultado esperado
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService - Tests Unitarios")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserRegistrationDto validUserDto;
    private User validUser;

    @BeforeEach
    void setUp() {
        // Datos de prueba reutilizables
        validUserDto = new UserRegistrationDto(
            "Test User",
            "test@example.com",
            "SecurePass123!",
            Role.COLABORADOR
        );

        validUser = User.builder()
            .id("user123")
            .fullName("Test User")
            .email("test@example.com")
            .password("hashedPassword")
            .role(Role.COLABORADOR)
            .active(true)
            .leadProjectIds(new HashSet<>())
            .collaboratorProjectIds(new HashSet<>())
            .build();
    }

    // ==================== TESTS DE REGISTRO (CREATE) ====================

    @Test
    @DisplayName("registerUser - Debe crear usuario con email único")
    void registerUser_WithUniqueEmail_ShouldCreateUser() {
        // Arrange
        when(userRepository.existsByEmail(validUserDto.email())).thenReturn(false);
        when(passwordEncoder.encode(validUserDto.password())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(validUser);

        // Act
        userService.registerUser(validUserDto);

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getEmail()).isEqualTo(validUserDto.email());
        assertThat(savedUser.getFullName()).isEqualTo(validUserDto.fullName());
        assertThat(savedUser.getRole()).isEqualTo(validUserDto.role());
        assertThat(savedUser.isActive()).isTrue();

        // Verificar que la contraseña fue encriptada
        verify(passwordEncoder).encode(validUserDto.password());
    }

    @Test
    @DisplayName("registerUser - Debe lanzar excepción con email duplicado")
    void registerUser_WithDuplicateEmail_ShouldThrowException() {
        // Arrange
        when(userRepository.existsByEmail(validUserDto.email())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.registerUser(validUserDto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("ya está registrado");

        // Verificar que NO se intentó guardar
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("registerUser - Debe encriptar contraseña con BCrypt")
    void registerUser_ShouldEncryptPassword() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(validUserDto.password())).thenReturn("bcrypt$hashed$password");
        when(userRepository.save(any(User.class))).thenReturn(validUser);

        // Act
        userService.registerUser(validUserDto);

        // Assert
        verify(passwordEncoder).encode(validUserDto.password());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        // La contraseña guardada NO debe ser la original
        assertThat(userCaptor.getValue().getPassword()).isNotEqualTo(validUserDto.password());
    }

    // ==================== TESTS DE LECTURA (READ) ====================

    @Test
    @DisplayName("getUserById - Debe retornar usuario existente")
    void getUserById_WithValidId_ShouldReturnUser() {
        // Arrange
        when(userRepository.findById("user123")).thenReturn(Optional.of(validUser));

        // Act
        User result = userService.getUserById("user123");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("user123");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("getUserById - Debe lanzar excepción si no existe")
    void getUserById_WithInvalidId_ShouldThrowException() {
        // Arrange
        when(userRepository.findById("invalid")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.getUserById("invalid"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Usuario no encontrado");
    }

    @Test
    @DisplayName("getUserByEmail - Debe retornar usuario por email")
    void getUserByEmail_WithValidEmail_ShouldReturnUser() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(validUser));

        // Act
        User result = userService.getUserByEmail("test@example.com");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
    }

    // ==================== TESTS DE DESACTIVACIÓN (SOFT DELETE) ====================

    @Test
    @DisplayName("deactivateUser - Debe desactivar usuario normal")
    void deactivateUser_WithRegularUser_ShouldDeactivate() {
        // Arrange
        when(userRepository.findById("user123")).thenReturn(Optional.of(validUser));
        when(userRepository.save(any(User.class))).thenReturn(validUser);

        // Act
        userService.deactivateUser("user123");

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        assertThat(userCaptor.getValue().isActive()).isFalse();
    }

    @Test
    @DisplayName("deactivateUser - Debe rechazar desactivar último ADMIN")
    void deactivateUser_WithLastAdmin_ShouldThrowException() {
        // Arrange
        User admin = User.builder()
            .id("admin1")
            .email("admin@example.com")
            .role(Role.ADMIN)
            .active(true)
            .build();

        when(userRepository.findById("admin1")).thenReturn(Optional.of(admin));
        when(userRepository.countByRole(Role.ADMIN)).thenReturn(1L); // Solo 1 ADMIN

        // Act & Assert
        assertThatThrownBy(() -> userService.deactivateUser("admin1"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("único administrador");

        // Verificar que NO se guardó
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("deactivateUser - Debe permitir desactivar ADMIN si hay más de uno")
    void deactivateUser_WithMultipleAdmins_ShouldDeactivate() {
        // Arrange
        User admin = User.builder()
            .id("admin1")
            .email("admin@example.com")
            .role(Role.ADMIN)
            .active(true)
            .build();

        when(userRepository.findById("admin1")).thenReturn(Optional.of(admin));
        when(userRepository.countByRole(Role.ADMIN)).thenReturn(3L); // 3 ADMINs
        when(userRepository.save(any(User.class))).thenReturn(admin);

        // Act
        userService.deactivateUser("admin1");

        // Assert - Debe permitir desactivar
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("activateUser - Debe reactivar usuario desactivado")
    void activateUser_WithDeactivatedUser_ShouldActivate() {
        // Arrange
        User deactivatedUser = User.builder()
            .id("user123")
            .email("test@example.com")
            .active(false)
            .build();

        when(userRepository.findById("user123")).thenReturn(Optional.of(deactivatedUser));
        when(userRepository.save(any(User.class))).thenReturn(deactivatedUser);

        // Act
        userService.activateUser("user123");

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        assertThat(userCaptor.getValue().isActive()).isTrue();
    }

    // ==================== TESTS DE ELIMINACIÓN (HARD DELETE) ====================

    @Test
    @DisplayName("deleteUser - Debe eliminar usuario sin proyectos")
    void deleteUser_WithNoProjects_ShouldDelete() {
        // Arrange
        when(userRepository.findById("user123")).thenReturn(Optional.of(validUser));

        // Act
        userService.deleteUser("user123");

        // Assert
        verify(userRepository).delete(validUser);
    }

    @Test
    @DisplayName("deleteUser - Debe rechazar eliminar último ADMIN")
    void deleteUser_WithLastAdmin_ShouldThrowException() {
        // Arrange
        User admin = User.builder()
            .id("admin1")
            .email("admin@example.com")
            .role(Role.ADMIN)
            .active(true)
            .build();

        when(userRepository.findById("admin1")).thenReturn(Optional.of(admin));
        when(userRepository.countByRole(Role.ADMIN)).thenReturn(1L);

        // Act & Assert
        assertThatThrownBy(() -> userService.deleteUser("admin1"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("único administrador");

        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    @DisplayName("deleteUser - Debe rechazar eliminar LIDER con proyectos activos")
    void deleteUser_WithLiderWithProjects_ShouldThrowException() {
        // Arrange
        User lider = User.builder()
            .id("lider1")
            .email("lider@example.com")
            .role(Role.LIDER)
            .leadProjectIds(new HashSet<>())
            .build();

        lider.getLeadProjectIds().add("project1");
        lider.getLeadProjectIds().add("project2");

        when(userRepository.findById("lider1")).thenReturn(Optional.of(lider));

        // Act & Assert
        assertThatThrownBy(() -> userService.deleteUser("lider1"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("líder de 2 proyecto");

        verify(userRepository, never()).delete(any(User.class));
    }

    // ==================== TESTS DE ACTUALIZACIÓN (UPDATE) ====================

    @Test
    @DisplayName("updateUser - Debe actualizar nombre y rol")
    void updateUser_WithValidData_ShouldUpdate() {
        // Arrange
        UserUpdateDto updateDto = new UserUpdateDto(
            "user123",
            "Updated Name",
            Role.LIDER
        );

        when(userRepository.findById("user123")).thenReturn(Optional.of(validUser));
        when(userRepository.save(any(User.class))).thenReturn(validUser);

        // Act
        userService.updateUser(updateDto);

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User updatedUser = userCaptor.getValue();
        assertThat(updatedUser.getFullName()).isEqualTo("Updated Name");
        assertThat(updatedUser.getRole()).isEqualTo(Role.LIDER);
    }

    @Test
    @DisplayName("updateUser - Debe rechazar cambiar rol de LIDER con proyectos")
    void updateUser_WithLiderWithProjects_ShouldThrowException() {
        // Arrange
        User lider = User.builder()
            .id("lider1")
            .fullName("Lider Test")
            .email("lider@example.com")
            .role(Role.LIDER)
            .leadProjectIds(new HashSet<>())
            .build();

        lider.getLeadProjectIds().add("project1");

        UserUpdateDto updateDto = new UserUpdateDto(
            "lider1",
            "Lider Test",
            Role.COLABORADOR // Intentar cambiar a COLABORADOR
        );

        when(userRepository.findById("lider1")).thenReturn(Optional.of(lider));

        // Act & Assert
        assertThatThrownBy(() -> userService.updateUser(updateDto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("proyectos activos");

        verify(userRepository, never()).save(any(User.class));
    }

    // ==================== TESTS DE CAMBIO DE CONTRASEÑA ====================

    @Test
    @DisplayName("adminChangePassword - Debe cambiar contraseña y encriptarla")
    void adminChangePassword_WithValidData_ShouldChangePassword() {
        // Arrange
        AdminPasswordChangeDto dto = new AdminPasswordChangeDto(
            "user123",
            "NewSecurePass123!"
        );

        when(userRepository.findById("user123")).thenReturn(Optional.of(validUser));
        when(passwordEncoder.encode("NewSecurePass123!")).thenReturn("newHashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(validUser);

        // Act
        userService.adminChangePassword(dto);

        // Assert
        verify(passwordEncoder).encode("NewSecurePass123!");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        assertThat(userCaptor.getValue().getPassword()).isEqualTo("newHashedPassword");
    }

    // ==================== TESTS DE VALIDACIÓN ====================

    @Test
    @DisplayName("existsByEmail - Debe retornar true si email existe")
    void existsByEmail_WithExistingEmail_ShouldReturnTrue() {
        // Arrange
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // Act
        boolean result = userService.existsByEmail("test@example.com");

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("existsByEmail - Debe retornar false si email no existe")
    void existsByEmail_WithNonExistingEmail_ShouldReturnFalse() {
        // Arrange
        when(userRepository.existsByEmail("nonexistent@example.com")).thenReturn(false);

        // Act
        boolean result = userService.existsByEmail("nonexistent@example.com");

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("countByRole - Debe contar usuarios por rol")
    void countByRole_ShouldReturnCount() {
        // Arrange
        when(userRepository.countByRole(Role.ADMIN)).thenReturn(5L);

        // Act
        long count = userService.countByRole(Role.ADMIN);

        // Assert
        assertThat(count).isEqualTo(5L);
    }
}
