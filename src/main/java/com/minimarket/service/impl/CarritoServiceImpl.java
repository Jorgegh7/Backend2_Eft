package com.minimarket.service.impl;

import com.minimarket.dto.carrito.CarritoRequestDTO;
import com.minimarket.dto.carrito.CarritoResponseDTO;
import com.minimarket.entity.Carrito;
import com.minimarket.repository.CarritoRepository;
import com.minimarket.service.CarritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarritoServiceImpl implements CarritoService {

    private final CarritoRepository carritoRepository;

    public CarritoServiceImpl(CarritoRepository carritoRepository) {
        this.carritoRepository = carritoRepository;
    }

    @Override
    public List<CarritoResponseDTO> findAll() {
        List<Carrito> carritos = carritoRepository.findAll();
        return carritos.stream().map(carrito ->
                new CarritoResponseDTO(
                        carrito.getId(),
                        carrito.getUsuario().getId(),
                        carrito.getUsuario().getUsername(),
                        carrito.getProducto().getId(),
                        carrito.getProducto().getNombre(),
                        carrito.getCantidad())).toList();
    }

    @Override
    public CarritoResponseDTO findById(Long id) {
        Carrito carrito = carritoRepository.findById(id).orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
        return new CarritoResponseDTO(
                carrito.getId(),
                carrito.getUsuario().getId(),
                carrito.getUsuario().getUsername(),
                carrito.getProducto().getId(),
                carrito.getProducto().getNombre(),
                carrito.getCantidad());
    }

    @Override
    public CarritoResponseDTO agregarProducto(CarritoRequestDTO request) {
        return null;
    }

    @Override
    public CarritoResponseDTO actualizar(Long id, CarritoRequestDTO request) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public List<CarritoResponseDTO> findByUsuarioId(Long usuarioId) {
        return List.of();
    }
}
