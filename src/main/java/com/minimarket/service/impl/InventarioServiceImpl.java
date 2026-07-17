package com.minimarket.service.impl;

import com.minimarket.dto.inventario.InventarioRequestDTO;
import com.minimarket.dto.inventario.InventarioResponseDTO;
import com.minimarket.entity.Inventario;
import com.minimarket.entity.Producto;
import com.minimarket.entity.TipoMovimiento;
import com.minimarket.repository.InventarioRepository;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.service.InventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventarioServiceImpl implements InventarioService {

    private final InventarioRepository inventarioRepository;
    private final ProductoRepository productoRepository;

    public InventarioServiceImpl(InventarioRepository inventarioRepository, ProductoRepository productoRepository) {
        this.inventarioRepository = inventarioRepository;
        this.productoRepository = productoRepository;
    }


    @Override
    public List<InventarioResponseDTO> findAll() {
        List<Inventario> inventarios = inventarioRepository.findAll();
        return inventarios.stream().map(inventario -> {
            return new InventarioResponseDTO(
                    inventario.getId(),
                    inventario.getProducto().getId(),
                    inventario.getProducto().getNombre(),
                    inventario.getCantidad(),
                    inventario.getTipoMovimiento(),
                    inventario.getFechaMovimiento());
        }).toList();
    }

    @Override
    public InventarioResponseDTO findById(Long id) {
        Inventario inventario = inventarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Inventario no encontrado"));
        return new InventarioResponseDTO(
                inventario.getId(),
                inventario.getProducto().getId(),
                inventario.getProducto().getNombre(),
                inventario.getCantidad(),
                inventario.getTipoMovimiento(),
                inventario.getFechaMovimiento());
    }

    @Override
    public InventarioResponseDTO registrarMovimiento(InventarioRequestDTO inventarioRequestDTO) {
        Producto producto = productoRepository.findById(inventarioRequestDTO.productoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (inventarioRequestDTO.tipoMovimiento() == TipoMovimiento.ENTRADA) {
            producto.setStock(producto.getStock() + inventarioRequestDTO.cantidad());
        } else {
            if (producto.getStock() < inventarioRequestDTO.cantidad()) {
                throw new RuntimeException("Stock insuficiente para realizar la salida");
            }
            producto.setStock(producto.getStock() - inventarioRequestDTO.cantidad());
        }
        productoRepository.save(producto); //Guarda el cambio en el stock

        Inventario inventario = new Inventario();
        inventario.setProducto(producto);
        inventario.setCantidad(inventarioRequestDTO.cantidad());
        inventario.setTipoMovimiento(inventarioRequestDTO.tipoMovimiento());
        inventario.setFechaMovimiento(LocalDateTime.now());

        Inventario creado = inventarioRepository.save(inventario);
        return new InventarioResponseDTO(
                creado.getId(),
                creado.getProducto().getId(),
                creado.getProducto().getNombre(),
                creado.getCantidad(),
                creado.getTipoMovimiento(),
                creado.getFechaMovimiento());
    }

    @Override
    public void deleteById(Long id) {
        Inventario inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventario no encontrado"));
        inventarioRepository.delete(inventario);
    }

    @Override
    public List<InventarioResponseDTO> findByProductoId(Long productoId) {
        if(productoRepository.findById(productoId).isEmpty()){
            throw new RuntimeException("Producto no encontrado");
        }
        List<Inventario> inventarios = inventarioRepository.findByProductoId(productoId);
        return inventarios.stream().map(inventario ->
                new InventarioResponseDTO(
                        inventario.getId(),
                        inventario.getProducto().getId(),
                        inventario.getProducto().getNombre(),
                        inventario.getCantidad(),
                        inventario.getTipoMovimiento(),
                        inventario.getFechaMovimiento())
        ).toList();

    }
}
