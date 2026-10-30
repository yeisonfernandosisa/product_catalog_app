package com.catalog.dto;

import com.catalog.model.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateUserRequestDTO {

    @NotBlank(message = "no puede estar vacío")
    @Email(message = "debe tener un formato de correo electrónico válido")
    private String email;

    @NotBlank(message = "no puede estar vacío")
    private String fullName;

    private Role role;

    @Size(min = 8, message = "debe tener al menos {min} caracteres")
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
