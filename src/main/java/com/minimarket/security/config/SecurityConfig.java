package com.minimarket.security.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // JWT no usa cookies de sesion, por eso se deshabilita CSRF.
                .csrf(csrf -> csrf.disable())

                // JWT trabaja sin sesiones del lado del servidor.
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Manejo claro de errores:
                // 401 cuando no hay autenticacion valida.
                // 403 cuando el usuario esta autenticado, pero no tiene permisos.
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler())
                )

                .authorizeHttpRequests(auth -> auth

                        // Endpoints públicos.
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/public/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()

                        // Documentación Swagger/OpenAPI - acceso público.
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml"
                        ).permitAll()

                        // Usuarios: solo Gerente gestiona cuentas.
                        .requestMatchers("/api/usuarios", "/api/usuarios/**")
                        .hasAuthority("GERENTE")

                        // Productos:
                        // Consultar: todos los roles, incluido Cliente.
                        .requestMatchers(HttpMethod.GET, "/api/productos", "/api/productos/**")
                        .hasAnyAuthority("GERENTE", "JEFE_TURNO", "REPONEDOR", "CAJERO", "ASISTENTE", "CLIENTE")

                        // Actualizar precios / editar producto: solo Gerente.
                        .requestMatchers(HttpMethod.PUT, "/api/productos", "/api/productos/**")
                        .hasAuthority("GERENTE")

                        // Crear producto: Gerente.
                        .requestMatchers(HttpMethod.POST, "/api/productos", "/api/productos/**")
                        .hasAuthority("GERENTE")

                        // Eliminar producto: Gerente.
                        .requestMatchers(HttpMethod.DELETE, "/api/productos", "/api/productos/**")
                        .hasAuthority("GERENTE")

                        // Inventario:
                        // Consultar stock: Gerente, Jefe de Turno, Reponedor.
                        .requestMatchers(HttpMethod.GET, "/api/inventario", "/api/inventario/**")
                        .hasAnyAuthority("GERENTE", "JEFE_TURNO", "REPONEDOR")

                        // Modificar stock (reposición): Reponedor, Jefe de Turno, Gerente.
                        .requestMatchers(HttpMethod.PUT, "/api/inventario", "/api/inventario/**")
                        .hasAnyAuthority("GERENTE", "JEFE_TURNO", "REPONEDOR")
                        .requestMatchers(HttpMethod.POST, "/api/inventario", "/api/inventario/**")
                        .hasAnyAuthority("GERENTE", "JEFE_TURNO", "REPONEDOR")

                        // Reportes de rotación (si expones un endpoint tipo /api/inventario/reportes):
                        .requestMatchers("/api/inventario/reportes/**")
                        .hasAnyAuthority("GERENTE", "JEFE_TURNO")

                        // Ventas:
                        // Registrar venta (caja): Cajero, Jefe de Turno, Gerente.
                        .requestMatchers(HttpMethod.POST, "/api/ventas", "/api/ventas/**")
                        .hasAnyAuthority("GERENTE", "JEFE_TURNO", "CAJERO")

                        // Consultar ventas / reportes: Gerente, Jefe de Turno.
                        .requestMatchers(HttpMethod.GET, "/api/ventas", "/api/ventas/**")
                        .hasAnyAuthority("GERENTE", "JEFE_TURNO")

                        // Carrito / pedidos en línea:
                        // Crear y gestionar su propio pedido: Cliente.
                        .requestMatchers(HttpMethod.POST, "/api/carrito", "/api/carrito/**")
                        .hasAuthority("CLIENTE")

                        // Consultar carritos: Cliente (el suyo) y Asistente (soporte).
                        .requestMatchers(HttpMethod.GET, "/api/carrito", "/api/carrito/**")
                        .hasAnyAuthority("CLIENTE", "ASISTENTE", "GERENTE")

                        // DetalleVenta:
                        // Consultar: Gerente, Jefe de Turno (igual que Venta).
                        .requestMatchers(HttpMethod.GET, "/api/detalle-ventas", "/api/detalle-ventas/**")
                        .hasAnyAuthority("GERENTE", "JEFE_TURNO")

                        // Agregar detalle a una venta: Cajero, Jefe de Turno, Gerente (igual que registrar venta).
                        .requestMatchers(HttpMethod.POST, "/api/detalle-ventas", "/api/detalle-ventas/**")
                        .hasAnyAuthority("GERENTE", "JEFE_TURNO", "CAJERO")

                        // Actualizar/eliminar detalle: solo Gerente (por el riesgo de descuadre contable que documentamos).
                        .requestMatchers(HttpMethod.PUT, "/api/detalle-ventas", "/api/detalle-ventas/**")
                        .hasAuthority("GERENTE")
                        .requestMatchers(HttpMethod.DELETE, "/api/detalle-ventas", "/api/detalle-ventas/**")
                        .hasAuthority("GERENTE")

                        // Cualquier otra ruta requiere autenticación.
                        .anyRequest().authenticated()
                )

                // Filtro JWT antes del filtro estandar de autenticacion de Spring.
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // Permite usar H2 Console en navegador durante desarrollo.
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            // Respuesta cuando no hay token o la autenticacion no es valida.
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"No autenticado. Debe enviar un token JWT valido.\"}");
        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            // Respuesta cuando el usuario esta autenticado, pero no tiene permisos.
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Acceso denegado. No tiene permisos para este recurso.\"}");
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
            throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}