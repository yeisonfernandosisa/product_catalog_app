package com.catalog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
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
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

class UserServiceTest {
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
  void registersUserAndReturnsAuthenticationToken() {
    when(users.existsByEmail(EMAIL)).thenReturn(false);
    when(encoder.encode("password123")).thenReturn("hashed-password");
    when(users.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

    var response =
        service.register(new RegisterRequestDTO(" USER@example.com ", "password123", " User "));

    assertEquals(EMAIL, response.getEmail());
    assertEquals("User", response.getFullName());
    assertEquals(Role.USER, response.getRole());
    assertNotNull(response.getToken());
    verify(users).save(any(AppUser.class));
  }

  @Test
  void logsInWithValidCredentials() {
    AppUser user = user(EMAIL, "User", "hashed-password");
    when(users.findByEmail(EMAIL)).thenReturn(Optional.of(user));
    when(encoder.matches("password123", "hashed-password")).thenReturn(true);

    var response = service.login(new LoginRequestDTO(" USER@example.com ", "password123"));

    assertEquals(EMAIL, response.getEmail());
    assertNotNull(response.getToken());
  }

  @Test
  void listsUsersAsResponseDtos() {
    PageRequest pageable = PageRequest.of(0, 10);
    when(users.findAll(pageable))
        .thenReturn(new PageImpl<>(List.of(user(EMAIL, "User", "hash")), pageable, 1));

    var response = service.list(pageable);

    assertEquals(1, response.getTotalElements());
    assertEquals(EMAIL, response.getContent().get(0).getEmail());
    assertEquals("User", response.getContent().get(0).getFullName());
  }

  @Test
  void returnsUserProfile() {
    when(users.findByEmail(EMAIL)).thenReturn(Optional.of(user(EMAIL, "User", "hash")));

    var response = service.profile(EMAIL);

    assertEquals(EMAIL, response.getEmail());
    assertEquals("User", response.getFullName());
    assertEquals(Role.USER, response.getRole());
  }

  @Test
  void updatesProfileAndEncodesOptionalPassword() {
    AppUser user = user(EMAIL, "Old name", "old-hash");
    when(users.findByEmail(EMAIL)).thenReturn(Optional.of(user));
    when(encoder.encode("newpassword")).thenReturn("new-hash");

    var response = service.updateProfile(
        EMAIL, new UpdateProfileRequestDTO(" New name ", "newpassword"), EMAIL);

    assertEquals("New name", response.getFullName());
    assertEquals("new-hash", user.getPassword());
    assertEquals(EMAIL, user.getUpdatedBy());
    assertNotNull(response.getToken());
  }

  @Test
  void updatesUserAndTransfersCatalogWhenEmailChanges() {
    AppUser user = user("old@example.com", "User", "hash");
    when(users.findById(1L)).thenReturn(Optional.of(user));

    var response =
        service.update(
            1L,
            new UpdateUserRequestDTO(" NEW@example.com ", " Updated ", Role.ADMIN, null),
            "admin@example.com");

    assertEquals("new@example.com", response.getEmail());
    assertEquals("Updated", response.getFullName());
    assertEquals(Role.ADMIN, response.getRole());
    verify(products)
        .transferCreatedBy("old@example.com", "new@example.com", "admin@example.com");
    verify(categories)
        .transferCreatedBy("old@example.com", "new@example.com", "admin@example.com");
  }

  private AppUser user(String email, String name, String password) {
    return new AppUser(email, name, password, Role.USER);
  }
}
