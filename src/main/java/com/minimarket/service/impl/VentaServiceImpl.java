package com.minimarket.service.impl;

import com.minimarket.dto.detalleVenta.DetalleVentaResponseDTO;
import com.minimarket.dto.venta.DetalleVentaItemDTO;
import com.minimarket.dto.venta.VentaRequestDTO;
import com.minimarket.dto.venta.VentaResponseDTO;
import com.minimarket.entity.DetalleVenta;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Usuario;
import com.minimarket.entity.Venta;
import com.minimarket.repository.DetalleVentaRepository;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.repository.UsuarioRepository;
import com.minimarket.repository.VentaRepository;
import com.minimarket.service.VentaService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final DetalleVentaRepository detalleVentaRepository;

    public VentaServiceImpl(VentaRepository ventaRepository,
                            UsuarioRepository usuarioRepository,
                            ProductoRepository productoRepository,
                            DetalleVentaRepository detalleVentaRepository) {
        this.ventaRepository = ventaRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.detalleVentaRepository = detalleVentaRepository;
    }

    @Override
    public List<VentaResponseDTO> findAll() {
        return ventaRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    public VentaResponseDTO findById(Long id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
        return toResponseDTO(venta);
    }

    @Override
    public VentaResponseDTO crear(VentaRequestDTO request) {
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Venta venta = new Venta();
        venta.setUsuario(usuario);
        venta.setFecha(LocalDateTime.now());
        Venta ventaGuardada = ventaRepository.save(venta);

        List<DetalleVenta> detallesCreados = new ArrayList<>();

        for (DetalleVentaItemDTO item : request.detalles()) {
            Producto producto = productoRepository.findById(item.productoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + item.productoId()));

            if (producto.getStock() < item.cantidad()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
            }

            producto.setStock(producto.getStock() - item.cantidad());
            productoRepository.save(producto);

            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(ventaGuardada);
            detalle.setProducto(producto);
            detalle.setCantidad(item.cantidad());
            detalle.setPrecio(producto.getPrecio());

            detallesCreados.add(detalleVentaRepository.save(detalle));
        }

        ventaGuardada.setDetalles(detallesCreados);

        return toResponseDTO(ventaGuardada);
    }

    @Override
    public List<VentaResponseDTO> findByUsuarioId(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new RuntimeException("Usuario no encontrado");
        }

        return ventaRepository.findByUsuarioId(usuarioId).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private VentaResponseDTO toResponseDTO(Venta venta) {
        List<DetalleVentaResponseDTO> detallesDTO = venta.getDetalles().stream()
                .map(detalle -> new DetalleVentaResponseDTO(
                        detalle.getId(),
                        detalle.getProducto().getId(),
                        detalle.getProducto().getNombre(),
                        detalle.getCantidad(),
                        detalle.getPrecio()))
                .toList();

        Double total = venta.getDetalles().stream()
                .mapToDouble(d -> d.getCantidad() * d.getPrecio())
                .sum();

        return new VentaResponseDTO(
                venta.getId(),
                venta.getUsuario().getId(),
                venta.getUsuario().getUsername(),
                venta.getFecha(),
                total,
                detallesDTO);
    }
}