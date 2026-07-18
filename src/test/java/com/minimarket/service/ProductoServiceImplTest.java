package com.minimarket.service.impl;

import com.minimarket.dto.producto.ProductoRequestDTO;
import com.minimarket.dto.producto.ProductoResponseDTO;
import com.minimarket.entity.Categoria;
import com.minimarket.entity.Producto;
import com.minimarket.repository.CarritoRepository;
import com.minimarket.repository.CategoriaRepository;
import com.minimarket.repository.DetalleVentaRepository;
import com.minimarket.repository.InventarioRepository;
import com.minimarket.repository.ProductoRepository;
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
public class ProductoServiceImplTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private DetalleVentaRepository detalleVentaRepository;

    @Mock
    private InventarioRepository inventarioRepository;

    @Mock
    private CarritoRepository carritoRepository;

    @InjectMocks
    private ProductoServiceImpl productoService;

    private Categoria categoria;
    private Producto producto;

    @BeforeEach
    public void setUp() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Abarrotes");

        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Arroz");
        producto.setPrecio(1500.0);
        producto.setStock(10);
        producto.setCategoria(categoria);
    }

    @Test
    public void findAll_debeRetornarListaDeProductos(){
        //Arrange
        when(productoRepository.findAll()).thenReturn(List.of(producto));

        //Act
        List<ProductoResponseDTO> respuesta = productoService.findAll();

        //Assert
        assertNotNull(respuesta);
        assertEquals(1, respuesta.size());
        assertEquals("Arroz", respuesta.get(0).nombre());
        assertEquals(1500.0, respuesta.get(0).precio());
        assertEquals(1L, respuesta.get(0).categoriaId());
        verify(productoRepository).findAll();
    }

    @Test
    public void findById_cuandoExiste_debeRetornarProducto() {
        // Arrange
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // Act
        ProductoResponseDTO respuesta = productoService.findById(1L);

        // Assert
        assertNotNull(respuesta);
        assertEquals("Arroz", respuesta.nombre());
        assertEquals(1500.0, respuesta.precio());
        assertEquals(10, respuesta.stock());
        assertEquals(1L, respuesta.categoriaId());
        assertEquals("Abarrotes", respuesta.categoriaNombre());
        verify(productoRepository).findById(1L);
    }

    @Test
    public void findById_cuandoNoExiste_debeLanzarExcepcion(){
        //Arrange
        when(productoRepository.findById(2L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> productoService.findById(2L));

        verify(productoRepository).findById(2L);
    }






}