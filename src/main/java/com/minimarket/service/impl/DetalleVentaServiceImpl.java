package com.minimarket.service.impl;

import com.minimarket.dto.detalleVenta.DetalleVentaRequestDTO;
import com.minimarket.dto.detalleVenta.DetalleVentaResponseDTO;
import com.minimarket.entity.DetalleVenta;
import com.minimarket.repository.DetalleVentaRepository;
import com.minimarket.service.DetalleVentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DetalleVentaServiceImpl implements DetalleVentaService {

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;


    @Override
    public List<DetalleVentaResponseDTO> findAll() {
        return List.of();
    }

    @Override
    public DetalleVentaResponseDTO findById(Long id) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public List<DetalleVentaResponseDTO> findByVentaId(Long ventaId) {
        return List.of();
    }

    @Override
    public DetalleVentaResponseDTO actualizar(Long id, DetalleVentaRequestDTO request) {
        return null;
    }

    @Override
    public DetalleVentaResponseDTO agregarDetalle(DetalleVentaRequestDTO request) {
        return null;
    }
}
