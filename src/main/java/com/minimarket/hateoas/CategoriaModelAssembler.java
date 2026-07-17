package com.minimarket.hateoas;

import com.minimarket.controller.CategoriaController;
import com.minimarket.dto.categoria.CategoriaResponseDTO;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CategoriaModelAssembler implements RepresentationModelAssembler<CategoriaResponseDTO, EntityModel<CategoriaResponseDTO>> {

    @Override
    public EntityModel<CategoriaResponseDTO> toModel(CategoriaResponseDTO categoria) {
        return EntityModel.of(categoria,
                linkTo(methodOn(CategoriaController.class).obtenerCategoriaPorId(categoria.id())).withSelfRel(),
                linkTo(methodOn(CategoriaController.class).listarCategorias()).withRel("categorias"),
                linkTo(methodOn(CategoriaController.class).actualizarCategoria(categoria.id(), null)).withRel("actualizar"),
                linkTo(methodOn(CategoriaController.class).eliminarCategoria(categoria.id())).withRel("eliminar")
        );
    }

    public CollectionModel<EntityModel<CategoriaResponseDTO>> toCollectionModel(List<CategoriaResponseDTO> categorias) {
        List<EntityModel<CategoriaResponseDTO>> categoriasModel = categorias.stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(categoriasModel,
                linkTo(methodOn(CategoriaController.class).listarCategorias()).withSelfRel(),
                linkTo(methodOn(CategoriaController.class).guardarCategoria(null)).withRel("crear")
        );

    }
}