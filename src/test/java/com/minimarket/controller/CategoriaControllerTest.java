package com.minimarket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.dto.categoria.CategoriaRequestDTO;
import com.minimarket.dto.categoria.CategoriaResponseDTO;
import com.minimarket.hateoas.CategoriaModelAssembler;
import com.minimarket.security.config.JwtAuthenticationFilter;
import com.minimarket.security.service.JwtService;
import com.minimarket.service.CategoriaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoriaController.class)
public class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoriaService categoriaService;

    @MockitoBean
    private CategoriaModelAssembler assembler;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private CategoriaResponseDTO categoriaResponseDTO;

    @BeforeEach
    public void setUp() {
        categoriaResponseDTO = new CategoriaResponseDTO(1L, "Bebidas");
    }

    @Test
    @WithMockUser(authorities = "GERENTE")
    public void listarCategorias_debeRetornar200() throws Exception {
        // Arrange
        when(categoriaService.findAll()).thenReturn(List.of(categoriaResponseDTO));
        when(assembler.toCollectionModel(anyList())).thenReturn(
                org.springframework.hateoas.CollectionModel.of(List.of(EntityModel.of(categoriaResponseDTO)))
        );

        // Act & Assert
        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk());
    }
}