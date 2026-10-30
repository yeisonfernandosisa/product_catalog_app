package com.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateProfileRequestDTO {

    @NotBlank
    private String fullName;

    @Size(min = 8)
    private String password;

    public UpdateProfileRequestDTO() {
    }

    public UpdateProfileRequestDTO(String fullName, String password) {
        this.fullName = fullName;
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
