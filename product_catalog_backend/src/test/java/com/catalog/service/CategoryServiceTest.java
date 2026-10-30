package com.catalog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.catalog.dto.CategoryDTO;
import com.catalog.model.AppUser;
import com.catalog.model.Category;
import com.catalog.model.Role;
import com.catalog.repository.CategoryRepository;
import com.catalog.repository.ProductCategoryRepository;
import com.catalog.repository.UserRepository;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

class CategoryServiceTest {
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
    when(users.findByEmail(EMAIL)).thenReturn(Optional.of(user()));
  }

  @Test
  void listsCategoriesOwnedByUser() {
    PageRequest pageable = PageRequest.of(0, 10);
    when(categories.findAllByCreatedBy(EMAIL, pageable))
        .thenReturn(new PageImpl<>(List.of(new Category("Tools", EMAIL)), pageable, 1));

    assertEquals("Tools", service.categories(EMAIL, pageable).getContent().get(0).getName());
  }

  @Test
  void returnsOwnedCategory() throws Exception {
    Category category = category(4L, "Tools");
    when(categories.findById(4L)).thenReturn(Optional.of(category));

    CategoryDTO response = service.category(EMAIL, 4L);

    assertEquals(4L, response.getId());
    assertEquals("Tools", response.getName());
  }

  @Test
  void createsCategoryWithNormalizedName() {
    when(categories.existsByNameIgnoreCaseAndCreatedBy("Tools", EMAIL)).thenReturn(false);
    when(categories.save(any(Category.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    CategoryDTO response = service.createCategory(EMAIL, new CategoryDTO(null, " Tools "));

    assertEquals("Tools", response.getName());
    verify(categories).save(any(Category.class));
  }

  @Test
  void updatesOwnedCategory() throws Exception {
    Category category = category(4L, "Tools");
    when(categories.findById(4L)).thenReturn(Optional.of(category));
    when(categories.existsByNameIgnoreCaseAndCreatedBy("Hardware", EMAIL)).thenReturn(false);

    CategoryDTO response = service.updateCategory(EMAIL, 4L, new CategoryDTO(null, " Hardware "));

    assertEquals("Hardware", response.getName());
    assertEquals(EMAIL, category.getUpdatedBy());
  }

  @Test
  void deletesCategoryWithoutAssociatedProducts() throws Exception {
    Category category = category(4L, "Tools");
    when(categories.findById(4L)).thenReturn(Optional.of(category));
    when(productCategories.existsForCategory(4L)).thenReturn(false);

    service.deleteCategory(EMAIL, 4L);

    verify(categories).delete(category);
  }

  private AppUser user() {
    return new AppUser(EMAIL, "User", "hash", Role.USER);
  }

  private Category category(Long id, String name) throws Exception {
    Category category = new Category(name, EMAIL);
    Field field = Category.class.getDeclaredField("id");
    field.setAccessible(true);
    field.set(category, id);
    return category;
  }
}
