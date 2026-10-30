package com.catalog.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.catalog.dto.AuthDtos.UpdateUserRequest;
import com.catalog.model.AppUser;
import com.catalog.model.Role;
import com.catalog.repository.CategoryRepository;
import com.catalog.repository.ProductRepository;
import com.catalog.repository.UserRepository;
import com.catalog.security.JwtService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

class UserServiceTest {
  private UserRepository users;
  private ProductRepository products;
  private CategoryRepository categories;
  private UserService service;

  @BeforeEach
  void setUp() {
    users = mock(UserRepository.class);
    products = mock(ProductRepository.class);
    categories = mock(CategoryRepository.class);
    PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    JwtService jwtService = new JwtService("test-secret-key-with-at-least-32-characters");
    CatalogOwnershipService catalogOwnership = new CatalogOwnershipService(products, categories);
    service = new UserService(users, passwordEncoder, jwtService, catalogOwnership);
  }

  @Test
  void transfersCatalogOwnershipWhenAdminChangesUserEmail() {
    AppUser user = new AppUser("old@example.com", "User", "hash", Role.USER);
    when(users.findById(1L)).thenReturn(Optional.of(user));

    service.update(
        1L, new UpdateUserRequest("new@example.com", "User", Role.USER, null), "admin@example.com");

    verify(products).transferCreatedBy("old@example.com", "new@example.com", "admin@example.com");
    verify(categories).transferCreatedBy("old@example.com", "new@example.com", "admin@example.com");
  }

  @Test
  void doesNotTransferCatalogOwnershipWhenEmailIsUnchanged() {
    AppUser user = new AppUser("user@example.com", "User", "hash", Role.USER);
    when(users.findById(1L)).thenReturn(Optional.of(user));

    service.update(
        1L,
        new UpdateUserRequest("user@example.com", "User", Role.USER, null),
        "admin@example.com");

    verify(products, never())
        .transferCreatedBy("user@example.com", "user@example.com", "admin@example.com");
    verify(categories, never())
        .transferCreatedBy("user@example.com", "user@example.com", "admin@example.com");
  }
}
