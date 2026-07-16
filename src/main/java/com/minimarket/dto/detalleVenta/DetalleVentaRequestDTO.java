package com.minimarket.dto.detalleVenta;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DetalleVentaRequestDTO(
        @NotNull(message = "El detalle debe contar con un Id de Venta asociado")
        @Positive(message = "El Id debe ser positivo") Long ventaId,

        @NotNull(message = "El detalle debe contar con un Id de Producto asociado")
        @Positive(message = "El Id debe ser positivo") Long productoId,

        @NotNull(message = "Debe incluirse la cantidad")
        @Min(value = 1, message = "La cantidad debe ser al menos 1") Integer cantidad
) {
}