package com.minimarket.service;

import com.minimarket.dto.inventario.InventarioRequestDTO;
import com.minimarket.dto.inventario.InventarioResponseDTO;
import com.minimarket.entity.Inventario;

import java.util.List;

public interface InventarioService {
    List<InventarioResponseDTO> findAll();
    InventarioResponseDTO findById(Long id);
    InventarioResponseDTO registrarMovimiento(InventarioRequestDTO inventarioRequestDTO);
    void deleteById(Long id);
    List<InventarioResponseDTO> findByProductoId(Long productoId);
}
