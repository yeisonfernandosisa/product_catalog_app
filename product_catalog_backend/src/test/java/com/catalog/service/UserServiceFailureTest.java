package com.catalog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.catalog.dto.LoginRequestDTO;
import com.catalog.dto.RegisterRequestDTO;
import com.catalog.dto.UpdateProfileRequestDTO;
import com.catalog.dto.UpdateUserRequestDTO;
import com.catalog.model.AppUser;
import com.catalog.model.Role;
import com.catalog.repository.CategoryRepository;
import com.catalog.repository.ProductRepository;
import com.catalog.repository.UserRepository;
import com.catalog.security.JwtService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

class UserServiceFailureTest {
  private static final String EMAIL = "user@example.com";
  private UserRepository users;
  private ProductRepository products;
  private CategoryRepository categories;
  private PasswordEncoder encoder;
  private UserService service;

  @BeforeEach
  void setUp() {
    users = mock(UserRepository.class);
    products = mock(ProductRepository.class);
    categories = mock(CategoryRepository.class);
    encoder = mock(PasswordEncoder.class);
    service =
        new UserService(
            users,
            encoder,
            new JwtService("test-secret-key-with-at-least-32-characters"),
            new CatalogOwnershipService(products, categories));
  }

  @Test
  void rejectsRegistrationWhenEmailAlreadyExists() {
    when(users.existsByEmail(EMAIL)).thenReturn(true);

    assertStatus(
        HttpStatus.CONFLICT,
        () -> service.register(new RegisterRequestDTO(EMAIL, "password123", "User")));
    verify(users, never()).save(any(AppUser.class));
  }

  @Test
  void rejectsLoginWhenUserDoesNotExist() {
    when(users.findByEmail(EMAIL)).thenReturn(Optional.empty());

    assertStatus(
        HttpStatus.UNAUTHORIZED,
        () -> service.login(new LoginRequestDTO(EMAIL, "password123")));
  }

  @Test
  void propagatesRepositoryFailureWhenListingUsers() {
    when(users.findAll(PageRequest.of(0, 10)))
        .thenThrow(new IllegalStateException("Database unavailable"));

    assertThrows(
        IllegalStateException.class, () -> service.list(PageRequest.of(0, 10)));
  }

  @Test
  void returnsNotFoundWhenProfileUserDoesNotExist() {
    when(users.findByEmail(EMAIL)).thenReturn(Optional.empty());

    assertStatus(HttpStatus.NOT_FOUND, () -> service.profile(EMAIL));
  }

  @Test
  void returnsNotFoundWhenUpdatingMissingProfile() {
    when(users.findByEmail(EMAIL)).thenReturn(Optional.empty());

    assertStatus(
        HttpStatus.NOT_FOUND,
        () -> service.updateProfile(EMAIL, new UpdateProfileRequestDTO("New name", null), EMAIL));
  }

  @Test
  void returnsNotFoundWhenUpdatingMissingUser() {
    when(users.findById(1L)).thenReturn(Optional.empty());

    assertStatus(
        HttpStatus.NOT_FOUND,
        () ->
            service.update(
                1L, new UpdateUserRequestDTO(EMAIL, "User", Role.USER, null), "admin@example.com"));
  }

  private void assertStatus(
      HttpStatus status, org.junit.jupiter.api.function.Executable action) {
    ResponseStatusException exception = assertThrows(ResponseStatusException.class, action);
    assertEquals(status, exception.getStatusCode());
  }
}
