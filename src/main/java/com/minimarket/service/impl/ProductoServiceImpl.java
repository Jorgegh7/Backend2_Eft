package com.minimarket.service.impl;

import com.minimarket.dto.producto.ProductoRequestDTO;
import com.minimarket.dto.producto.ProductoResponseDTO;
import com.minimarket.entity.Categoria;
import com.minimarket.entity.Producto;
import com.minimarket.repository.*;
import com.minimarket.service.InventarioService;
import com.minimarket.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    private final CategoriaRepository categoriaRepository;

    private final DetalleVentaRepository detalleVentaRepository;

    private final InventarioRepository inventarioRepository;

    private final CarritoRepository carritoRepository;

    public ProductoServiceImpl(
            ProductoRepository productoRepository,
            CategoriaRepository categoriaRepository,
            DetalleVentaRepository detalleVentaRepository,
            InventarioRepository inventarioRepository,
            CarritoRepository carritoRepository

    ){
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.inventarioRepository = inventarioRepository;
        this.carritoRepository = carritoRepository;
    }

    @Override
    public List<ProductoResponseDTO> findAll() {
        List<Producto> productos = productoRepository.findAll();
        return productos.stream().map(producto -> {
            return new ProductoResponseDTO(
                    producto.getId(),
                    producto.getNombre(),
                    producto.getPrecio(),
                    producto.getStock(),
                    producto.getCategoria().getId(),
                    producto.getCategoria().getNombre());

        }).toList();

    }

    @Override
    public ProductoResponseDTO findById(Long id) {
        Producto producto = productoRepository.findById(id).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        return new ProductoResponseDTO(producto.getId(),
                producto.getNombre(),
                producto.getPrecio(),
                producto.getStock(),
                producto.getCategoria().getId(),
                producto.getCategoria().getNombre());
    }

    @Override
    public ProductoResponseDTO crear(ProductoRequestDTO productoRequestDTO) {
        Categoria categoria = categoriaRepository.findById(productoRequestDTO.categoriaId())
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada"));

        Producto producto = new Producto();
        producto.setCategoria(categoria);
        producto.setNombre(productoRequestDTO.nombre());
        producto.setPrecio(productoRequestDTO.precio());
        producto.setStock(0);
        Producto productoGuardado = productoRepository.save(producto);

        return new ProductoResponseDTO(
                productoGuardado.getId(),
                productoGuardado.getNombre(),
                productoGuardado.getPrecio(),
                productoGuardado.getStock(),
                productoGuardado.getCategoria().getId(),
                productoGuardado.getCategoria().getNombre());
    }

    @Override
    public ProductoResponseDTO actualizar(Long id, ProductoRequestDTO productoRequestDTO) {
        Producto producto = productoRepository.findById(id).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        Categoria categoria = categoriaRepository.findById(productoRequestDTO.categoriaId())
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada"));
        producto.setNombre(productoRequestDTO.nombre());
        producto.setPrecio(productoRequestDTO.precio());
        producto.setCategoria(categoria);

        Producto actualizado = productoRepository.save(producto);

        return new ProductoResponseDTO(
                actualizado.getId(),
                actualizado.getNombre(),
                actualizado.getPrecio(),
                actualizado.getStock(),
                actualizado.getCategoria().getId(),
                actualizado.getCategoria().getNombre());
    }

    @Override
    public void deleteById(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (!detalleVentaRepository.findByProductoId(id).isEmpty()) {
            throw new RuntimeException("No se puede eliminar: el producto tiene ventas asociadas");
        }

        if (!inventarioRepository.findByProductoId(id).isEmpty()) {
            throw new RuntimeException("No se puede eliminar: el producto tiene movimientos de inventario asociados");
        }

        if (!carritoRepository.findByProductoId(id).isEmpty()) {
            throw new RuntimeException("No se puede eliminar: el producto esta en carritos activos");
        }

        productoRepository.delete(producto);
    }

    @Override
    public List<ProductoRequestDTO> findByCategoriaId(Long categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId);
    }
}
