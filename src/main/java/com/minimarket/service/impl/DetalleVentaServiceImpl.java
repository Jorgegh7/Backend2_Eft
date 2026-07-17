package com.minimarket.service.impl;

import com.minimarket.dto.detalleVenta.DetalleVentaRequestDTO;
import com.minimarket.dto.detalleVenta.DetalleVentaResponseDTO;
import com.minimarket.entity.DetalleVenta;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Venta;
import com.minimarket.repository.DetalleVentaRepository;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.repository.VentaRepository;
import com.minimarket.service.DetalleVentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DetalleVentaServiceImpl implements DetalleVentaService {

    private final DetalleVentaRepository detalleVentaRepository;
    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;

    public DetalleVentaServiceImpl(DetalleVentaRepository detalleVentaRepository, VentaRepository ventaRepository, ProductoRepository productoRepository) {
        this.detalleVentaRepository = detalleVentaRepository;
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
    }




    @Override
    public List<DetalleVentaResponseDTO> findAll() {
        List<DetalleVenta> detalleVentas = detalleVentaRepository.findAll();
        return detalleVentas.stream().map(detalleVenta ->
                new DetalleVentaResponseDTO(
                        detalleVenta.getId(),
                        detalleVenta.getProducto().getId(),
                        detalleVenta.getProducto().getNombre(),
                        detalleVenta.getCantidad(),
                        detalleVenta.getPrecio())).toList();
    }

    @Override
    public DetalleVentaResponseDTO findById(Long id) {
        DetalleVenta detalleVenta = detalleVentaRepository
                .findById(id).orElseThrow(() -> new RuntimeException("DetalleVenta no encontrado"));
        return new DetalleVentaResponseDTO(
                detalleVenta.getId(),
                detalleVenta.getProducto().getId(),
                detalleVenta.getProducto().getNombre(),
                detalleVenta.getCantidad(),
                detalleVenta.getPrecio());
    }

    @Override
    public void deleteById(Long id) {
        if(detalleVentaRepository.findById(id).isEmpty()){
            throw new RuntimeException("DetalleVenta no encontrado");
        }
        detalleVentaRepository.deleteById(id);
    }

    @Override
    public List<DetalleVentaResponseDTO> findByVentaId(Long ventaId) {
        Venta venta = ventaRepository.findById(ventaId).orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        List<DetalleVenta> detalleVentas = detalleVentaRepository.findByVentaId(venta.getId());
        return detalleVentas.stream().map(detalleVenta ->
                new DetalleVentaResponseDTO(
                        detalleVenta.getId(),
                        detalleVenta.getProducto().getId(),
                        detalleVenta.getProducto().getNombre(),
                        detalleVenta.getCantidad(),
                        detalleVenta.getPrecio())).toList();
    }

    @Override
    public DetalleVentaResponseDTO actualizar(Long id, DetalleVentaRequestDTO request) {
        DetalleVenta detalleVenta = detalleVentaRepository.findById(id).orElseThrow(() -> new RuntimeException("DetalleVenta no encontrado"));
        Venta venta = ventaRepository.findById(request.ventaId()).orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        Producto producto = productoRepository.findById(request.productoId()).orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        detalleVenta.setVenta(venta);
        detalleVenta.setProducto(producto);
        detalleVenta.setCantidad(request.cantidad());
        detalleVenta.setPrecio(producto.getPrecio());

        DetalleVenta detalleCreado = detalleVentaRepository.save(detalleVenta);
        return new DetalleVentaResponseDTO(
                detalleCreado.getId(),
                detalleCreado.getProducto().getId(),
                detalleCreado.getProducto().getNombre(),
                detalleCreado.getCantidad(),
                detalleCreado.getPrecio());
    }

    @Override
    public DetalleVentaResponseDTO agregarDetalle(DetalleVentaRequestDTO request) {
        Venta venta = ventaRepository.findById(request.ventaId()).orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        Producto producto = productoRepository.findById(request.productoId()).orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        DetalleVenta detalleVenta = new DetalleVenta();
        detalleVenta.setVenta(venta);
        detalleVenta.setProducto(producto);
        detalleVenta.setCantidad(request.cantidad());
        detalleVenta.setPrecio(producto.getPrecio());

        DetalleVenta detalleCreado = detalleVentaRepository.save(detalleVenta);
        return new DetalleVentaResponseDTO(
                detalleCreado.getId(),
                detalleCreado.getProducto().getId(),
                detalleCreado.getProducto().getNombre(),
                detalleCreado.getCantidad(),
                detalleCreado.getPrecio());
    }
}
