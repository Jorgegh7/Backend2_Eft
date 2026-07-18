package com.minimarket.service;

import com.minimarket.dto.inventario.InventarioRequestDTO;
import com.minimarket.dto.venta.DetalleVentaItemDTO;
import com.minimarket.dto.venta.VentaRequestDTO;
import com.minimarket.dto.venta.VentaResponseDTO;
import com.minimarket.entity.*;
import com.minimarket.repository.DetalleVentaRepository;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.repository.UsuarioRepository;
import com.minimarket.repository.VentaRepository;
import com.minimarket.service.impl.VentaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VentaServiceImplTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private DetalleVentaRepository detalleVentaRepository;

    @Mock
    private InventarioService inventarioService;

    @InjectMocks
    private VentaServiceImpl ventaService;

    private Usuario usuario;
    private Producto producto;
    private Venta venta;
    private DetalleVenta detalleVenta;

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

        venta = new Venta();
        venta.setId(1L);
        venta.setUsuario(usuario);
        venta.setFecha(LocalDateTime.now());

        detalleVenta = new DetalleVenta();
        detalleVenta.setId(1L);
        detalleVenta.setVenta(venta);
        detalleVenta.setProducto(producto);
        detalleVenta.setCantidad(2);
        detalleVenta.setPrecio(1500.0);

        venta.setDetalles(List.of(detalleVenta));
    }

    @Test
    public void findAll_debeRetornarListaConTotalCalculado() {
        when(ventaRepository.findAll()).thenReturn(List.of(venta));

        List<VentaResponseDTO> respuesta = ventaService.findAll();

        assertNotNull(respuesta);
        assertEquals(1, respuesta.size());
        assertEquals(3000.0, respuesta.get(0).total());
        assertEquals("jperez", respuesta.get(0).usuarioUsername());
        assertEquals(1, respuesta.get(0).detalles().size());
    }

    @Test
    public void findById_cuandoExiste_debeRetornarVentaConTotal() {
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));

        VentaResponseDTO respuesta = ventaService.findById(1L);

        assertNotNull(respuesta);
        assertEquals(3000.0, respuesta.total());
        assertEquals(1L, respuesta.usuarioId());
        assertEquals(1, respuesta.detalles().size());
    }

    @Test
    public void findById_cuandoNoExiste_debeLanzarExcepcion() {
        when(ventaRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> ventaService.findById(2L));

        verify(ventaRepository).findById(2L);
    }

    @Test
    public void crear_conDatosValidos_debeCrearVentaConDetalles() {
        // Arrange
        DetalleVentaItemDTO item = new DetalleVentaItemDTO(1L, 2);
        VentaRequestDTO request = new VentaRequestDTO(1L, List.of(item));

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(ventaRepository.save(any(Venta.class))).thenReturn(venta);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(detalleVentaRepository.save(any(DetalleVenta.class))).thenReturn(detalleVenta);

        // Act
        VentaResponseDTO respuesta = ventaService.crear(request);

        // Assert
        assertNotNull(respuesta);
        assertEquals(3000.0, respuesta.total());
        verify(inventarioService).registrarMovimiento(any(InventarioRequestDTO.class));
        verify(detalleVentaRepository).save(any(DetalleVenta.class));
    }

    @Test
    public void crear_conStockInsuficiente_debeLanzarExcepcion() {
        // Arrange
        DetalleVentaItemDTO item = new DetalleVentaItemDTO(1L, 100);
        VentaRequestDTO request = new VentaRequestDTO(1L, List.of(item));

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(ventaRepository.save(any(Venta.class))).thenReturn(venta);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(inventarioService.registrarMovimiento(any(InventarioRequestDTO.class)))
                .thenThrow(new RuntimeException("Stock insuficiente para realizar la salida"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> ventaService.crear(request));

        verify(detalleVentaRepository, never()).save(any(DetalleVenta.class));
    }

    @Test
    public void crear_conUsuarioInexistente_debeLanzarExcepcion() {
        DetalleVentaItemDTO item = new DetalleVentaItemDTO(1L, 2);
        VentaRequestDTO request = new VentaRequestDTO(99L, List.of(item));

        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> ventaService.crear(request));

        verify(ventaRepository, never()).save(any(Venta.class));
    }

    @Test
    public void findByUsuarioId_conUsuarioValido_debeRetornarVentas() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(ventaRepository.findByUsuarioId(1L)).thenReturn(List.of(venta));

        List<VentaResponseDTO> respuesta = ventaService.findByUsuarioId(1L);

        assertNotNull(respuesta);
        assertEquals(1, respuesta.size());
        assertEquals(3000.0, respuesta.get(0).total());
    }
}