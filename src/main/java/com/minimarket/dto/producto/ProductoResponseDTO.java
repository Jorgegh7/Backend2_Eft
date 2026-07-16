package com.minimarket.dto.producto;

public record ProductoResponseDTO(
        Long id,
        String nombre,
        Double precio,
        Integer stock,
        Long categoriaId,
        String categoriaNombre
) {
}
