package com.catalog.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.catalog.dto.AuthResponseDTO;
import com.catalog.dto.LoginRequestDTO;
import com.catalog.dto.RegisterRequestDTO;
import com.catalog.dto.UpdateProfileRequestDTO;
import com.catalog.dto.UserResponseDTO;
import com.catalog.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService service;

    public AuthController(UserService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public AuthResponseDTO register(@Valid @RequestBody RegisterRequestDTO request) {
        return service.register(request);
    }

    @PostMapping("/login")
    public AuthResponseDTO login(@Valid @RequestBody LoginRequestDTO request) {
        return service.login(request);
    }

    @GetMapping("/profile")
    public UserResponseDTO profile(Authentication auth) {
        return service.profile(auth.getName());
    }

    @PutMapping("/profile")
    public AuthResponseDTO updateProfile(
            Authentication auth, @Valid @RequestBody UpdateProfileRequestDTO request) {
        return service.updateProfile(auth.getName(), request, auth.getName());
    }
}
