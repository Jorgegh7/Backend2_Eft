package com.minimarket.service.impl;

import com.minimarket.dto.inventario.InventarioRequestDTO;
import com.minimarket.dto.inventario.InventarioResponseDTO;
import com.minimarket.entity.Inventario;
import com.minimarket.repository.InventarioRepository;
import com.minimarket.service.InventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventarioServiceImpl implements InventarioService {

    private final InventarioRepository inventarioRepository;

    public InventarioServiceImpl(InventarioRepository inventarioRepository) {
        this.inventarioRepository = inventarioRepository;
    }


    @Override
    public List<InventarioResponseDTO> findAll() {
        List<Inventario> inventarios = inventarioRepository.findAll();
        return inventarios.stream().map(inventario -> {
            return new InventarioResponseDTO(
                    inventario.getId(),
                    inventario.getProducto().getId(),
                    inventario.getProducto().getNombre(),
                    inventario.getCantidad(),
                    inventario.getTipoMovimiento(),
                    inventario.getFechaMovimiento());
        }).toList();
    }

    @Override
    public InventarioResponseDTO findById(Long id) {
        return null;
    }

    @Override
    public InventarioResponseDTO registrarMovimiento(InventarioRequestDTO inventarioRequestDTO) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public List<InventarioResponseDTO> findByProductoId(Long productoId) {
        return List.of();
    }
}
