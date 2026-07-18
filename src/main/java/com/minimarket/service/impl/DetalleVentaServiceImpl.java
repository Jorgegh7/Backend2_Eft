package com.minimarket.service.impl;

import com.minimarket.dto.detalleVenta.DetalleVentaRequestDTO;
import com.minimarket.dto.detalleVenta.DetalleVentaResponseDTO;
import com.minimarket.dto.inventario.InventarioRequestDTO;
import com.minimarket.entity.DetalleVenta;
import com.minimarket.entity.Producto;
import com.minimarket.entity.TipoMovimiento;
import com.minimarket.entity.Venta;
import com.minimarket.repository.DetalleVentaRepository;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.repository.VentaRepository;
import com.minimarket.service.DetalleVentaService;
import com.minimarket.service.InventarioService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DetalleVentaServiceImpl implements DetalleVentaService {

    private final DetalleVentaRepository detalleVentaRepository;
    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final InventarioService inventarioService;

    public DetalleVentaServiceImpl(DetalleVentaRepository detalleVentaRepository,
                                   VentaRepository ventaRepository,
                                   ProductoRepository productoRepository,
                                   InventarioService inventarioService) {
        this.detalleVentaRepository = detalleVentaRepository;
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
        this.inventarioService = inventarioService;
    }

    @Override
    public List<DetalleVentaResponseDTO> findAll() {
        return detalleVentaRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    public DetalleVentaResponseDTO findById(Long id) {
        DetalleVenta detalleVenta = detalleVentaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DetalleVenta no encontrado"));
        return toResponseDTO(detalleVenta);
    }

    @Override
    public DetalleVentaResponseDTO agregarDetalle(DetalleVentaRequestDTO request) {
        Venta venta = ventaRepository.findById(request.ventaId())
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        Producto producto = productoRepository.findById(request.productoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Registra la salida de inventario correspondiente a este detalle.
        inventarioService.registrarMovimiento(
                new InventarioRequestDTO(request.productoId(), request.cantidad(), TipoMovimiento.SALIDA));

        DetalleVenta detalleVenta = new DetalleVenta();
        detalleVenta.setVenta(venta);
        detalleVenta.setProducto(producto);
        detalleVenta.setCantidad(request.cantidad());
        detalleVenta.setPrecio(producto.getPrecio());

        DetalleVenta detalleCreado = detalleVentaRepository.save(detalleVenta);
        return toResponseDTO(detalleCreado);
    }

    @Override
    public DetalleVentaResponseDTO actualizar(Long id, DetalleVentaRequestDTO request) {
        DetalleVenta detalleVenta = detalleVentaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DetalleVenta no encontrado"));

        // Decision de diseno: no se permite modificar cantidad ni producto,
        // ya que afectan el inventario ya descontado al crear el detalle.
        // Solo se permite reasignar el detalle a otra Venta existente.
        Venta venta = ventaRepository.findById(request.ventaId())
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        detalleVenta.setVenta(venta);

        DetalleVenta detalleActualizado = detalleVentaRepository.save(detalleVenta);
        return toResponseDTO(detalleActualizado);
    }

    @Override
    public void deleteById(Long id) {
        if (detalleVentaRepository.findById(id).isEmpty()) {
            throw new RuntimeException("DetalleVenta no encontrado");
        }
        detalleVentaRepository.deleteById(id);
    }

    @Override
    public List<DetalleVentaResponseDTO> findByVentaId(Long ventaId) {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        List<DetalleVenta> detalleVentas = detalleVentaRepository.findByVentaId(venta.getId());
        return detalleVentas.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private DetalleVentaResponseDTO toResponseDTO(DetalleVenta detalleVenta) {
        return new DetalleVentaResponseDTO(
                detalleVenta.getId(),
                detalleVenta.getProducto().getId(),
                detalleVenta.getProducto().getNombre(),
                detalleVenta.getCantidad(),
                detalleVenta.getPrecio());
    }
}