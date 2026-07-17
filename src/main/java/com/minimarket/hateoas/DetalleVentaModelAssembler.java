package com.minimarket.hateoas;

import com.minimarket.controller.DetalleVentaController;
import com.minimarket.dto.detalleVenta.DetalleVentaResponseDTO;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class DetalleVentaModelAssembler
        implements RepresentationModelAssembler<DetalleVentaResponseDTO, EntityModel<DetalleVentaResponseDTO>> {

    @Override
    public EntityModel<DetalleVentaResponseDTO> toModel(DetalleVentaResponseDTO detalle) {
        return EntityModel.of(detalle,
                linkTo(methodOn(DetalleVentaController.class).obtenerDetalleVentaPorId(detalle.id())).withSelfRel(),
                linkTo(methodOn(DetalleVentaController.class).listarDetalleVentas()).withRel("detalleVentas"),
                linkTo(methodOn(DetalleVentaController.class).actualizarDetalleVenta(detalle.id(), null)).withRel("actualizar"),
                linkTo(methodOn(DetalleVentaController.class).eliminarDetalleVenta(detalle.id())).withRel("eliminar")
        );
    }

    public CollectionModel<EntityModel<DetalleVentaResponseDTO>> toCollectionModel(List<DetalleVentaResponseDTO> detalles) {
        List<EntityModel<DetalleVentaResponseDTO>> detallesModel = detalles.stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(detallesModel,
                linkTo(methodOn(DetalleVentaController.class).listarDetalleVentas()).withSelfRel());
    }
}