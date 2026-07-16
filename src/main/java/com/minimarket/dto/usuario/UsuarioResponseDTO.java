package com.minimarket.dto.usuario;

import java.util.Set;

public record UsuarioResponseDTO(
        Long id,
        String username,
        Set<String> roles
) {
}
