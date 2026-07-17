package com.minimarket.controller;

import com.minimarket.dto.inventario.InventarioRequestDTO;
import com.minimarket.dto.inventario.InventarioResponseDTO;
import com.minimarket.hateoas.InventarioModelAssembler;
import com.minimarket.service.InventarioService;
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
@RequestMapping("/api/inventario")
@Tag(name = "Inventario", description = "Operaciones para gestionar Inventario")
public class InventarioController {

    private final InventarioService inventarioService;
    private final InventarioModelAssembler assembler;

    public InventarioController(InventarioService inventarioService, InventarioModelAssembler assembler) {
        this.inventarioService = inventarioService;
        this.assembler = assembler;
    }

    @Operation(summary = "Listar Inventarios", description = "Obtiene una lista con todos los Inventarios")
    @ApiResponse(responseCode = "200", description = "Listado obtenido de forma correcta")
    @GetMapping
    public CollectionModel<EntityModel<InventarioResponseDTO>> listarMovimientosDeInventario() {
        return assembler.toCollectionModel(inventarioService.findAll());
    }

    @Operation(summary = "Obtener Inventario por ID", description = "Obtiene un Inventario por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invetario obtenido de forma correcta"),
            @ApiResponse(responseCode = "404", description = "Inventario no encontrado")
    })
    @GetMapping("/{id}")
    public EntityModel<InventarioResponseDTO> obtenerMovimientoPorId(
            @Parameter(description = "ID Inventario", example = "1")
            @PathVariable Long id) {
        return assembler.toModel(inventarioService.findById(id));
    }

    @Operation(summary = "Obtener Inventario por Producto ID", description = "Obtiene Movimientos de Inventario por Producto ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimientos Inventario obtenido de forma correcta"),
            @ApiResponse(responseCode = "404", description = "Inventario no encontrado")
    })
    @GetMapping("/producto/{productoId}")
    public CollectionModel<EntityModel<InventarioResponseDTO>> listarMovimientosPorProducto(
            @Parameter(description = "ID Producto", example = "1")
            @PathVariable Long productoId) {
        return assembler.toCollectionModel(inventarioService.findByProductoId(productoId));
    }

    @Operation(summary = "Registrar Movimiento Inventario", description = "Registra un nuevo Movimiento en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Movimiento creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos")
    })
    @PostMapping
    public ResponseEntity<EntityModel<InventarioResponseDTO>> registrarMovimiento(
            @Valid @RequestBody InventarioRequestDTO inventarioRequestDTO) {
        InventarioResponseDTO creado = inventarioService.registrarMovimiento(inventarioRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(creado));
    }

    @Operation(summary = "Eliminar Movimiento Inventario", description = "Elimina un Movimiento por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Movimiento eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Movimiento inventario no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMovimiento(@PathVariable Long id) {
        inventarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}