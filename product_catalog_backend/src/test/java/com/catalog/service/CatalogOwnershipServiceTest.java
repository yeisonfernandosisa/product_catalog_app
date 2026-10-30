package com.catalog.service;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.catalog.repository.CategoryRepository;
import com.catalog.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class CatalogOwnershipServiceTest {

    @Mock private ProductRepository products;

    @Mock private CategoryRepository categories;

    @InjectMocks private CatalogOwnershipService service;

    @Test
    void transfersProductsAndCategoriesToTheNewOwner() {
        service.transferToNewEmail("old@example.com", "new@example.com", "admin@example.com");

        verify(products).transferCreatedBy("old@example.com", "new@example.com", "admin@example.com");
        verify(categories)
                .transferCreatedBy("old@example.com", "new@example.com", "admin@example.com");
    }
}
