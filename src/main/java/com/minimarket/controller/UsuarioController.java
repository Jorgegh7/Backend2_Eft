package com.minimarket.controller;

import com.minimarket.dto.usuario.UsuarioRequestDTO;
import com.minimarket.dto.usuario.UsuarioResponseDTO;
import com.minimarket.entity.Usuario;
import com.minimarket.hateoas.UsuarioModelAssembler;
import com.minimarket.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Operaciones para gestionar Usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioModelAssembler assembler;

    public UsuarioController(UsuarioService usuarioService, UsuarioModelAssembler assembler) {
        this.usuarioService = usuarioService;
        this.assembler = assembler;
    }

    @Operation(summary = "Listar Usuarios", description = "Obtiene una lista con todos los Usuarios")
    @ApiResponse(responseCode = "200", description = "Listado obtenido de forma correcta")
    @GetMapping
    public CollectionModel<EntityModel<UsuarioResponseDTO>> listarUsuarios() {
        return assembler.toCollectionModel(usuarioService.findAll());
    }

    @Operation(summary = "Retorna Usuario por ID", description = "Obtiene un usuario segun su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario obtenido de forma correcta"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}")
    public EntityModel<UsuarioResponseDTO> obtenerUsuarioPorId(@PathVariable Long id) {
       return assembler.toModel(usuarioService.findById(id));
    }

    @Operation(summary = "Retorna Usuario por Username", description = "Obtiene un usuario segun su Username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario obtenido de forma correcta"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/username/{username}")
    public EntityModel<UsuarioResponseDTO> obtenerUsuarioPorUsername(@PathVariable String username){
        return assembler.toModel(usuarioService.findByUsername(username));
    }

    @Operation(summary = "Actualizar Usuario", description = "Actualiza un Usuario accediendo a este por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PutMapping("/{id}")
    public EntityModel<UsuarioResponseDTO> actualizarUsuario(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO usuarioRequestDTO) {
        return assembler.toModel(usuarioService.actualizar(id, usuarioRequestDTO));
    }

    @Operation(summary = "Eliminar Usuario", description = "Elimina un Usuario por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        usuarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
