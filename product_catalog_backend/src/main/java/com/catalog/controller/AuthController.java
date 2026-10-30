package com.catalog.controller;

import com.catalog.dto.AuthDtos.AuthResponse;
import com.catalog.dto.AuthDtos.LoginRequest;
import com.catalog.dto.AuthDtos.RegisterRequest;
import com.catalog.dto.AuthDtos.UpdateProfileRequest;
import com.catalog.dto.AuthDtos.UserResponse;
import com.catalog.service.TokenRevocationService;
import com.catalog.service.UserService;
import jakarta.validation.Valid;
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
  public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
    return service.register(request);
  }

  @PostMapping("/login")
  public AuthResponse login(@Valid @RequestBody LoginRequest request) {
    return service.login(request);
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(
      Authentication auth, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
    String token = authorization.substring("Bearer ".length());
    tokenRevocations.revoke(token, auth.getName());
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/me")
  public UserResponse profile(Authentication auth) {
    return service.profile(auth.getName());
  }

  @PutMapping("/me")
  public AuthResponse updateProfile(
      Authentication auth, @Valid @RequestBody UpdateProfileRequest request) {
    return service.updateProfile(auth.getName(), request, auth.getName());
  }
}
