package com.minimarket.service;

import com.minimarket.dto.usuario.UsuarioRequestDTO;
import com.minimarket.dto.usuario.UsuarioResponseDTO;
import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.CarritoRepository;
import com.minimarket.repository.RolRepository;
import com.minimarket.repository.UsuarioRepository;
import com.minimarket.repository.VentaRepository;
import com.minimarket.service.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private CarritoRepository carritoRepository;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuario;
    private Rol rolCliente;

    @BeforeEach
    public void setUp() {
        rolCliente = new Rol();
        rolCliente.setId(1L);
        rolCliente.setNombre("CLIENTE");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("jperez");
        usuario.setPassword("hashedPassword123");
        usuario.setRoles(Set.of(rolCliente));
    }

    @Test
    public void findAll_debeRetornarListaDeUsuarios(){
        //Arrange
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        //Act
        List<UsuarioResponseDTO> respuesta = usuarioService.findAll();

        //Assert
        assertNotNull(respuesta);
        assertEquals(1, respuesta.size());
        assertEquals("jperez", respuesta.get(0).username());
    }


}