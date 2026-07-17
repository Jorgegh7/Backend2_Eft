package com.minimarket.hateoas;

import com.minimarket.controller.InventarioController;
import com.minimarket.dto.inventario.InventarioResponseDTO;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class InventarioModelAssembler
        implements RepresentationModelAssembler<InventarioResponseDTO, EntityModel<InventarioResponseDTO>> {

    @Override
    public EntityModel<InventarioResponseDTO> toModel(InventarioResponseDTO inventario) {
        return EntityModel.of(inventario,
                linkTo(methodOn(InventarioController.class).obtenerInventarioPorId(inventario.id())).withSelfRel(),
                linkTo(methodOn(InventarioController.class).listarInventario()).withRel("inventario"),
                linkTo(methodOn(InventarioController.class).eliminarMovimiento(inventario.id())).withRel("eliminar")
        );
    }

    public CollectionModel<EntityModel<InventarioResponseDTO>> toCollectionModel(List<InventarioResponseDTO> inventarios) {
        List<EntityModel<InventarioResponseDTO>> inventariosModel = inventarios.stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(inventariosModel,
                linkTo(methodOn(InventarioController.class).listarInventario()).withSelfRel());
    }
}