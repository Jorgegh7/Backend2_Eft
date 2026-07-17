package com.minimarket.service;

import com.minimarket.dto.carrito.CarritoRequestDTO;
import com.minimarket.dto.carrito.CarritoResponseDTO;

import java.util.List;

public interface CarritoService {
    List<CarritoResponseDTO> findAll();
    CarritoResponseDTO findById(Long id);
    CarritoResponseDTO agregarProducto(CarritoRequestDTO request);
    CarritoResponseDTO actualizar(Long id, CarritoRequestDTO request);
    void deleteById(Long id);
    List<CarritoResponseDTO> findByUsuarioId(Long usuarioId);
}