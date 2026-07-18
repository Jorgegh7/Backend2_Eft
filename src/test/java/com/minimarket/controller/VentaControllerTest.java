package com.minimarket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.dto.venta.DetalleVentaItemDTO;
import com.minimarket.dto.venta.VentaRequestDTO;
import com.minimarket.dto.venta.VentaResponseDTO;
import com.minimarket.dto.detalleVenta.DetalleVentaResponseDTO;
import com.minimarket.hateoas.VentaModelAssembler;
import com.minimarket.security.config.SecurityConfig;
import com.minimarket.security.service.CustomUserDetailsService;
import com.minimarket.security.service.JwtService;
import com.minimarket.service.VentaService;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VentaController.class)
@Import(SecurityConfig.class)
class VentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VentaService ventaService;

    @MockitoBean
    private VentaModelAssembler assembler;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private JwtService jwtService;

    private VentaResponseDTO ventaResponseDTO;

    @BeforeEach
    public void setUp() {
        DetalleVentaResponseDTO detalle = new DetalleVentaResponseDTO(1L, 1L, "Arroz", 2, 1500.0);
        ventaResponseDTO = new VentaResponseDTO(
                1L, 1L, "jperez", LocalDateTime.now(), 3000.0, List.of(detalle));

        when(assembler.toModel(any(VentaResponseDTO.class)))
                .thenReturn(EntityModel.of(ventaResponseDTO));
        when(assembler.toCollectionModel(anyList()))
                .thenReturn(CollectionModel.of(List.of(EntityModel.of(ventaResponseDTO))));
    }

    @Test
    @WithMockUser(authorities = "GERENTE")
    void gerentePuedeListarVentas() throws Exception {
        when(ventaService.findAll()).thenReturn(List.of(ventaResponseDTO));

        mockMvc.perform(get("/api/ventas"))
                .andExpect(status().isOk());

        verify(ventaService).findAll();
    }

    @Test
    @WithMockUser(authorities = "GERENTE")
    void gerentePuedeObtenerVentaPorId() throws Exception {
        when(ventaService.findById(1L)).thenReturn(ventaResponseDTO);

        mockMvc.perform(get("/api/ventas/1"))
                .andExpect(status().isOk());

        verify(ventaService).findById(1L);
    }

    @Test
    @WithMockUser(authorities = "GERENTE")
    void gerentePuedeListarPorUsuario() throws Exception {
        when(ventaService.findByUsuarioId(1L)).thenReturn(List.of(ventaResponseDTO));

        mockMvc.perform(get("/api/ventas/usuario/1"))
                .andExpect(status().isOk());

        verify(ventaService).findByUsuarioId(1L);
    }

    @Test
    @WithMockUser(authorities = "CAJERO")
    void cajeroPuedeRegistrarVenta() throws Exception {
        DetalleVentaItemDTO item = new DetalleVentaItemDTO(1L, 2);
        VentaRequestDTO request = new VentaRequestDTO(1L, List.of(item));

        when(ventaService.crear(any(VentaRequestDTO.class))).thenReturn(ventaResponseDTO);

        mockMvc.perform(post("/api/ventas")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(ventaService).crear(any(VentaRequestDTO.class));
    }
}