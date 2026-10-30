package com.catalog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.catalog.dto.CategoryDTO;
import com.catalog.model.AppUser;
import com.catalog.model.Category;
import com.catalog.model.Role;
import com.catalog.repository.CategoryRepository;
import com.catalog.repository.ProductCategoryRepository;
import com.catalog.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    private static final String EMAIL = "user@example.com";

    @Mock private CategoryRepository categories;

    @Mock private ProductCategoryRepository productCategories;

    @Mock private UserRepository users;

    @Test
    void createsCategoryAndRemovesExtraSpacesFromName() {
        CategoryService service = new CategoryService(categories, productCategories, users);

        when(users.findByEmail(EMAIL))
                .thenReturn(Optional.of(new AppUser(EMAIL, "Test user", "password", Role.USER)));
        when(categories.existsByNameIgnoreCaseAndCreatedBy("Office", EMAIL)).thenReturn(false);
        when(categories.save(any(Category.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CategoryDTO result = service.createCategory(EMAIL, new CategoryDTO(null, "  Office  "));

        assertEquals("Office", result.getName());
    }
}
