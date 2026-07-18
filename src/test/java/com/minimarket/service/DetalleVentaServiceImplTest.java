package com.minimarket.service;

import com.minimarket.dto.detalleVenta.DetalleVentaRequestDTO;
import com.minimarket.dto.detalleVenta.DetalleVentaResponseDTO;
import com.minimarket.dto.inventario.InventarioRequestDTO;
import com.minimarket.entity.*;
import com.minimarket.repository.DetalleVentaRepository;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.repository.VentaRepository;
import com.minimarket.service.impl.DetalleVentaServiceImpl;
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
public class DetalleVentaServiceImplTest {

    @Mock
    private DetalleVentaRepository detalleVentaRepository;

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private InventarioService inventarioService;

    @InjectMocks
    private DetalleVentaServiceImpl detalleVentaService;

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
    }

    @Test
    public void findAll_debeRetornarListaDeDetalles() {
        when(detalleVentaRepository.findAll()).thenReturn(List.of(detalleVenta));

        List<DetalleVentaResponseDTO> respuesta = detalleVentaService.findAll();

        assertNotNull(respuesta);
        assertEquals(1L, respuesta.get(0).id());
        assertEquals(1L, respuesta.get(0).productoId());
        assertEquals(2, respuesta.get(0).cantidad());
        assertEquals(1500.0, respuesta.get(0).precio());
    }

    @Test
    public void findById_cuandoExiste_debeRetornarDetalle() {
        when(detalleVentaRepository.findById(1L)).thenReturn(Optional.of(detalleVenta));

        DetalleVentaResponseDTO respuesta = detalleVentaService.findById(1L);

        assertNotNull(respuesta);
        assertEquals(1L, respuesta.id());
        assertEquals(1L, respuesta.productoId());
        assertEquals(2, respuesta.cantidad());
        assertEquals(1500.0, respuesta.precio());
    }

    @Test
    public void findById_cuandoNoExiste_debeLanzarExcepcion() {
        when(detalleVentaRepository.findById(2L)).thenReturn(Optional.empty());

        RuntimeException excepcion = assertThrows(RuntimeException.class,
                () -> detalleVentaService.findById(2L));

        assertEquals("DetalleVenta no encontrado", excepcion.getMessage());
        verify(detalleVentaRepository).findById(2L);
    }

    @Test
    public void agregarDetalle_conDatosValidos_debeRegistrarSalidaEnInventario() {
        // Arrange
        DetalleVentaRequestDTO request = new DetalleVentaRequestDTO(1L, 1L, 2);

        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(detalleVentaRepository.save(any(DetalleVenta.class))).thenReturn(detalleVenta);

        // Act
        DetalleVentaResponseDTO respuesta = detalleVentaService.agregarDetalle(request);

        // Assert
        assertNotNull(respuesta);
        assertEquals(1500.0, respuesta.precio());
        verify(inventarioService).registrarMovimiento(any(InventarioRequestDTO.class));
        verify(detalleVentaRepository).save(any(DetalleVenta.class));
    }

    @Test
    public void agregarDetalle_conVentaInexistente_debeLanzarExcepcion() {
        DetalleVentaRequestDTO request = new DetalleVentaRequestDTO(2L, 1L, 2);
        when(ventaRepository.findById(2L)).thenReturn(Optional.empty());

        RuntimeException excepcion = assertThrows(RuntimeException.class,
                () -> detalleVentaService.agregarDetalle(request));

        assertEquals("Venta no encontrada", excepcion.getMessage());
        verify(inventarioService, never()).registrarMovimiento(any(InventarioRequestDTO.class));
        verify(detalleVentaRepository, never()).save(any(DetalleVenta.class));
    }

    @Test
    public void agregarDetalle_conStockInsuficiente_debeLanzarExcepcion() {
        DetalleVentaRequestDTO request = new DetalleVentaRequestDTO(1L, 1L, 100);

        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(inventarioService.registrarMovimiento(any(InventarioRequestDTO.class)))
                .thenThrow(new RuntimeException("Stock insuficiente para realizar la salida"));

        assertThrows(RuntimeException.class, () -> detalleVentaService.agregarDetalle(request));

        verify(detalleVentaRepository, never()).save(any(DetalleVenta.class));
    }

    @Test
    public void actualizar_conVentaValida_debeReasignarDetalle() {
        // Arrange
        DetalleVentaRequestDTO request = new DetalleVentaRequestDTO(1L, 1L, 2);

        when(detalleVentaRepository.findById(1L)).thenReturn(Optional.of(detalleVenta));
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        when(detalleVentaRepository.save(any(DetalleVenta.class))).thenReturn(detalleVenta);

        // Act
        DetalleVentaResponseDTO respuesta = detalleVentaService.actualizar(1L, request);

        // Assert
        assertNotNull(respuesta);
        verify(detalleVentaRepository).save(any(DetalleVenta.class));
        verify(inventarioService, never()).registrarMovimiento(any(InventarioRequestDTO.class));
    }

    @Test
    public void actualizar_cuandoDetalleNoExiste_debeLanzarExcepcion() {
        DetalleVentaRequestDTO request = new DetalleVentaRequestDTO(1L, 1L, 4);
        when(detalleVentaRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> detalleVentaService.actualizar(2L, request));

        verify(detalleVentaRepository, never()).save(any(DetalleVenta.class));
    }

    @Test
    public void deleteById_cuandoExiste_debeEliminarCorrectamente() {
        when(detalleVentaRepository.findById(1L)).thenReturn(Optional.of(detalleVenta));

        detalleVentaService.deleteById(1L);

        verify(detalleVentaRepository).deleteById(1L);
    }

    @Test
    public void deleteById_cuandoNoExiste_debeLanzarExcepcion() {
        when(detalleVentaRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> detalleVentaService.deleteById(2L));

        verify(detalleVentaRepository, never()).deleteById(any());
    }

    @Test
    public void findByVentaId_conVentaValida_debeRetornarDetalles() {
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        when(detalleVentaRepository.findByVentaId(1L)).thenReturn(List.of(detalleVenta));

        List<DetalleVentaResponseDTO> respuesta = detalleVentaService.findByVentaId(1L);

        assertNotNull(respuesta);
        assertEquals(1, respuesta.size());
        assertEquals(1500.0, respuesta.get(0).precio());
    }
}