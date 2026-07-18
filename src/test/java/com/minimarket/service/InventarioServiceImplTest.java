package com.minimarket.service;

import com.minimarket.dto.inventario.InventarioRequestDTO;
import com.minimarket.dto.inventario.InventarioResponseDTO;
import com.minimarket.entity.Categoria;
import com.minimarket.entity.Inventario;
import com.minimarket.entity.Producto;
import com.minimarket.entity.TipoMovimiento;
import com.minimarket.repository.InventarioRepository;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.service.impl.InventarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventarioServiceImplTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private InventarioServiceImpl inventarioService;

    private Producto producto;
    private Inventario inventario;

    @BeforeEach
    public void setUp() {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Abarrotes");

        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Arroz");
        producto.setPrecio(1500.0);
        producto.setStock(10);
        producto.setCategoria(categoria);

        inventario = new Inventario();
        inventario.setId(1L);
        inventario.setProducto(producto);
        inventario.setCantidad(5);
        inventario.setTipoMovimiento(TipoMovimiento.ENTRADA);
        inventario.setFechaMovimiento(LocalDateTime.now());
    }

    @Test
    public void findAll_debeRetornarListaDeMovimientos(){
        //Arrange
        when(inventarioRepository.findAll()).thenReturn(List.of(inventario));

        //Act
        List<InventarioResponseDTO> respuesta = inventarioService.findAll();

        //Assert
        assertNotNull(respuesta);
        assertEquals(1, respuesta.size());
        assertEquals(TipoMovimiento.ENTRADA, respuesta.get(0).tipoMovimiento());
        assertEquals(5, respuesta.get(0).cantidad());
        assertEquals("Arroz", respuesta.get(0).productoNombre());
        assertEquals(1L, respuesta.get(0).productoId());
    }

    @Test
    public void findById_cuandoExiste_debeRetornarMovimiento(){
        //Arrange
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventario));

        //Act
        InventarioResponseDTO respuesta = inventarioService.findById(1L);

        //Assert
        assertNotNull(respuesta);
        assertEquals(TipoMovimiento.ENTRADA, respuesta.tipoMovimiento());
        assertEquals(5, respuesta.cantidad());
        assertEquals("Arroz", respuesta.productoNombre());
        assertEquals(1L, respuesta.productoId());

    }
}