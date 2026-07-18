package com.minimarket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.dto.detalleVenta.DetalleVentaRequestDTO;
import com.minimarket.dto.detalleVenta.DetalleVentaResponseDTO;
import com.minimarket.hateoas.DetalleVentaModelAssembler;
import com.minimarket.security.config.SecurityConfig;
import com.minimarket.security.service.CustomUserDetailsService;
import com.minimarket.security.service.JwtService;
import com.minimarket.service.DetalleVentaService;
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

@WebMvcTest(DetalleVentaController.class)
@Import(SecurityConfig.class)
class DetalleVentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DetalleVentaService detalleVentaService;

    @MockitoBean
    private DetalleVentaModelAssembler assembler;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private JwtService jwtService;

    private DetalleVentaResponseDTO detalleVentaResponseDTO;

    @BeforeEach
    public void setUp() {
        detalleVentaResponseDTO = new DetalleVentaResponseDTO(1L, 1L, "Arroz", 2, 1500.0);
        when(assembler.toModel(any(DetalleVentaResponseDTO.class)))
                .thenReturn(EntityModel.of(detalleVentaResponseDTO));
        when(assembler.toCollectionModel(anyList()))
                .thenReturn(CollectionModel.of(List.of(EntityModel.of(detalleVentaResponseDTO))));
    }

    @Test
    @WithMockUser(authorities = "GERENTE")
    void gerentePuedeListarDetalleVentas() throws Exception {
        when(detalleVentaService.findAll()).thenReturn(List.of(detalleVentaResponseDTO));

        mockMvc.perform(get("/api/detalle-ventas"))
                .andExpect(status().isOk());

        verify(detalleVentaService).findAll();
    }

    @Test
    @WithMockUser(authorities = "GERENTE")
    void gerentePuedeObtenerDetalleVentaPorId() throws Exception {
        when(detalleVentaService.findById(1L)).thenReturn(detalleVentaResponseDTO);

        mockMvc.perform(get("/api/detalle-ventas/1"))
                .andExpect(status().isOk());

        verify(detalleVentaService).findById(1L);
    }

    @Test
    @WithMockUser(authorities = "GERENTE")
    void gerentePuedeListarPorVenta() throws Exception {
        when(detalleVentaService.findByVentaId(1L)).thenReturn(List.of(detalleVentaResponseDTO));

        mockMvc.perform(get("/api/detalle-ventas/venta/1"))
                .andExpect(status().isOk());

        verify(detalleVentaService).findByVentaId(1L);
    }

    @Test
    @WithMockUser(authorities = "CAJERO")
    void cajeroPuedeAgregarDetalle() throws Exception {
        DetalleVentaRequestDTO request = new DetalleVentaRequestDTO(1L, 1L, 2);
        when(detalleVentaService.agregarDetalle(any(DetalleVentaRequestDTO.class)))
                .thenReturn(detalleVentaResponseDTO);

        mockMvc.perform(post("/api/detalle-ventas")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(detalleVentaService).agregarDetalle(any(DetalleVentaRequestDTO.class));
    }

    @Test
    @WithMockUser(authorities = "GERENTE")
    void gerentePuedeActualizarDetalleVenta() throws Exception {
        DetalleVentaRequestDTO request = new DetalleVentaRequestDTO(1L, 1L, 4);
        when(detalleVentaService.actualizar(anyLong(), any(DetalleVentaRequestDTO.class)))
                .thenReturn(detalleVentaResponseDTO);

        mockMvc.perform(put("/api/detalle-ventas/1")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(detalleVentaService).actualizar(anyLong(), any(DetalleVentaRequestDTO.class));
    }

    @Test
    @WithMockUser(authorities = "GERENTE")
    void gerentePuedeEliminarDetalleVenta() throws Exception {
        mockMvc.perform(delete("/api/detalle-ventas/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(detalleVentaService).deleteById(1L);
    }
}