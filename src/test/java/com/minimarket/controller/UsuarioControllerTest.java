package com.minimarket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.dto.usuario.UsuarioRequestDTO;
import com.minimarket.dto.usuario.UsuarioResponseDTO;
import com.minimarket.hateoas.UsuarioModelAssembler;
import com.minimarket.security.config.SecurityConfig;
import com.minimarket.security.service.CustomUserDetailsService;
import com.minimarket.security.service.JwtService;
import com.minimarket.service.UsuarioService;
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
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
@Import(SecurityConfig.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private UsuarioModelAssembler assembler;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private JwtService jwtService;

    private UsuarioResponseDTO usuarioResponseDTO;

    @BeforeEach
    public void setUp() {
        usuarioResponseDTO = new UsuarioResponseDTO(1L, "jperez", Set.of("CLIENTE"));
        when(assembler.toModel(any(UsuarioResponseDTO.class)))
                .thenReturn(EntityModel.of(usuarioResponseDTO));
        when(assembler.toCollectionModel(anyList()))
                .thenReturn(CollectionModel.of(List.of(EntityModel.of(usuarioResponseDTO))));
    }

    @Test
    @WithMockUser(authorities = "GERENTE")
    void gerentePuedeListarUsuarios() throws Exception {
        when(usuarioService.findAll()).thenReturn(List.of(usuarioResponseDTO));

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk());

        verify(usuarioService).findAll();
    }

    @Test
    @WithMockUser(authorities = "GERENTE")
    void gerentePuedeObtenerUsuarioPorId() throws Exception {
        when(usuarioService.findById(1L)).thenReturn(usuarioResponseDTO);

        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().isOk());

        verify(usuarioService).findById(1L);
    }

    @Test
    @WithMockUser(authorities = "GERENTE")
    void gerentePuedeObtenerUsuarioPorUsername() throws Exception {
        when(usuarioService.findByUsername("jperez")).thenReturn(usuarioResponseDTO);

        mockMvc.perform(get("/api/usuarios/username/jperez"))
                .andExpect(status().isOk());

        verify(usuarioService).findByUsername("jperez");
    }

    @Test
    @WithMockUser(authorities = "GERENTE")
    void gerentePuedeActualizarRolesDeUsuario() throws Exception {
        UsuarioRequestDTO request = new UsuarioRequestDTO(Set.of("GERENTE"));
        when(usuarioService.actualizar(anyLong(), any(UsuarioRequestDTO.class)))
                .thenReturn(usuarioResponseDTO);

        mockMvc.perform(put("/api/usuarios/1")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(usuarioService).actualizar(anyLong(), any(UsuarioRequestDTO.class));
    }

    @Test
    @WithMockUser(authorities = "GERENTE")
    void gerentePuedeEliminarUsuario() throws Exception {
        mockMvc.perform(delete("/api/usuarios/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(usuarioService).deleteById(1L);
    }
}