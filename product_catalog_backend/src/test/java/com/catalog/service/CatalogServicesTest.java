package com.catalog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.catalog.dto.ProductDTO;
import com.catalog.model.AppUser;
import com.catalog.model.Category;
import com.catalog.model.Role;
import com.catalog.repository.CategoryRepository;
import com.catalog.repository.ProductCategoryRepository;
import com.catalog.repository.ProductRepository;
import com.catalog.repository.UserRepository;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

class CatalogServicesTest {
  private ProductRepository products;
  private CategoryRepository categories;
  private ProductCategoryRepository productCategories;
  private UserRepository users;
  private ProductService productService;
  private CategoryService categoryService;

  @BeforeEach
  void setUp() {
    products = mock(ProductRepository.class);
    categories = mock(CategoryRepository.class);
    productCategories = mock(ProductCategoryRepository.class);
    users = mock(UserRepository.class);
    productService = new ProductService(products, categories, users);
    categoryService = new CategoryService(categories, productCategories, users);
  }

  @Test
  void refusesToAssociateCategoriesCreatedByAnotherUser() {
    AppUser user = user("user@example.com");
    when(users.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    when(categories.findAllByIdInAndCreatedBy(Set.of(4L), user.getEmail())).thenReturn(Set.of());

    ResponseStatusException exception =
        assertThrows(
            ResponseStatusException.class,
            () ->
                productService.createProduct(
                    user.getEmail(), new ProductDTO("Item", "", BigDecimal.TEN, Set.of(4L))));

    assertEquals(404, exception.getStatusCode().value());
    verify(products, never()).save(any());
  }

  @Test
  void refusesToDeleteCategoryWhenProductsAreAssociated() throws ReflectiveOperationException {
    AppUser user = user("user@example.com");
    Category category = new Category("Category", user.getEmail());
    setId(category, 4L);
    when(users.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    when(categories.findById(4L)).thenReturn(Optional.of(category));
    when(productCategories.existsForCategory(4L)).thenReturn(true);

    ResponseStatusException exception =
        assertThrows(
            ResponseStatusException.class,
            () -> categoryService.deleteCategory(user.getEmail(), 4L));

    assertEquals(409, exception.getStatusCode().value());
    verify(categories, never()).delete(any(Category.class));
  }

  private AppUser user(String email) {
    return new AppUser(email, "User", "hash", Role.USER);
  }

  private void setId(Category category, Long id) throws ReflectiveOperationException {
    Field field = Category.class.getDeclaredField("id");
    field.setAccessible(true);
    field.set(category, id);
  }
}
