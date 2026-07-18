package com.minimarket.service;

import com.minimarket.dto.detalleVenta.DetalleVentaRequestDTO;
import com.minimarket.dto.detalleVenta.DetalleVentaResponseDTO;
import com.minimarket.entity.*;
import com.minimarket.repository.DetalleVentaRepository;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.repository.VentaRepository;
import com.minimarket.service.impl.DetalleVentaServiceImpl;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DetalleVentaServiceImplTest {

    @Mock
    private DetalleVentaRepository detalleVentaRepository;

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private ProductoRepository productoRepository;

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
    public void findAll_debeRetornarListaDeDetalles(){
        //Arrange
        when(detalleVentaRepository.findAll()).thenReturn(List.of(detalleVenta));

        //Act
        List<DetalleVentaResponseDTO> respuesta = detalleVentaService.findAll();

        //Assert
        assertNotNull(respuesta);
        assertEquals(1L, respuesta.get(0).id());
        assertEquals(1L, respuesta.get(0).productoId());
        assertEquals(2, respuesta.get(0).cantidad());
        assertEquals(1500.0, respuesta.get(0).precio());
    }

    @Test
    public void findById_cuandoExiste_debeRetornarDetalle(){
        //Arrange
        when(detalleVentaRepository.findById(1L)).thenReturn(Optional.of(detalleVenta));

        //Act
        DetalleVentaResponseDTO respuesta = detalleVentaService.findById(1L);

        //Assert
        assertNotNull(respuesta);
        assertEquals(1L, respuesta.id());
        assertEquals(1L, respuesta.productoId());
        assertEquals(2, respuesta.cantidad());
        assertEquals(1500.0, respuesta.precio());
    }

    @Test
    public void findById_cuandoNoExiste_debeLanzarExcepcion() {
        // Arrange
        when(detalleVentaRepository.findById(2L)).thenReturn(Optional.empty());

        // Act
        RuntimeException excepcion = assertThrows(RuntimeException.class,
                () -> detalleVentaService.findById(2L));

        // Assert
        assertEquals("DetalleVenta no encontrado", excepcion.getMessage());
        verify(detalleVentaRepository).findById(2L);
    }

    @Test
    public void agregarDetalle_conDatosValidos_debeCongelarPrecioDelProducto() {
        // Arrange
        DetalleVentaRequestDTO request = new DetalleVentaRequestDTO(1L, 1L, 2);

        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(detalleVentaRepository.save(any(DetalleVenta.class))).thenReturn(detalleVenta);

        ArgumentCaptor<DetalleVenta> captor = ArgumentCaptor.forClass(DetalleVenta.class);

        // Act
        DetalleVentaResponseDTO respuesta = detalleVentaService.agregarDetalle(request);

        // Assert
        verify(detalleVentaRepository).save(captor.capture());
        DetalleVenta detalleGuardado = captor.getValue();

        assertEquals(1500.0, detalleGuardado.getPrecio());
        assertEquals(2, detalleGuardado.getCantidad());
        assertNotNull(respuesta);
    }





}