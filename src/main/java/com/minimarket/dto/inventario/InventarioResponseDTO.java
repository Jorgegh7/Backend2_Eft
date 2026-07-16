package com.minimarket.dto.inventario;

import com.minimarket.entity.TipoMovimiento;

import java.time.LocalDateTime;

public record InventarioResponseDTO(
        Long id,
        Long productoId,
        String productoNombre,
        Integer cantidad,
        TipoMovimiento tipoMovimiento,
        LocalDateTime fechaMovimiento
) {
}
