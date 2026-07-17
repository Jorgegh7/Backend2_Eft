package com.minimarket.hateoas;

import com.minimarket.controller.UsuarioController;
import com.minimarket.dto.usuario.UsuarioResponseDTO;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UsuarioModelAssembler implements RepresentationModelAssembler<UsuarioResponseDTO, EntityModel<UsuarioResponseDTO>> {

    @Override
    public EntityModel<UsuarioResponseDTO> toModel(UsuarioResponseDTO usuario) {
        return EntityModel.of(usuario,
                linkTo(methodOn(UsuarioController.class).obtenerUsuarioPorId(usuario.id())).withSelfRel(),
                linkTo(methodOn(UsuarioController.class).actualizarUsuario(usuario.id(), null)).withRel("actualizar"),
                linkTo(methodOn(UsuarioController.class).eliminarUsuario(usuario.id())).withRel("eliminar")
        );
    }

    public CollectionModel<EntityModel<UsuarioResponseDTO>> toCollectionModel(List<UsuarioResponseDTO> usuarios) {
        List<EntityModel<UsuarioResponseDTO>> usuariosModel = usuarios.stream()
                .map(this::toModel)
                .toList();

        return CollectionModel.of(usuariosModel,
                linkTo(methodOn(UsuarioController.class).listarUsuarios()).withSelfRel());
    }
}
