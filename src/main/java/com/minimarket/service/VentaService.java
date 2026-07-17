package com.minimarket.service;

import com.minimarket.dto.venta.VentaRequestDTO;
import com.minimarket.dto.venta.VentaResponseDTO;

import java.util.List;

public interface VentaService {
    List<VentaResponseDTO> findAll();
    VentaResponseDTO findById(Long id);
    VentaResponseDTO crear(VentaRequestDTO request);
    List<VentaResponseDTO> findByUsuarioId(Long usuarioId);
}