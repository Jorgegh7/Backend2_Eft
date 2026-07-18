package com.minimarket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.RolRepository;
import com.minimarket.repository.UsuarioRepository;
import com.minimarket.security.config.SecurityConfig;
import com.minimarket.security.model.AuthRequest;
import com.minimarket.security.model.RegisterRequest;
import com.minimarket.security.service.CustomUserDetailsService;
import com.minimarket.security.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private RolRepository rolRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private Rol rolCliente;

    @BeforeEach
    public void setUp() {
        rolCliente = new Rol();
        rolCliente.setId(1L);
        rolCliente.setNombre("CLIENTE");
    }

    @Test
    void registrarUsuarioNuevo_debeRetornar200() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("nuevoUser");
        request.setPassword("password123");
        request.setRoles(Set.of(rolCliente));

        when(usuarioRepository.findByUsername("nuevoUser")).thenReturn(Optional.empty());
        when(rolRepository.findByNombre("CLIENTE")).thenReturn(Optional.of(rolCliente));
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(new Usuario());

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void registrarUsuarioExistente_debeRetornarBadRequest() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existente");
        request.setPassword("password123");
        request.setRoles(Set.of(rolCliente));

        Usuario existente = new Usuario();
        existente.setUsername("existente");

        when(usuarioRepository.findByUsername("existente")).thenReturn(Optional.of(existente));

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginConCredencialesInvalidas_debeRetornar401() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setUsername("usuario");
        request.setPassword("passwordIncorrecto");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new RuntimeException("Credenciales invalidas"));

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}