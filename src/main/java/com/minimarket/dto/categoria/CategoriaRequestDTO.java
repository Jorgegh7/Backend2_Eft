package com.minimarket.dto.categoria;

import jakarta.validation.constraints.NotBlank;

public record CategoriaRequestDTO(
        @NotBlank(message = "El nombre es obligatorio") String nombre
) {}