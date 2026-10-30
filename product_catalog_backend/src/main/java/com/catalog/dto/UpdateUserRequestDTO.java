package com.catalog.dto;

import com.catalog.model.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateUserRequestDTO {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String fullName;

    private Role role;

    @Size(min = 8)
    private String password;

    public UpdateUserRequestDTO() {
    }

    public UpdateUserRequestDTO(String email, String fullName, Role role, String password) {
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
