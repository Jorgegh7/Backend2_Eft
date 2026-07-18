package com.minimarket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.dto.inventario.InventarioRequestDTO;
import com.minimarket.dto.inventario.InventarioResponseDTO;
import com.minimarket.entity.TipoMovimiento;
import com.minimarket.hateoas.InventarioModelAssembler;
import com.minimarket.security.config.SecurityConfig;
import com.minimarket.security.service.CustomUserDetailsService;
import com.minimarket.security.service.JwtService;
import com.minimarket.service.InventarioService;
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

@WebMvcTest(InventarioController.class)
@Import(SecurityConfig.class)
class InventarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private InventarioService inventarioService;

    @MockitoBean
    private InventarioModelAssembler assembler;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private JwtService jwtService;

    private InventarioResponseDTO inventarioResponseDTO;

    @BeforeEach
    public void setUp() {
        inventarioResponseDTO = new InventarioResponseDTO(
                1L, 1L, "Arroz", 5, TipoMovimiento.ENTRADA, LocalDateTime.now());
        when(assembler.toModel(any(InventarioResponseDTO.class)))
                .thenReturn(EntityModel.of(inventarioResponseDTO));
        when(assembler.toCollectionModel(anyList()))
                .thenReturn(CollectionModel.of(List.of(EntityModel.of(inventarioResponseDTO))));
    }

    @Test
    @WithMockUser(authorities = "GERENTE")
    void gerentePuedeListarInventario() throws Exception {
        when(inventarioService.findAll()).thenReturn(List.of(inventarioResponseDTO));

        mockMvc.perform(get("/api/inventario"))
                .andExpect(status().isOk());

        verify(inventarioService).findAll();
    }

    @Test
    @WithMockUser(authorities = "GERENTE")
    void gerentePuedeObtenerMovimientoPorId() throws Exception {
        when(inventarioService.findById(1L)).thenReturn(inventarioResponseDTO);

        mockMvc.perform(get("/api/inventario/1"))
                .andExpect(status().isOk());

        verify(inventarioService).findById(1L);
    }

    @Test
    @WithMockUser(authorities = "REPONEDOR")
    void reponedorPuedeRegistrarMovimiento() throws Exception {
        InventarioRequestDTO request = new InventarioRequestDTO(1L, 5, TipoMovimiento.ENTRADA);
        when(inventarioService.registrarMovimiento(any(InventarioRequestDTO.class)))
                .thenReturn(inventarioResponseDTO);

        mockMvc.perform(post("/api/inventario")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(inventarioService).registrarMovimiento(any(InventarioRequestDTO.class));
    }

    @Test
    @WithMockUser(authorities = "GERENTE")
    void gerentePuedeEliminarMovimiento() throws Exception {
        mockMvc.perform(delete("/api/inventario/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(inventarioService).deleteById(1L);
    }

    @Test
    @WithMockUser(authorities = "GERENTE")
    void gerentePuedeListarPorProducto() throws Exception {
        when(inventarioService.findByProductoId(1L)).thenReturn(List.of(inventarioResponseDTO));

        mockMvc.perform(get("/api/inventario/producto/1"))
                .andExpect(status().isOk());

        verify(inventarioService).findByProductoId(1L);
    }
}