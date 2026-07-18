package com.minimarket.service;

import com.minimarket.dto.categoria.CategoriaRequestDTO;
import com.minimarket.dto.categoria.CategoriaResponseDTO;
import com.minimarket.entity.Categoria;
import com.minimarket.repository.CategoriaRepository;
import com.minimarket.service.impl.CategoriaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoriaServiceImplTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaServiceImpl categoriaService;

    private Categoria categoria;

    @BeforeEach
    public void setUp() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Bebidas");
    }

    @Test
    public void findAll_debeRetornarListaDeCategorias(){
        //Arrange
        when(categoriaRepository.findAll()).thenReturn(List.of(categoria));

        //Act
        List<CategoriaResponseDTO> resultado = categoriaService.findAll();

        //Assert
        assertNotNull(resultado);
        assertEquals("Bebidas", resultado.get(0).nombre());
        verify(categoriaRepository).findAll();

    }

    @Test
    public void findAll_sinCategorias_debeRetornarListaVacia(){
        //Arrange
        when(categoriaRepository.findAll()).thenReturn(List.of());

        //Act
        List<CategoriaResponseDTO> resultado = categoriaService.findAll();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(categoriaRepository).findAll();
    }

    @Test
    public void findById_cuandoExiste_debeRetornarCategoria(){
        //Arrange
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        //Act
        CategoriaResponseDTO resultado = categoriaService.findById(1L);

        //Assert
        assertNotNull(resultado);
        assertEquals("Bebidas", resultado.nombre());
        verify(categoriaRepository).findById(1L);
    }

    @Test
    public void findById_cuandoNoExiste_debeLanzarExcepcion(){
        //Arrange
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> categoriaService.findById(1L));

        verify(categoriaRepository).findById(1L);
    }

    @Test
    public void crear_conNombreValido_debeCrearCategoria() {
        // Arrange
        CategoriaRequestDTO categoriaRequestDTO = new CategoriaRequestDTO(categoria.getNombre());
        when(categoriaRepository.existsByNombre(categoria.getNombre())).thenReturn(false);
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

        // Act
        CategoriaResponseDTO respuesta = categoriaService.crear(categoriaRequestDTO);

        // Assert
        assertNotNull(respuesta);
        assertEquals("Bebidas", respuesta.nombre());
        verify(categoriaRepository).save(any(Categoria.class));
    }

    @Test
    public void crear_conNombreDuplicado_debeLanzarExcepcion(){
        //Arrange
        CategoriaRequestDTO categoriaRequestDTO = new CategoriaRequestDTO(categoria.getNombre());
        when(categoriaRepository.existsByNombre(categoria.getNombre())).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> categoriaService.crear(categoriaRequestDTO));

        verify(categoriaRepository, never()).save(any(Categoria.class));
    }


}