package com.catalog.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.catalog.repository.CategoryRepository;
import com.catalog.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatalogOwnershipServiceTest {
  private ProductRepository products;
  private CategoryRepository categories;
  private CatalogOwnershipService service;

  @BeforeEach
  void setUp() {
    products = mock(ProductRepository.class);
    categories = mock(CategoryRepository.class);
    service = new CatalogOwnershipService(products, categories);
  }

  @Test
  void transfersProductsAndCategoriesWhenEmailChanges() {
    service.transferToNewEmail("old@example.com", "new@example.com", "admin@example.com");

    verify(products).transferCreatedBy("old@example.com", "new@example.com", "admin@example.com");
    verify(categories).transferCreatedBy("old@example.com", "new@example.com", "admin@example.com");
  }

  @Test
  void skipsTransferWhenEmailIsUnchanged() {
    service.transferToNewEmail("user@example.com", "user@example.com", "admin@example.com");

    verify(products, never())
        .transferCreatedBy("user@example.com", "user@example.com", "admin@example.com");
    verify(categories, never())
        .transferCreatedBy("user@example.com", "user@example.com", "admin@example.com");
  }
}
