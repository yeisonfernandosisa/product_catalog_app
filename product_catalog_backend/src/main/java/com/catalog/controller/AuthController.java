package com.catalog.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.catalog.dto.AuthResponseDTO;
import com.catalog.dto.LoginRequestDTO;
import com.catalog.dto.RegisterRequestDTO;
import com.catalog.dto.UpdateProfileRequestDTO;
import com.catalog.dto.UserResponseDTO;
import com.catalog.service.TokenRevocationService;
import com.catalog.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService service;
    private final TokenRevocationService tokenRevocations;

    public AuthController(UserService service, TokenRevocationService tokenRevocations) {
        this.service = service;
        this.tokenRevocations = tokenRevocations;
    }

    @PostMapping("/register")
    public AuthResponseDTO register(@Valid @RequestBody RegisterRequestDTO request) {
        return service.register(request);
    }

    @PostMapping("/login")
    public AuthResponseDTO login(@Valid @RequestBody LoginRequestDTO request) {
        return service.login(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            Authentication auth, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        String token = authorization.substring("Bearer ".length());
        tokenRevocations.revoke(token, auth.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping({"/me", "/profile"})
    public UserResponseDTO profile(Authentication auth) {
        return service.profile(auth.getName());
    }

    @PutMapping({"/me", "/profile"})
    public AuthResponseDTO updateProfile(
            Authentication auth, @Valid @RequestBody UpdateProfileRequestDTO request) {
        return service.updateProfile(auth.getName(), request, auth.getName());
    }
}
