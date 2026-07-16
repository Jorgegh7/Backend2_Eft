package com.minimarket.dto.carrito;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CarritoRequestDTO(
        @NotNull(message = "Carrito debe tener un Usuario asociado")
        @Positive(message = "El ID debe ser un valor positivo") Long usuarioId,

        @NotNull(message = "Carrito debe tener un Producto asociado")
        @Positive(message = "El ID debe ser un valor positivo") Long productoId,

        @NotNull(message = "Debes seleccionar una cantidad")
        @Min(value = 1, message = "Debes seleccionar una cantidad para el producto") Integer cantidad
) {
}