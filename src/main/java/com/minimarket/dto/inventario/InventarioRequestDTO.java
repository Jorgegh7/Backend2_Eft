package com.minimarket.dto.inventario;

import com.minimarket.entity.TipoMovimiento;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record InventarioRequestDTO(
        @NotNull(message = "Debes incluir un Id de producto")
        @Positive(message = "El Id debe ser positivo") Long productoId,
        @Min(value = 1, message = "La cantidad debe ser mayor a 0")
        @NotNull(message = "Debes incluir la catidad") Integer cantidad,
        @NotNull(message = "Debes incluir un tipo de movimiento") TipoMovimiento tipoMovimiento
) {
}
