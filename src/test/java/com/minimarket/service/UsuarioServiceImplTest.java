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

import java.util.HashSet;
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
        assertTrue(respuesta.get(0).roles().contains("CLIENTE"));
    }

    @Test
    public void findById_cuandoExiste_debeRetornarUsuario(){
        //Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        //Act
        UsuarioResponseDTO respuesta = usuarioService.findById(1L);

        //Assert
        assertNotNull(respuesta);
        assertEquals("jperez", respuesta.username());
        assertTrue(respuesta.roles().contains("CLIENTE"));
    }

    @Test
    public void findById_cuandoNoExiste_debeLanzarExcepcion(){
        //Arrange
        when(usuarioRepository.findById(2L)).thenReturn(Optional.empty());

        //Act & Assert
        assertThrows(RuntimeException.class, ()-> usuarioService.findById(2L));

        verify(usuarioRepository).findById(2L);
    }

    @Test
    public void findByUsername_cuandoExiste_debeRetornarUsuario(){
        //Arrange
        when(usuarioRepository.findByUsername("jperez")).thenReturn(Optional.of(usuario));

        //Act
        UsuarioResponseDTO respuesta = usuarioService.findByUsername("jperez");

        //Assert
        assertNotNull(respuesta);
        assertEquals("jperez", respuesta.username());
        assertTrue(respuesta.roles().contains("CLIENTE"));
    }

    @Test
    public void findByUsername_cuandoNoExiste_debeLanzarExcepcion(){
        //Arrange
        when(usuarioRepository.findByUsername("noexiste")).thenReturn(Optional.empty());

        //Act & Assert
        assertThrows(RuntimeException.class, ()-> usuarioService.findByUsername("noexiste"));

        verify(usuarioRepository).findByUsername("noexiste");
    }

    @Test
    public void actualizar_conRolesValidos_debeActualizarRoles() {
        // Arrange
        Rol rolGerente = new Rol();
        rolGerente.setId(2L);
        rolGerente.setNombre("GERENTE");

        UsuarioRequestDTO usuarioRequestDTO = new UsuarioRequestDTO(Set.of("GERENTE"));

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(rolRepository.findByNombre("GERENTE")).thenReturn(Optional.of(rolGerente));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        UsuarioResponseDTO respuesta = usuarioService.actualizar(1L, usuarioRequestDTO);

        // Assert
        assertNotNull(respuesta);
        assertTrue(respuesta.roles().contains("GERENTE"));
        verify(usuarioRepository).save(any(Usuario.class));
    }
}