package com.minimarket.service;

import com.minimarket.dto.producto.ProductoRequestDTO;
import com.minimarket.dto.producto.ProductoResponseDTO;

import java.util.List;

public interface ProductoService {
    List<ProductoResponseDTO> findAll();
    ProductoResponseDTO findById(Long id);

    ProductoResponseDTO crear(ProductoRequestDTO productoRequestDTO);
    ProductoResponseDTO actualizar(Long id, ProductoRequestDTO productoRequestDTO);
    void deleteById(Long id);
    List<ProductoResponseDTO> findByCategoriaId(Long id);
}
