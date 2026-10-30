package com.catalog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.catalog.dto.CategoryDTO;
import com.catalog.model.Category;
import com.catalog.repository.CategoryRepository;
import com.catalog.repository.ProductCategoryRepository;
import com.catalog.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class CategoryServiceFailureTest {
  private static final String EMAIL = "user@example.com";
  private CategoryRepository categories;
  private ProductCategoryRepository productCategories;
  private UserRepository users;
  private CategoryService service;

  @BeforeEach
  void setUp() {
    categories = mock(CategoryRepository.class);
    productCategories = mock(ProductCategoryRepository.class);
    users = mock(UserRepository.class);
    service = new CategoryService(categories, productCategories, users);
  }

  @Test
  void rejectsCategoryListWhenSessionUserDoesNotExist() {
    assertStatus(
        HttpStatus.UNAUTHORIZED,
        () -> service.categories(EMAIL, PageRequest.of(0, 10)));
  }

  @Test
  void rejectsCategoryLookupWhenCategoryDoesNotExist() {
    when(users.findByEmail(EMAIL)).thenReturn(Optional.of(user()));
    when(categories.findById(4L)).thenReturn(Optional.empty());

    assertStatus(HttpStatus.NOT_FOUND, () -> service.category(EMAIL, 4L));
  }

  @Test
  void rejectsDuplicateCategoryCreation() {
    when(users.findByEmail(EMAIL)).thenReturn(Optional.of(user()));
    when(categories.existsByNameIgnoreCaseAndCreatedBy("Tools", EMAIL)).thenReturn(true);

    assertStatus(
        HttpStatus.CONFLICT,
        () -> service.createCategory(EMAIL, new CategoryDTO(null, "Tools")));
    verify(categories, never()).save(any(Category.class));
  }

  @Test
  void rejectsCategoryUpdateWhenNameIsAlreadyUsed() throws Exception {
    Category category = new Category("Hardware", EMAIL);
    setId(category, 4L);
    when(users.findByEmail(EMAIL)).thenReturn(Optional.of(user()));
    when(categories.findById(4L)).thenReturn(Optional.of(category));
    when(categories.existsByNameIgnoreCaseAndCreatedBy("Tools", EMAIL)).thenReturn(true);

    assertStatus(
        HttpStatus.CONFLICT,
        () -> service.updateCategory(EMAIL, 4L, new CategoryDTO(null, "Tools")));
  }

  @Test
  void rejectsCategoryDeletionWhenProductsAreAssociated() throws Exception {
    Category category = new Category("Tools", EMAIL);
    setId(category, 4L);
    when(users.findByEmail(EMAIL)).thenReturn(Optional.of(user()));
    when(categories.findById(4L)).thenReturn(Optional.of(category));
    when(productCategories.existsForCategory(4L)).thenReturn(true);

    assertStatus(HttpStatus.CONFLICT, () -> service.deleteCategory(EMAIL, 4L));
    verify(categories, never()).delete(any(Category.class));
  }

  private com.catalog.model.AppUser user() {
    return new com.catalog.model.AppUser(EMAIL, "User", "hash", com.catalog.model.Role.USER);
  }

  private void assertStatus(HttpStatus status, org.junit.jupiter.api.function.Executable action) {
    ResponseStatusException exception = assertThrows(ResponseStatusException.class, action);
    assertEquals(status, exception.getStatusCode());
  }

  private void setId(Category category, Long id) throws Exception {
    var field = Category.class.getDeclaredField("id");
    field.setAccessible(true);
    field.set(category, id);
  }
}
