package com.catalog.service;

import com.catalog.dto.CatalogDtos.ProductRequest;
import com.catalog.model.AppUser;
import com.catalog.model.Category;
import com.catalog.model.Role;
import com.catalog.repository.CategoryRepository;
import com.catalog.repository.ProductCategoryRepository;
import com.catalog.repository.ProductRepository;
import com.catalog.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.util.Set;
import java.util.Optional;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CatalogServiceTest {
    private ProductRepository products;
    private CategoryRepository categories;
    private ProductCategoryRepository productCategories;
    private UserRepository users;
    private CatalogService service;

    @BeforeEach
    void setUp() {
        products = mock(ProductRepository.class);
        categories = mock(CategoryRepository.class);
        productCategories = mock(ProductCategoryRepository.class);
        users = mock(UserRepository.class);
        service = new CatalogService(products, categories, productCategories, users);
    }

    @Test
    void refusesToAssociateCategoriesOwnedByAnotherUser() throws ReflectiveOperationException {
        AppUser owner = new AppUser("user@example.com", "User", "hash", Role.USER);
        Field id = AppUser.class.getDeclaredField("id");
        id.setAccessible(true);
        id.set(owner, 7L);
        when(users.findByEmail(owner.getEmail())).thenReturn(Optional.of(owner));
        when(categories.findAllByIdInAndCreatedBy(Set.of(4L), owner.getEmail())).thenReturn(Set.of());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> service.createProduct(owner.getEmail(), new ProductRequest("Item", "", BigDecimal.TEN, Set.of(4L))));

        assertEquals(404, exception.getStatusCode().value());
        verify(products, never()).save(any());
    }

    @Test
    void refusesToDeleteCategoryWhenProductsAreAssociated() throws ReflectiveOperationException {
        AppUser owner = new AppUser("user@example.com", "User", "hash", Role.USER);
        Field id = AppUser.class.getDeclaredField("id");
        id.setAccessible(true);
        id.set(owner, 7L);
        Category category = new Category("Category", owner.getEmail());
        Field categoryId = Category.class.getDeclaredField("id");
        categoryId.setAccessible(true);
        categoryId.set(category, 4L);
        when(users.findByEmail(owner.getEmail())).thenReturn(Optional.of(owner));
        when(categories.findById(4L)).thenReturn(Optional.of(category));
        when(productCategories.existsForCategory(4L)).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> service.deleteCategory(owner.getEmail(), 4L));

        assertEquals(409, exception.getStatusCode().value());
        verify(categories, never()).delete(any(Category.class));
    }
}
