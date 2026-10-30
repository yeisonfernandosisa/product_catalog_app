package com.catalog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.catalog.dto.AuthResponseDTO;
import com.catalog.dto.RegisterRequestDTO;
import com.catalog.model.AppUser;
import com.catalog.repository.CategoryRepository;
import com.catalog.repository.ProductRepository;
import com.catalog.repository.UserRepository;
import com.catalog.security.JwtService;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository users;

    @Mock private PasswordEncoder encoder;

    @Mock private ProductRepository products;

    @Mock private CategoryRepository categories;

    @Test
    void registersUserWithNormalizedEmail() {
        when(users.existsByEmail("user@example.com")).thenReturn(false);
        when(encoder.encode("password123")).thenReturn("encoded-password");
        when(users.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));
        UserService service =
                new UserService(
                        users,
                        encoder,
                        new JwtService("test-secret-key-with-at-least-32-characters"),
                        new CatalogOwnershipService(products, categories));
        AuthResponseDTO result =
                service.register(
                        new RegisterRequestDTO(" USER@EXAMPLE.COM ", "password123", "Test User"));

        assertEquals("user@example.com", result.getEmail());
        assertEquals("Test User", result.getFullName());
        assertEquals(3, result.getToken().split("\\.").length);
    }
}
