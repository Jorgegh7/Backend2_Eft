package com.minimarket.service.impl;

import com.minimarket.dto.producto.ProductoRequestDTO;
import com.minimarket.dto.producto.ProductoResponseDTO;
import com.minimarket.entity.Producto;
import com.minimarket.repository.CategoriaRepository;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    private final CategoriaRepository categoriaRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository, CategoriaRepository categoriaRepository){
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
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
        return null;
    }

    @Override
    public ProductoResponseDTO actualizar(Long id, ProductoRequestDTO productoRequestDTO) {
        return null;
    }

    @Override
    public void deleteById(Long id) {
        productoRepository.deleteById(id);
    }

    @Override
    public List<ProductoRequestDTO> findByCategoriaId(Long categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId);
    }
}
