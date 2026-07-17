package com.minimarket.service.impl;

import com.minimarket.dto.carrito.CarritoRequestDTO;
import com.minimarket.dto.carrito.CarritoResponseDTO;
import com.minimarket.entity.Carrito;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.CarritoRepository;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.repository.UsuarioRepository;
import com.minimarket.service.CarritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarritoServiceImpl implements CarritoService {

    private final CarritoRepository carritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    public CarritoServiceImpl(CarritoRepository carritoRepository, UsuarioRepository usuarioRepository, ProductoRepository productoRepository) {
        this.carritoRepository = carritoRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
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
        Carrito carrito = new Carrito();
        Usuario usuario = usuarioRepository.findById(request.usuarioId()).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Producto producto = productoRepository.findById(request.productoId()).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        carrito.setUsuario(usuario);
        carrito.setProducto(producto);
        carrito.setCantidad(request.cantidad());

        Carrito creado = carritoRepository.save(carrito);
        return new CarritoResponseDTO(
                creado.getId(),
                creado.getUsuario().getId(),
                creado.getUsuario().getUsername(),
                creado.getProducto().getId(),
                creado.getProducto().getNombre(),
                creado.getCantidad());
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
