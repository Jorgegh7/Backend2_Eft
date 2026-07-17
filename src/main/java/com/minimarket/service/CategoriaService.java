package com.minimarket.service;

import com.minimarket.dto.categoria.CategoriaRequestDTO;
import com.minimarket.dto.categoria.CategoriaResponseDTO;
import com.minimarket.entity.Categoria;

import java.util.List;

public interface CategoriaService {
    List<CategoriaResponseDTO> findAll();
    CategoriaResponseDTO findById(Long id);
    CategoriaResponseDTO crear(CategoriaRequestDTO request);
    CategoriaResponseDTO actualizar(Long id, CategoriaRequestDTO request);
    void deleteById(Long id);
}