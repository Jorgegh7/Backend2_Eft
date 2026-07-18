package com.minimarket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.dto.categoria.CategoriaRequestDTO;
import com.minimarket.dto.categoria.CategoriaResponseDTO;
import com.minimarket.hateoas.CategoriaModelAssembler;
import com.minimarket.security.config.SecurityConfig;
import com.minimarket.security.service.CustomUserDetailsService;
import com.minimarket.security.service.JwtService;
import com.minimarket.service.CategoriaService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoriaController.class)
@Import(SecurityConfig.class)
class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoriaService categoriaService;

    @MockitoBean
    private CategoriaModelAssembler assembler;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private JwtService jwtService;

    private CategoriaResponseDTO categoriaResponseDTO;

    @BeforeEach
    public void setUp() {
        categoriaResponseDTO = new CategoriaResponseDTO(1L, "Bebidas");
        when(assembler.toModel(any(CategoriaResponseDTO.class)))
                .thenReturn(EntityModel.of(categoriaResponseDTO));
        when(assembler.toCollectionModel(anyList()))
                .thenReturn(CollectionModel.of(List.of(EntityModel.of(categoriaResponseDTO))));
    }

    @Test
    @WithMockUser(authorities = "GERENTE")
    void gerentePuedeListarCategorias() throws Exception {
        when(categoriaService.findAll()).thenReturn(List.of(categoriaResponseDTO));

        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk());

        verify(categoriaService).findAll();
    }

    @Test
    @WithMockUser(authorities = "GERENTE")
    void gerentePuedeObtenerCategoriaPorId() throws Exception {
        when(categoriaService.findById(1L)).thenReturn(categoriaResponseDTO);

        mockMvc.perform(get("/api/categorias/1"))
                .andExpect(status().isOk());

        verify(categoriaService).findById(1L);
    }

    @Test
    @WithMockUser(authorities = "GERENTE")
    void gerentePuedeCrearCategoria() throws Exception {
        CategoriaRequestDTO request = new CategoriaRequestDTO("Lacteos");
        when(categoriaService.crear(any(CategoriaRequestDTO.class))).thenReturn(categoriaResponseDTO);

        mockMvc.perform(post("/api/categorias")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(categoriaService).crear(any(CategoriaRequestDTO.class));
    }

    @Test
    @WithMockUser(authorities = "GERENTE")
    void gerentePuedeActualizarCategoria() throws Exception {
        CategoriaRequestDTO request = new CategoriaRequestDTO("Lacteos");
        when(categoriaService.actualizar(anyLong(), any(CategoriaRequestDTO.class)))
                .thenReturn(categoriaResponseDTO);

        mockMvc.perform(put("/api/categorias/1")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(categoriaService).actualizar(anyLong(), any(CategoriaRequestDTO.class));
    }

    @Test
    @WithMockUser(authorities = "GERENTE")
    void gerentePuedeEliminarCategoria() throws Exception {
        mockMvc.perform(delete("/api/categorias/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(categoriaService).deleteById(1L);
    }

}