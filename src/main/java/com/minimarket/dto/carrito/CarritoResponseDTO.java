package com.minimarket.dto.carrito;

public record CarritoResponseDTO(
        Long id,
        Long usuarioId,
        String usuarioUsername,
        Long productoId,
        String productoNombre,
        Integer cantidad
) {
}
