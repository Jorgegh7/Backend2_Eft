package com.minimarket.controller;

import com.minimarket.dto.carrito.CarritoRequestDTO;
import com.minimarket.dto.carrito.CarritoResponseDTO;
import com.minimarket.hateoas.CarritoModelAssembler;
import com.minimarket.service.CarritoService;
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
@RequestMapping("/api/carrito")
@Tag(name = "Carritos", description = "Operaciones para gestionar Carritos")
public class CarritoController {

    private final CarritoService carritoService;
    private final CarritoModelAssembler assembler;

    public CarritoController(CarritoService carritoService, CarritoModelAssembler assembler) {
        this.carritoService = carritoService;
        this.assembler = assembler;
    }

    @Operation(summary = "Listar Carritos", description = "Obtiene una lista con todos los Carritos")
    @ApiResponse(responseCode = "200", description = "Listado obtenido de forma correcta")
    @GetMapping
    public CollectionModel<EntityModel<CarritoResponseDTO>> listarCarrito() {
        return assembler.toCollectionModel(carritoService.findAll());
    }

    @Operation(summary = "Retorna Carrito por ID", description = "Obtiene un carrito segun su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carrito obtenido de forma correcta"),
            @ApiResponse(responseCode = "404", description = "Carrito no encontrado")
    })
    @GetMapping("/{id}")
    public EntityModel<CarritoResponseDTO> obtenerCarritoPorId(
            @Parameter(description = "ID Carrito", example = "1")
            @PathVariable Long id) {
        return assembler.toModel(carritoService.findById(id));
    }

    @Operation(summary = "Listar Carritos por Usuario", description = "Obtiene los carritos asociados a un usuario")
    @ApiResponse(responseCode = "200", description = "Listado obtenido de forma correcta")
    @GetMapping("/usuario/{usuarioId}")
    public CollectionModel<EntityModel<CarritoResponseDTO>> listarPorUsuario(
            @Parameter(description = "ID Usuario", example = "1")
            @PathVariable Long usuarioId) {
        return assembler.toCollectionModel(carritoService.findByUsuarioId(usuarioId));
    }

    @Operation(summary = "Registrar Carrito", description = "Agrega un producto al carrito de un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Carrito creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos")
    })
    @PostMapping
    public ResponseEntity<EntityModel<CarritoResponseDTO>> agregarProductoAlCarrito(
            @Valid @RequestBody CarritoRequestDTO request) {
        CarritoResponseDTO creado = carritoService.agregarProducto(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(creado));
    }

    @Operation(summary = "Actualizar Carrito", description = "Actualiza un Carrito accediendo a este por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carrito actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Carrito no encontrado")
    })
    @PutMapping("/{id}")
    public EntityModel<CarritoResponseDTO> actualizarCarrito(
            @Parameter(description = "ID Carrito", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody CarritoRequestDTO request) {
        return assembler.toModel(carritoService.actualizar(id, request));
    }

    @Operation(summary = "Eliminar Carrito", description = "Elimina un Carrito por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Carrito eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Carrito no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProductoDelCarrito(@PathVariable Long id) {
        carritoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}