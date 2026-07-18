package com.minimarket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.dto.producto.ProductoRequestDTO;
import com.minimarket.dto.producto.ProductoResponseDTO;
import com.minimarket.hateoas.ProductoModelAssembler;
import com.minimarket.security.config.SecurityConfig;
import com.minimarket.security.service.CustomUserDetailsService;
import com.minimarket.security.service.JwtService;
import com.minimarket.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductoController.class)
@Import(SecurityConfig.class)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductoService productoService;

    @MockitoBean
    private ProductoModelAssembler assembler;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private JwtService jwtService;

    private ProductoResponseDTO productoResponseDTO;

    @BeforeEach
    public void setUp() {
        productoResponseDTO = new ProductoResponseDTO(1L, "Arroz", 1500.0, 10, 1L, "Abarrotes");
        when(assembler.toModel(any(ProductoResponseDTO.class)))
                .thenReturn(EntityModel.of(productoResponseDTO));
        when(assembler.toCollectionModel(anyList()))
                .thenReturn(CollectionModel.of(List.of(EntityModel.of(productoResponseDTO))));
    }

    @Test
    @WithMockUser(authorities = "CLIENTE")
    void clientePuedeListarProductos() throws Exception {
        when(productoService.findAll()).thenReturn(List.of(productoResponseDTO));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk());

        verify(productoService).findAll();
    }

    @Test
    @WithMockUser(authorities = "CLIENTE")
    void clientePuedeObtenerProductoPorId() throws Exception {
        when(productoService.findById(1L)).thenReturn(productoResponseDTO);

        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk());

        verify(productoService).findById(1L);
    }

    @Test
    @WithMockUser(authorities = "GERENTE")
    void gerentePuedeCrearProducto() throws Exception {
        ProductoRequestDTO request = new ProductoRequestDTO("Arroz", 1500.0, 1L);
        when(productoService.crear(any(ProductoRequestDTO.class))).thenReturn(productoResponseDTO);

        mockMvc.perform(post("/api/productos")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(productoService).crear(any(ProductoRequestDTO.class));
    }

    @Test
    @WithMockUser(authorities = "GERENTE")
    void gerentePuedeActualizarProducto() throws Exception {
        ProductoRequestDTO request = new ProductoRequestDTO("Arroz", 1600.0, 1L);
        when(productoService.actualizar(anyLong(), any(ProductoRequestDTO.class)))
                .thenReturn(productoResponseDTO);

        mockMvc.perform(put("/api/productos/1")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(productoService).actualizar(anyLong(), any(ProductoRequestDTO.class));
    }

    @Test
    @WithMockUser(authorities = "GERENTE")
    void gerentePuedeEliminarProducto() throws Exception {
        mockMvc.perform(delete("/api/productos/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(productoService).deleteById(1L);
    }

    @Test
    @WithMockUser(authorities = "CLIENTE")
    void clientePuedeListarPorCategoria() throws Exception {
        when(productoService.findByCategoriaId(1L)).thenReturn(List.of(productoResponseDTO));

        mockMvc.perform(get("/api/productos/categoria/1"))
                .andExpect(status().isOk());

        verify(productoService).findByCategoriaId(1L);
    }
}