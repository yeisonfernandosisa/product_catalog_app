package com.catalog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.catalog.dto.ProductDTO;
import com.catalog.model.AppUser;
import com.catalog.model.Product;
import com.catalog.model.Role;
import com.catalog.repository.CategoryRepository;
import com.catalog.repository.ProductRepository;
import com.catalog.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    private static final String EMAIL = "user@example.com";

    @Mock private ProductRepository products;

    @Mock private CategoryRepository categories;

    @Mock private UserRepository users;

    @InjectMocks private ProductService service;

    @Test
    void createsProductAndRemovesExtraSpacesFromName() {
        givenSignedInUser();
        when(products.saveAndFlush(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(products.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductDTO result =
                service.createProduct(
                        EMAIL, new ProductDTO("  Desk lamp  ", "A small lamp", BigDecimal.TEN, null));

        assertEquals("Desk lamp", result.getName());
        assertEquals(BigDecimal.TEN, result.getPrice());
        assertEquals(0, result.getCategories().size());
        verify(products).saveAndFlush(any(Product.class));
    }

    @Test
    void listsProductsOwnedByTheSignedInUser() {
        givenSignedInUser();
        PageRequest pageRequest = PageRequest.of(0, 10);
        Product product = new Product("Desk lamp", "A small lamp", BigDecimal.TEN, EMAIL);
        when(products.findAllByCreatedBy(EMAIL, pageRequest))
                .thenReturn(new PageImpl<>(List.of(product), pageRequest, 1));

        ProductDTO result = service.products(EMAIL, null, pageRequest).getContent().get(0);

        assertEquals("Desk lamp", result.getName());
        assertEquals(BigDecimal.TEN, result.getPrice());
        verify(products).findAllByCreatedBy(EMAIL, pageRequest);
    }

    private void givenSignedInUser() {
        when(users.findByEmail(EMAIL))
                .thenReturn(Optional.of(new AppUser(EMAIL, "Test user", "password", Role.USER)));
    }
}
