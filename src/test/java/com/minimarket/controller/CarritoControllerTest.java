package com.minimarket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.dto.carrito.CarritoRequestDTO;
import com.minimarket.dto.carrito.CarritoResponseDTO;
import com.minimarket.hateoas.CarritoModelAssembler;
import com.minimarket.security.config.SecurityConfig;
import com.minimarket.security.service.CustomUserDetailsService;
import com.minimarket.security.service.JwtService;
import com.minimarket.service.CarritoService;
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

@WebMvcTest(CarritoController.class)
@Import(SecurityConfig.class)
class CarritoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CarritoService carritoService;

    @MockitoBean
    private CarritoModelAssembler assembler;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private JwtService jwtService;

    private CarritoResponseDTO carritoResponseDTO;

    @BeforeEach
    public void setUp() {
        carritoResponseDTO = new CarritoResponseDTO(1L, 1L, "jperez", 1L, "Arroz", 3);
        when(assembler.toModel(any(CarritoResponseDTO.class)))
                .thenReturn(EntityModel.of(carritoResponseDTO));
        when(assembler.toCollectionModel(anyList()))
                .thenReturn(CollectionModel.of(List.of(EntityModel.of(carritoResponseDTO))));
    }

    @Test
    @WithMockUser(authorities = "GERENTE")
    void gerentePuedeListarCarritos() throws Exception {
        when(carritoService.findAll()).thenReturn(List.of(carritoResponseDTO));

        mockMvc.perform(get("/api/carrito"))
                .andExpect(status().isOk());

        verify(carritoService).findAll();
    }

    @Test
    @WithMockUser(authorities = "CLIENTE")
    void clientePuedeObtenerCarritoPorId() throws Exception {
        when(carritoService.findById(1L)).thenReturn(carritoResponseDTO);

        mockMvc.perform(get("/api/carrito/1"))
                .andExpect(status().isOk());

        verify(carritoService).findById(1L);
    }

    @Test
    @WithMockUser(authorities = "GERENTE")
    void gerentePuedeListarPorUsuario() throws Exception {
        when(carritoService.findByUsuarioId(1L)).thenReturn(List.of(carritoResponseDTO));

        mockMvc.perform(get("/api/carrito/usuario/1"))
                .andExpect(status().isOk());

        verify(carritoService).findByUsuarioId(1L);
    }

    @Test
    @WithMockUser(authorities = "CLIENTE")
    void clientePuedeAgregarProductoAlCarrito() throws Exception {
        CarritoRequestDTO request = new CarritoRequestDTO(1L, 1L, 3);
        when(carritoService.agregarProducto(any(CarritoRequestDTO.class)))
                .thenReturn(carritoResponseDTO);

        mockMvc.perform(post("/api/carrito")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(carritoService).agregarProducto(any(CarritoRequestDTO.class));
    }

    @Test
    @WithMockUser(authorities = "CLIENTE")
    void clientePuedeActualizarCarrito() throws Exception {
        CarritoRequestDTO request = new CarritoRequestDTO(1L, 1L, 5);
        when(carritoService.actualizar(anyLong(), any(CarritoRequestDTO.class)))
                .thenReturn(carritoResponseDTO);

        mockMvc.perform(put("/api/carrito/1")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(carritoService).actualizar(anyLong(), any(CarritoRequestDTO.class));
    }

    @Test
    @WithMockUser(authorities = "CLIENTE")
    void clientePuedeEliminarCarrito() throws Exception {
        mockMvc.perform(delete("/api/carrito/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(carritoService).deleteById(1L);
    }
}