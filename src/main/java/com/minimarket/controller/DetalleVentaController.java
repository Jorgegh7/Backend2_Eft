package com.minimarket.controller;

import com.minimarket.dto.detalleVenta.DetalleVentaRequestDTO;
import com.minimarket.dto.detalleVenta.DetalleVentaResponseDTO;
import com.minimarket.entity.DetalleVenta;
import com.minimarket.hateoas.DetalleVentaModelAssembler;
import com.minimarket.service.DetalleVentaService;
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

@RestController
@RequestMapping("/api/detalle-ventas")
@Tag(name = "Detalle-Ventas", description = "Operaciones para gestionar Detalle-Ventas")
public class DetalleVentaController {

    private final DetalleVentaService detalleVentaService;
    private final DetalleVentaModelAssembler assembler;

    public DetalleVentaController(DetalleVentaService detalleVentaService, DetalleVentaModelAssembler assembler) {
        this.detalleVentaService = detalleVentaService;
        this.assembler = assembler;
    }

    @Operation(summary = "Listar DetalleVenta", description = "Obtiene una lista con todas los DetalleVenta")
    @ApiResponse(responseCode = "200", description = "Listado obtenido de forma correcta")
    @GetMapping
    public CollectionModel<EntityModel<DetalleVentaResponseDTO>> listarDetalleVentas() {
        return assembler.toCollectionModel(detalleVentaService.findAll());
    }

    @Operation(summary = "Obtener DetalleVenta por ID", description = "Obtiene DetalleVenta por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "DetalleVenta obtenida de forma correcta"),
            @ApiResponse(responseCode = "404", description = "DetalleVenta no encontrado")
    })
    @GetMapping("/{id}")
    public EntityModel<DetalleVentaResponseDTO> obtenerDetalleVentaPorId(@PathVariable Long id) {
        return assembler.toModel(detalleVentaService.findById(id));
    }

    @Operation(summary = "Obtener DetalleVenta por Venta ID", description = "Obtiene DetalleVenta por Venta ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "DetalleVenta obtenida de forma correcta"),
            @ApiResponse(responseCode = "404", description = "DetalleVenta no encontrado")
    })
    @GetMapping("/venta/{ventaId}")
    public CollectionModel<EntityModel<DetalleVentaResponseDTO>> listarPorVenta(@PathVariable Long ventaId){
        return assembler.toCollectionModel(detalleVentaService.findByVentaId(ventaId));
    }

    @Operation(summary = "Registrar DetalleVenta", description = "Registra un nuevo DetalleVenta en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "DetalleVenta creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos")
    })
    @PostMapping
    public ResponseEntity<EntityModel<DetalleVentaResponseDTO>> agregarDetalle(
            @Valid @RequestBody DetalleVentaRequestDTO detalleVentaRequestDTO) {
        DetalleVentaResponseDTO creado = detalleVentaService.agregarDetalle(detalleVentaRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(creado));
    }

    @Operation(summary = "Actualizar DetalleVenta por ID", description = "Actualizar un DetalleVenta en el sistema por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "DetalleVenta actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "404", description = "DetalleVenta no encontrado")
    })
    @PutMapping("/{id}")
    public EntityModel<DetalleVentaResponseDTO> actualizarDetalleVenta(
            @PathVariable Long id, @Valid @RequestBody DetalleVentaRequestDTO detalleVentaRequestDTO) {
        return assembler.toModel(detalleVentaService.actualizar(id, detalleVentaRequestDTO));
    }

    @Operation(summary = "Eliminar DetalleVenta", description = "Elimina un DetalleVenta por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "DetalleVenta eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "DetalleVenta no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDetalleVenta(@PathVariable Long id) {
        detalleVentaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
