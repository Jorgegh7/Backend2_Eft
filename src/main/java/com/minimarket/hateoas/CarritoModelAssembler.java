package com.minimarket.hateoas;

import com.minimarket.controller.CarritoController;
import com.minimarket.dto.carrito.CarritoResponseDTO;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CarritoModelAssembler
        implements RepresentationModelAssembler<CarritoResponseDTO, EntityModel<CarritoResponseDTO>> {

    @Override
    public EntityModel<CarritoResponseDTO> toModel(CarritoResponseDTO carrito) {
        return EntityModel.of(carrito,
                linkTo(methodOn(CarritoController.class).obtenerCarritoPorId(carrito.id())).withSelfRel(),
                linkTo(methodOn(CarritoController.class).listarCarrito()).withRel("carritos"),
                linkTo(methodOn(CarritoController.class).actualizarCarrito(carrito.id(), null)).withRel("actualizar"),
                linkTo(methodOn(CarritoController.class).eliminarProductoDelCarrito(carrito.id())).withRel("eliminar")
        );
    }

    public CollectionModel<EntityModel<CarritoResponseDTO>> toCollectionModel(List<CarritoResponseDTO> carritos) {
        List<EntityModel<CarritoResponseDTO>> carritosModel = carritos.stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(carritosModel,
                linkTo(methodOn(CarritoController.class).listarCarrito()).withSelfRel());
    }
}