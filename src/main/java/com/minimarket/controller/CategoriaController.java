package com.minimarket.controller;

import com.minimarket.dto.categoria.CategoriaRequestDTO;
import com.minimarket.dto.categoria.CategoriaResponseDTO;
import com.minimarket.entity.Categoria;
import com.minimarket.hateoas.CategoriaModelAssembler;
import com.minimarket.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Categoria", description = "Operaciones para gestionar Categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    private final CategoriaModelAssembler assembler;

    public CategoriaController(CategoriaService categoriaService, CategoriaModelAssembler categoriaModelAssembler) {
        this.categoriaService = categoriaService;
        this.assembler = categoriaModelAssembler;
    }

    @Operation(summary = "Listar Categorias", description = "Obtiene una lista con todas las Categorias")
    @ApiResponse(responseCode = "200", description = "Listado obtenido de forma correcta")
    @GetMapping
    public CollectionModel<EntityModel<CategoriaResponseDTO>> listarCategorias() {
        return assembler.toCollectionModel(categoriaService.findAll());
    }

    @Operation(summary = "Retorna Categoria por ID", description = "Obtiene una categoria segun su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria obtenida de forma correcta"),
            @ApiResponse(responseCode = "404", description = "Categoria no encontrada")
    })
    @GetMapping("/{id}")
    public EntityModel<CategoriaResponseDTO> obtenerCategoriaPorId(@PathVariable Long id) {
        return assembler.toModel(categoriaService.findById(id));
    }

    @Operation(summary = "Registrar Categoria", description = "Registra una nueva Categoria en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categoria creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos")
    })
    @PostMapping
    public ResponseEntity<EntityModel<CategoriaResponseDTO>> guardarCategoria(@Valid @RequestBody CategoriaRequestDTO request) {
        CategoriaResponseDTO creada = categoriaService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(creada));
    }

    @Operation(summary = "Actualizar Categoria", description = "Actualiza una Categoria en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "404", description = "Categoria no encontrada"),
    })
    @PutMapping("/{id}")
    public EntityModel<CategoriaResponseDTO> actualizarCategoria(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaRequestDTO request) {
        return assembler.toModel(categoriaService.actualizar(id, request));
    }

    @Operation(summary = "Elimina Categoria por ID", description = "Elimina una categoria segun su ID solo si esta no tiene un producto relacionado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Categoria eliminada de forma correcta"),
            @ApiResponse(responseCode = "404", description = "Categoria no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Long id) {
        categoriaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}