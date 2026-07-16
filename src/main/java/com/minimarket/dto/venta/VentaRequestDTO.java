package com.minimarket.dto.venta;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record VentaRequestDTO(
        @NotNull(message = "Venta debe tener un Usuario asociado")
        @Positive(message = "El ID debe ser un valor positivo") Long usuarioId,
        @NotEmpty(message = "Venta debe tener productos asociados")
        @Valid List<DetalleVentaItemDTO> detalles

) {
}
