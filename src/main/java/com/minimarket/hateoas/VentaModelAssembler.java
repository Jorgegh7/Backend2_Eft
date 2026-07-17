package com.minimarket.hateoas;

import com.minimarket.controller.VentaController;
import com.minimarket.dto.venta.VentaResponseDTO;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class VentaModelAssembler
        implements RepresentationModelAssembler<VentaResponseDTO, EntityModel<VentaResponseDTO>> {

    @Override
    public EntityModel<VentaResponseDTO> toModel(VentaResponseDTO venta) {
        return EntityModel.of(venta,
                linkTo(methodOn(VentaController.class).obtenerVentaPorId(venta.id())).withSelfRel(),
                linkTo(methodOn(VentaController.class).listarVentas()).withRel("ventas")
        );
    }

    public CollectionModel<EntityModel<VentaResponseDTO>> toCollectionModel(List<VentaResponseDTO> ventas) {
        List<EntityModel<VentaResponseDTO>> ventasModel = ventas.stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(ventasModel,
                linkTo(methodOn(VentaController.class).listarVentas()).withSelfRel());
    }
}