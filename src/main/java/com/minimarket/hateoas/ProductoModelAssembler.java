package com.minimarket.hateoas;

import com.minimarket.controller.ProductoController;
import com.minimarket.dto.producto.ProductoResponseDTO;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ProductoModelAssembler
        implements RepresentationModelAssembler<ProductoResponseDTO, EntityModel<ProductoResponseDTO>> {

    @Override
    public EntityModel<ProductoResponseDTO> toModel(ProductoResponseDTO producto) {
        return EntityModel.of(producto,
                linkTo(methodOn(ProductoController.class).obtenerProductoPorId(producto.id())).withSelfRel(),
                linkTo(methodOn(ProductoController.class).listarProductos()).withRel("productos"),
                linkTo(methodOn(ProductoController.class).actualizarProducto(producto.id(), null)).withRel("actualizar"),
                linkTo(methodOn(ProductoController.class).eliminarProducto(producto.id())).withRel("eliminar")
        );
    }

    public CollectionModel<EntityModel<ProductoResponseDTO>> toCollectionModel(List<ProductoResponseDTO> productos) {
        List<EntityModel<ProductoResponseDTO>> productosModel = productos.stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(productosModel,
                linkTo(methodOn(ProductoController.class).listarProductos()).withSelfRel());
    }
}