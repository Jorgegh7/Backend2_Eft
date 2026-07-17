package com.minimarket.controller;

import com.minimarket.dto.venta.VentaRequestDTO;
import com.minimarket.dto.venta.VentaResponseDTO;
import com.minimarket.hateoas.VentaModelAssembler;
import com.minimarket.service.VentaService;
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
@RequestMapping("/api/ventas")
@Tag(name = "Ventas", description = "Operaciones para gestionar Ventas")
public class VentaController {

    private final VentaService ventaService;
    private final VentaModelAssembler assembler;

    public VentaController(VentaService ventaService, VentaModelAssembler assembler) {
        this.ventaService = ventaService;
        this.assembler = assembler;
    }

    @Operation(summary = "Listar Ventas", description = "Obtiene una lista con todas las Ventas")
    @ApiResponse(responseCode = "200", description = "Listado obtenido de forma correcta")
    @GetMapping
    public CollectionModel<EntityModel<VentaResponseDTO>> listarVentas() {
        return assembler.toCollectionModel(ventaService.findAll());
    }

    @Operation(summary = "Retorna Venta por ID", description = "Obtiene una venta segun su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Venta obtenida de forma correcta"),
            @ApiResponse(responseCode = "404", description = "Venta no encontrada")
    })
    @GetMapping("/{id}")
    public EntityModel<VentaResponseDTO> obtenerVentaPorId(
            @Parameter(description = "ID Venta", example = "1")
            @PathVariable Long id) {
        return assembler.toModel(ventaService.findById(id));
    }

    @Operation(summary = "Listar Ventas por Usuario", description = "Obtiene las ventas asociadas a un usuario")
    @ApiResponse(responseCode = "200", description = "Listado obtenido de forma correcta")
    @GetMapping("/usuario/{usuarioId}")
    public CollectionModel<EntityModel<VentaResponseDTO>> listarPorUsuario(
            @Parameter(description = "ID Usuario", example = "1")
            @PathVariable Long usuarioId) {
        return assembler.toCollectionModel(ventaService.findByUsuarioId(usuarioId));
    }

    @Operation(summary = "Registrar Venta", description = "Registra una nueva Venta con sus detalles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Venta creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos")
    })
    @PostMapping
    public ResponseEntity<EntityModel<VentaResponseDTO>> registrarVenta(
            @Valid @RequestBody VentaRequestDTO request) {
        VentaResponseDTO creada = ventaService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(creada));
    }
}