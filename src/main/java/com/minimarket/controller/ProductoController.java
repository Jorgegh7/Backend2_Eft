package com.minimarket.controller;

import com.minimarket.dto.producto.ProductoRequestDTO;
import com.minimarket.dto.producto.ProductoResponseDTO;
import com.minimarket.hateoas.ProductoModelAssembler;
import com.minimarket.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "Operaciones para gestionar Productos")
public class ProductoController {

    private final ProductoService productoService;
    private final ProductoModelAssembler assembler;

    public ProductoController(ProductoService productoService, ProductoModelAssembler assembler) {
        this.productoService = productoService;
        this.assembler = assembler;
    }

    @Operation(summary = "Listar Productos", description = "Obtiene una lista con todos los Productos")
    @ApiResponse(responseCode = "200", description = "Listado obtenido de forma correcta")
    @GetMapping
    public CollectionModel<EntityModel<ProductoResponseDTO>> listarProductos() {
        return assembler.toCollectionModel(productoService.findAll());
    }

    @Operation(summary = "Retorna Producto por ID", description = "Obtiene un producto segun su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto obtenido de forma correcta"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/{id}")
    public EntityModel<ProductoResponseDTO> obtenerProductoPorId(
            @Parameter(description = "ID Producto", example = "1")
            @PathVariable Long id) {
        return assembler.toModel(productoService.findById(id));
    }

    @Operation(summary = "Listar Productos por Categoria", description = "Obtiene los productos asociados a una categoria")
    @ApiResponse(responseCode = "200", description = "Listado obtenido de forma correcta")
    @GetMapping("/categoria/{categoriaId}")
    public CollectionModel<EntityModel<ProductoResponseDTO>> listarPorCategoria(
            @Parameter(description = "ID Categoria", example = "1")
            @PathVariable Long categoriaId) {
        return assembler.toCollectionModel(productoService.findByCategoriaId(categoriaId));
    }

    @Operation(summary = "Registrar Producto", description = "Registra un nuevo Producto en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos")
    })
    @PostMapping
    public ResponseEntity<EntityModel<ProductoResponseDTO>> guardarProducto(
            @Valid @RequestBody ProductoRequestDTO request) {
        ProductoResponseDTO creado = productoService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(creado));
    }

    @Operation(summary = "Actualizar Producto", description = "Actualiza un Producto accediendo a este por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PutMapping("/{id}")
    public EntityModel<ProductoResponseDTO> actualizarProducto(
            @Parameter(description = "ID Producto", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequestDTO request) {
        return assembler.toModel(productoService.actualizar(id, request));
    }

    @Operation(summary = "Eliminar Producto", description = "Elimina un Producto por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}