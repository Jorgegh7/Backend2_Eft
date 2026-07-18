package com.minimarket.service;

import com.minimarket.dto.carrito.CarritoRequestDTO;
import com.minimarket.dto.carrito.CarritoResponseDTO;
import com.minimarket.entity.Carrito;
import com.minimarket.entity.Categoria;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.CarritoRepository;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.repository.UsuarioRepository;
import com.minimarket.service.impl.CarritoServiceImpl;
import com.minimarket.service.impl.ProductoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CarritoServiceImplTest {

    @Mock
    private CarritoRepository carritoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private CarritoServiceImpl carritoService;

    private Usuario usuario;
    private Producto producto;
    private Carrito carrito;

    @BeforeEach
    public void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("jperez");

        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Abarrotes");

        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Arroz");
        producto.setPrecio(1500.0);
        producto.setStock(10);
        producto.setCategoria(categoria);

        carrito = new Carrito();
        carrito.setId(1L);
        carrito.setUsuario(usuario);
        carrito.setProducto(producto);
        carrito.setCantidad(3);
    }

    @Test
    public void findAll_debeRetornarListaDeCarritos(){
        //Arrange
        when(carritoRepository.findAll()).thenReturn(List.of(carrito));

        //Act
        List<CarritoResponseDTO> respuesta = carritoService.findAll();

        //Assert
        assertNotNull(respuesta);
        assertEquals(1, respuesta.size());
        assertEquals(1, respuesta.get(0).id());
        assertEquals(3, respuesta.get(0).cantidad());
        assertEquals(1, respuesta.get(0).productoId());
        assertEquals("Arroz", respuesta.get(0).productoNombre());
        assertEquals(1, respuesta.get(0).usuarioId());
        assertEquals("jperez", respuesta.get(0).usuarioUsername());
    }

    @Test
    public void findById_cuandoExiste_debeRetornarCarrito(){
        //Arrange
        when(carritoRepository.findById(1L)).thenReturn(Optional.of(carrito));

        //Act
        CarritoResponseDTO respuesta = carritoService.findById(1L);

        //Assert
        assertNotNull(respuesta);
        assertEquals(1, respuesta.id());
        assertEquals(3, respuesta.cantidad());
        assertEquals(1, respuesta.productoId());
        assertEquals("Arroz", respuesta.productoNombre());
        assertEquals(1, respuesta.usuarioId());
        assertEquals("jperez", respuesta.usuarioUsername());
    }

    @Test
    public void findById_cuandoNoExiste_debeLanzarExcepcion(){
        //Arrange
        when(carritoRepository.findById(2L)).thenReturn(Optional.empty());

        //Act & Assert
        assertThrows(RuntimeException.class, () -> carritoService.findById(2L));

        verify(carritoRepository).findById(2L);
    }

    @Test
    public void agregarProducto_conDatosValidos_debeCrearCarrito(){
        //Arrange
        CarritoRequestDTO carritoRequestDTO = new CarritoRequestDTO(1L, 1L, 3);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(carritoRepository.save(any(Carrito.class))).thenReturn(carrito);

        //Act
        CarritoResponseDTO respuesta = carritoService.agregarProducto(carritoRequestDTO);

        //Assert
        assertNotNull(respuesta);
        assertEquals(3, respuesta.cantidad());
        assertEquals(1, respuesta.productoId());
        assertEquals("Arroz", respuesta.productoNombre());
        assertEquals(1, respuesta.usuarioId());
        assertEquals("jperez", respuesta.usuarioUsername());
    }
    @Test
    public void actualizar_conDatosValidos_debeActualizarCarrito(){
        
    }












}