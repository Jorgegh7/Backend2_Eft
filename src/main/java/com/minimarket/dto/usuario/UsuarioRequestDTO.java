package com.minimarket.dto.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record UsuarioRequestDTO(
        @NotEmpty(message = "El usuario debe tener al menos un rol") Set<String> roles
) {
}