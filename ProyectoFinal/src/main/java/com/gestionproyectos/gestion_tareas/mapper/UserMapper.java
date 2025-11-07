package com.gestionproyectos.gestion_tareas.mapper;

import com.gestionproyectos.gestion_tareas.dto.UserListDto;
import com.gestionproyectos.gestion_tareas.dto.UserRegistrationDto;
import com.gestionproyectos.gestion_tareas.dto.UserSimpleDto;
import com.gestionproyectos.gestion_tareas.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * UserMapper - Conversor entre entidades User y DTOs
 *
 * PROPÓSITO:
 * Centraliza toda la lógica de conversión entre User y sus diferentes DTOs.
 * Esto evita duplicar código de mapeo en controladores y servicios.
 *
 * PATRÓN: Mapper Pattern
 * - Separa la lógica de conversión de la lógica de negocio
 * - Facilita mantener consistencia en las conversiones
 * - Simplifica los tests
 *
 * @author Sistema de Gestión de Tareas
 * @version 1.0
 * @since 2025-11-06
 */
@Component
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    /**
     * Convierte User a UserSimpleDto
     *
     * @param user Entidad User
     * @return UserSimpleDto con datos básicos
     */
    public UserSimpleDto toSimpleDto(User user) {
        if (user == null) {
            return null;
        }

        return UserSimpleDto.fromUser(user);
    }

    /**
     * Convierte lista de Users a lista de UserSimpleDtos
     *
     * @param users Lista de entidades User
     * @return Lista de UserSimpleDtos
     */
    public List<UserSimpleDto> toSimpleDtoList(List<User> users) {
        if (users == null) {
            return List.of();
        }

        return users.stream()
            .map(UserSimpleDto::fromUser)
            .collect(Collectors.toList());
    }

    /**
     * Convierte User a UserListDto
     *
     * @param user Entidad User
     * @return UserListDto con información extendida para listados
     */
    public UserListDto toListDto(User user) {
        if (user == null) {
            return null;
        }

        return UserListDto.fromUser(user);
    }

    /**
     * Convierte lista de Users a lista de UserListDtos
     *
     * @param users Lista de entidades User
     * @return Lista de UserListDtos
     */
    public List<UserListDto> toListDtoList(List<User> users) {
        if (users == null) {
            return List.of();
        }

        return users.stream()
            .map(UserListDto::fromUser)
            .collect(Collectors.toList());
    }

    /**
     * Convierte UserRegistrationDto a User (nueva entidad)
     *
     * IMPORTANTE: La contraseña se encripta con BCrypt
     *
     * @param dto DTO con datos de registro
     * @return User nuevo (sin guardar en BD)
     */
    public User toEntity(UserRegistrationDto dto) {
        if (dto == null) {
            return null;
        }

        return User.builder()
            .fullName(dto.fullName())
            .email(dto.email())
            .password(passwordEncoder.encode(dto.password()))
            .role(dto.role())
            .active(true)
            .build();
    }

    /**
     * Actualiza una entidad User existente con datos de un DTO
     *
     * NO actualiza: password, email, createdAt
     * (estos requieren validaciones especiales)
     *
     * @param user Entidad User a actualizar
     * @param fullName Nuevo nombre completo
     * @param active Nuevo estado activo/inactivo
     */
    public void updateEntity(User user, String fullName, Boolean active) {
        if (user == null) {
            return;
        }

        if (fullName != null && !fullName.trim().isEmpty()) {
            user.setFullName(fullName);
        }

        if (active != null) {
            user.setActive(active);
        }
    }
}
