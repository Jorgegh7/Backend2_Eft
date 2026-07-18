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
        verify(productoRepository).findAll();
    }






}