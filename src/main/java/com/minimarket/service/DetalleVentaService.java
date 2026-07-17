package com.minimarket.service;

import com.minimarket.dto.detalleVenta.DetalleVentaRequestDTO;
import com.minimarket.dto.detalleVenta.DetalleVentaResponseDTO;

import java.util.List;

public interface DetalleVentaService {
    List<DetalleVentaResponseDTO> findAll();
    DetalleVentaResponseDTO findById(Long id);
    DetalleVentaResponseDTO agregarDetalle(DetalleVentaRequestDTO request);
    DetalleVentaResponseDTO actualizar(Long id, DetalleVentaRequestDTO request);
    void deleteById(Long id);
    List<DetalleVentaResponseDTO> findByVentaId(Long ventaId);
}