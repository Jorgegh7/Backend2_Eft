package com.minimarket.dto.detalleVenta;

public record DetalleVentaResponseDTO(
        Long id,
        Long productoId,
        String productoNombre,
        Integer cantidad,
        Double precio
) {}