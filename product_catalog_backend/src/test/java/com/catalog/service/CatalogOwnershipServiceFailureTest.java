package com.catalog.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.catalog.repository.CategoryRepository;
import com.catalog.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatalogOwnershipServiceFailureTest {
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
  void propagatesFailureWhenProductOwnershipCannotBeTransferred() {
    when(products.transferCreatedBy("old@example.com", "new@example.com", "admin@example.com"))
        .thenThrow(new IllegalStateException("Product transfer failed"));

    assertThrows(
        IllegalStateException.class,
        () -> service.transferToNewEmail("old@example.com", "new@example.com", "admin@example.com"));
  }
}
