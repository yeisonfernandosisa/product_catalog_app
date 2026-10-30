package com.catalog.dto;

import com.catalog.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class AuthDtos {
    private AuthDtos() {}
    public record RegisterRequest(@NotBlank @Email String email, @NotBlank @Size(min = 8) String password,
                                  @NotBlank String fullName) {}
    public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {}
    public record AuthResponse(String token, String email, String fullName, Role role) {}
    public record UserResponse(Long id, String email, String fullName, Role role) {}
    public record UpdateUserRequest(@NotBlank @Email String email, @NotBlank String fullName, Role role,
                                   @Size(min = 8) String password) {}
    public record UpdateProfileRequest(@NotBlank String fullName, @Size(min = 8) String password) {}
}
