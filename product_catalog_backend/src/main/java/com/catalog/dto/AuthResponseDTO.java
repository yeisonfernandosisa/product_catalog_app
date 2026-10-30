package com.catalog.dto;

import com.catalog.model.Role;

public class AuthResponseDTO {

    private String token;
    private String email;
    private String fullName;
    private Role role;

    public AuthResponseDTO() {
    }

    public AuthResponseDTO(String token, String email, String fullName, Role role) {
        this.token = token;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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
}
