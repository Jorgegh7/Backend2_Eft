package com.minimarket.service;

import com.minimarket.dto.usuario.UsuarioRequestDTO;
import com.minimarket.dto.usuario.UsuarioResponseDTO;
import com.minimarket.entity.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    List<UsuarioResponseDTO> findAll();
    UsuarioResponseDTO findById(Long id);
    UsuarioResponseDTO findByUsername(String username);
    UsuarioResponseDTO actualizar(Long id, UsuarioRequestDTO usuarioRequestDTO);
    void deleteById(Long id);
}