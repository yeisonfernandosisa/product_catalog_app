package com.catalog.controller;

import com.catalog.dto.AuthDtos.*;
import com.catalog.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService service;
    public AuthController(UserService service) { this.service = service; }
    @PostMapping("/register") public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return service.register(request);
    }
    @PostMapping("/login") public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return service.login(request);
    }
    @GetMapping("/me") public UserResponse profile(Authentication auth) {
        return service.profile(auth.getName());
    }
    @PutMapping("/me") public AuthResponse updateProfile(Authentication auth,
        @Valid @RequestBody UpdateProfileRequest request) {
        return service.updateProfile(auth.getName(), request, auth.getName());
    }
}
