package com.minimarket.service.impl;

import com.minimarket.dto.usuario.UsuarioRequestDTO;
import com.minimarket.dto.usuario.UsuarioResponseDTO;
import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.CarritoRepository;
import com.minimarket.repository.RolRepository;
import com.minimarket.repository.UsuarioRepository;
import com.minimarket.repository.VentaRepository;
import com.minimarket.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final VentaRepository ventaRepository;
    private final CarritoRepository carritoRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              RolRepository rolRepository,
                              VentaRepository ventaRepository,
                              CarritoRepository carritoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.ventaRepository = ventaRepository;
        this.carritoRepository = carritoRepository;
    }


    @Override
    public List<UsuarioResponseDTO> findAll() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return usuarios.stream()
                .map(usuario -> new UsuarioResponseDTO(
                        usuario.getId(),
                        usuario.getUsername(),
                        usuario.getRoles().stream().map(Rol::getNombre).collect(Collectors.toSet())


                )).toList();
    }

    @Override
    public UsuarioResponseDTO findById(Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getRoles().stream().map(Rol::getNombre).collect(Collectors.toSet()));
    }

    @Override
    public UsuarioResponseDTO findByUsername(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getRoles().stream().map(Rol::getNombre).collect(Collectors.toSet())
        );
    }

    @Override
    public UsuarioResponseDTO actualizar(Long id, UsuarioRequestDTO usuarioRequestDTO) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Set<Rol> roles = usuarioRequestDTO.roles().stream()
                .map(nombreRol -> rolRepository.findByNombre(nombreRol)
                        .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + nombreRol)))
                .collect(Collectors.toSet());

        usuario.setRoles(roles);
        Usuario actualizado = usuarioRepository.save(usuario);
        return new UsuarioResponseDTO(
                actualizado.getId(),
                actualizado.getUsername(),
                actualizado.getRoles().stream().map(Rol::getNombre).collect(Collectors.toSet()));
    }

    @Override
    public void deleteById(Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!ventaRepository.findByUsuarioId(id).isEmpty()) {
            throw new RuntimeException("No se puede eliminar: el usuario tiene ventas asociadas");
        }

        if (!carritoRepository.findByUsuarioId(id).isEmpty()) {
            throw new RuntimeException("No se puede eliminar: el usuario tiene carritos asociados");
        }

        usuarioRepository.delete(usuario);
    }
}
