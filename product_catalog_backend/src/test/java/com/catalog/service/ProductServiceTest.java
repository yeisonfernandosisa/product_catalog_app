package com.catalog.service;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.catalog.dto.ProductDTO;
import com.catalog.model.AppUser;
import com.catalog.model.Category;
import com.catalog.model.Product;
import com.catalog.model.ProductCategory;
import com.catalog.model.Role;
import com.catalog.repository.CategoryRepository;
import com.catalog.repository.ProductRepository;
import com.catalog.repository.UserRepository;

class ProductServiceTest {

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
        when(users.findByEmail(EMAIL)).thenReturn(Optional.of(user()));
    }

    @Test
    void listsProductsForUserAndOptionalCategory() {
        PageRequest pageable = PageRequest.of(0, 10);
        Product product = product();
        when(products.findAllByCreatedBy(EMAIL, pageable))
                .thenReturn(new PageImpl<>(List.of(product), pageable, 1));
        when(products.findAllByCreatedByAndProductCategories_Category_Id(EMAIL, 3L, pageable))
                .thenReturn(new PageImpl<>(List.of(product), pageable, 1));

        assertEquals("Widget", service.products(EMAIL, null, pageable).getContent().get(0).getName());
        assertEquals(
                "Widget", service.products(EMAIL, 3L, pageable).getContent().get(0).getName());
    }

    @Test
    void returnsOwnedProductWithCategories() throws Exception {
        Product product = product();
        Category category = new Category("Tools", EMAIL);
        setId(category, 3L);
        product.getProductCategories().add(new ProductCategory(product, category, EMAIL));
        setId(product, 8L);
        when(products.findById(8L)).thenReturn(Optional.of(product));

        ProductDTO response = service.product(EMAIL, 8L);

        assertEquals(8L, response.getId());
        assertEquals("Widget", response.getName());
        assertEquals(Set.of(3L), response.getCategories().stream().map(c -> c.getId()).collect(java.util.stream.Collectors.toSet()));
    }

    @Test
    void createsProductWithoutCategories() {
        Product product = product();
        when(products.saveAndFlush(any(Product.class))).thenReturn(product);
        when(products.save(any(Product.class))).thenReturn(product);

        ProductDTO response
                = service.createProduct(EMAIL, new ProductDTO(" Widget ", "desc", BigDecimal.TEN, null));

        assertEquals("Widget", response.getName());
        assertEquals(BigDecimal.TEN, response.getPrice());
        assertNotNull(response.getCategories());
    }

    @Test
    void updatesOwnedProduct() throws Exception {
        Product product = product();
        setId(product, 8L);
        when(products.findById(8L)).thenReturn(Optional.of(product));

        ProductDTO response
                = service.updateProduct(EMAIL, 8L, new ProductDTO(" Updated ", "new", BigDecimal.ONE, null));

        assertEquals("Updated", response.getName());
        assertEquals("new", response.getDescription());
        assertEquals(BigDecimal.ONE, response.getPrice());
    }

    @Test
    void deletesOwnedProduct() throws Exception {
        Product product = product();
        setId(product, 8L);
        when(products.findById(8L)).thenReturn(Optional.of(product));

        service.deleteProduct(EMAIL, 8L);

        org.mockito.Mockito.verify(products).delete(product);
    }

    private AppUser user() {
        return new AppUser(EMAIL, "User", "hash", Role.USER);
    }

    private Product product() {
        return new Product("Widget", "desc", BigDecimal.TEN, EMAIL);
    }

    private void setId(Object entity, Long id) throws Exception {
        Field field = entity.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(entity, id);
    }
}
