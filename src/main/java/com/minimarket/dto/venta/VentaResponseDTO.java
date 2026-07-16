package com.minimarket.dto.venta;

import com.minimarket.dto.detalleVenta.DetalleVentaResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public record VentaResponseDTO(
        Long id,
        Long usuarioId,
        String usuarioUsername,
        LocalDateTime fecha,
        Double total,
        List<DetalleVentaResponseDTO> detalles
) {
}