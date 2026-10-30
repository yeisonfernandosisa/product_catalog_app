package com.catalog.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequestDTO {

    @NotBlank(message = "no puede estar vacío")
    @Email(message = "debe tener un formato de correo electrónico válido")
    private String email;

    @NotBlank(message = "no puede estar vacía")
    @Size(min = 8, message = "debe tener al menos {min} caracteres")
    private String password;

    @NotBlank(message = "no puede estar vacío")
    private String fullName;

    public RegisterRequestDTO() {
    }

    public RegisterRequestDTO(String email, String password, String fullName) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
