package com.minimarket.dto.venta;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DetalleVentaItemDTO(
        @NotNull @Positive Long productoId,
        @NotNull @Min(1) Integer cantidad
) {}