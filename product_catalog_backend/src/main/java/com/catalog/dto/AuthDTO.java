package com.catalog.dto;

import com.catalog.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class AuthDTO {
  private AuthDTO() {}

  public static class RegisterRequestDTO {
    @NotBlank @Email private String email;
    @NotBlank @Size(min = 8) private String password;
    @NotBlank private String fullName;

    public RegisterRequestDTO() {}

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

  public static class LoginRequestDTO {
    @NotBlank @Email private String email;
    @NotBlank private String password;

    public LoginRequestDTO() {}

    public LoginRequestDTO(String email, String password) {
      this.email = email;
      this.password = password;
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
  }

  public static class AuthResponseDTO {
    private String token;
    private String email;
    private String fullName;
    private Role role;

    public AuthResponseDTO() {}

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

  public static class UserResponseDTO {
    private Long id;
    private String email;
    private String fullName;
    private Role role;

    public UserResponseDTO() {}

    public UserResponseDTO(Long id, String email, String fullName, Role role) {
      this.id = id;
      this.email = email;
      this.fullName = fullName;
      this.role = role;
    }

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
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

  public static class UpdateUserRequestDTO {
    @NotBlank @Email private String email;
    @NotBlank private String fullName;
    private Role role;
    @Size(min = 8) private String password;

    public UpdateUserRequestDTO() {}

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

  public static class UpdateProfileRequestDTO {
    @NotBlank private String fullName;
    @Size(min = 8) private String password;

    public UpdateProfileRequestDTO() {}

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
}
