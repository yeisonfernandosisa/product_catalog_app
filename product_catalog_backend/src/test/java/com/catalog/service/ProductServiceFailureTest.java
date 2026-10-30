package com.catalog.service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.catalog.dto.ProductDTO;
import com.catalog.model.AppUser;
import com.catalog.model.Product;
import com.catalog.model.Role;
import com.catalog.repository.CategoryRepository;
import com.catalog.repository.ProductRepository;
import com.catalog.repository.UserRepository;

class ProductServiceFailureTest {

    private static final String EMAIL = "user@example.com";
    private ProductRepository products;
    private CategoryRepository categories;
    private UserRepository users;
    private ProductService service;

    @BeforeEach
    void setUp() {
        products = mock(ProductRepository.class);
        categories = mock(CategoryRepository.class);
        users = mock(UserRepository.class);
        service = new ProductService(products, categories, users);
    }

    @Test
    void rejectsProductListWhenSessionUserDoesNotExist() {
        assertStatus(
                HttpStatus.UNAUTHORIZED,
                () -> service.products(EMAIL, null, PageRequest.of(0, 10)));
    }

    @Test
    void rejectsProductLookupWhenProductDoesNotExist() {
        when(users.findByEmail(EMAIL)).thenReturn(Optional.of(user()));
        when(products.findById(9L)).thenReturn(Optional.empty());

        assertStatus(HttpStatus.NOT_FOUND, () -> service.product(EMAIL, 9L));
    }

    @Test
    void rejectsProductCreationWhenCategoryIsNotOwned() {
        when(users.findByEmail(EMAIL)).thenReturn(Optional.of(user()));
        when(categories.findAllByIdInAndCreatedBy(Set.of(3L), EMAIL)).thenReturn(Set.of());

        assertStatus(
                HttpStatus.NOT_FOUND,
                ()
                -> service.createProduct(
                        EMAIL, new ProductDTO("Widget", "desc", BigDecimal.TEN, Set.of(3L))));
        verify(products).saveAndFlush(any(Product.class));
        verify(products, never()).save(any(Product.class));
    }

    @Test
    void rejectsProductUpdateWhenProductIsNotOwned() {
        when(users.findByEmail(EMAIL)).thenReturn(Optional.of(user()));
        when(products.findById(9L)).thenReturn(Optional.empty());

        assertStatus(
                HttpStatus.NOT_FOUND,
                ()
                -> service.updateProduct(
                        EMAIL, 9L, new ProductDTO("Widget", "desc", BigDecimal.TEN, null)));
    }

    @Test
    void rejectsProductDeletionWhenProductIsNotOwned() {
        when(users.findByEmail(EMAIL)).thenReturn(Optional.of(user()));
        when(products.findById(9L)).thenReturn(Optional.empty());

        assertStatus(HttpStatus.NOT_FOUND, () -> service.deleteProduct(EMAIL, 9L));
        verify(products, never()).delete(any(Product.class));
    }

    private AppUser user() {
        return new AppUser(EMAIL, "User", "hash", Role.USER);
    }

    private void assertStatus(HttpStatus status, org.junit.jupiter.api.function.Executable action) {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, action);
        assertEquals(status, exception.getStatusCode());
    }
}
