package com.catalog.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.catalog.security.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    @SuppressWarnings("unused")
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter)
            throws Exception {
        return http.csrf(csrf -> csrf.disable())
                .cors(cors -> {
                })
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        auth
                        -> auth.requestMatchers("/api/auth/register", "/api/auth/login", "/error")
                                .permitAll()
                                .requestMatchers("/api/users/**")
                                .hasRole("ADMIN")
                                .requestMatchers(HttpMethod.OPTIONS, "/**")
                                .permitAll()
                                .anyRequest()
                                .authenticated())
                .exceptionHandling(
                        errors
                        -> errors
                                .authenticationEntryPoint(
                                        (request, response, exception) -> {
                                            response.setStatus(401);
                                            response.setContentType("application/json;charset=UTF-8");
                                            response
                                                    .getWriter()
                                                    .write(
                                                            "{\"status\":401,\"message\":\"Autenticación requerida o token inválido\"}");
                                        })
                                .accessDeniedHandler(
                                        (request, response, exception) -> {
                                            response.setStatus(403);
                                            response.setContentType("application/json;charset=UTF-8");
                                            response
                                                    .getWriter()
                                                    .write(
                                                            "{\"status\":403,\"message\":\"No tienes permisos para este recurso\"}");
                                        }))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    @SuppressWarnings("unused")
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @SuppressWarnings("unused")
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    @SuppressWarnings("unused")
    CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origin}") String allowedOrigin) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(allowedOrigin));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
