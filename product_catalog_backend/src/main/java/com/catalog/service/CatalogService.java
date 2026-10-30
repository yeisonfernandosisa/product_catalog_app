package com.catalog.service;

import com.catalog.dto.CatalogDtos.*;
import com.catalog.model.AppUser;
import com.catalog.model.Category;
import com.catalog.model.Product;
import com.catalog.repository.ProductCategoryRepository;
import com.catalog.repository.CategoryRepository;
import com.catalog.repository.ProductRepository;
import com.catalog.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.LinkedHashSet;
import java.util.Set;

@Service
public class CatalogService {
    private final ProductRepository products;
    private final CategoryRepository categories;
    private final ProductCategoryRepository productCategories;
    private final UserRepository users;
    public CatalogService(ProductRepository products, CategoryRepository categories,
                          ProductCategoryRepository productCategories, UserRepository users) {
        this.products = products; this.categories = categories;
        this.productCategories = productCategories; this.users = users;
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> products(String email, Long categoryId, Pageable pageable) {
        String createdBy = owner(email).getEmail();
        Page<Product> result = categoryId == null
            ? products.findAllByCreatedBy(createdBy, pageable)
            : products.findAllByCreatedByAndProductCategories_Category_Id(createdBy, categoryId, pageable);
        return result.map(this::productResponse);
    }
    @Transactional(readOnly = true)
    public ProductResponse product(String email, Long id) {
        Product product = products.findById(id).filter(p -> p.getCreatedBy().equals(owner(email).getEmail()))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
        return productResponse(product);
    }
    @Transactional
    public ProductResponse createProduct(String email, ProductRequest request) {
        String createdBy = owner(email).getEmail();
        Product product = products.saveAndFlush(new Product(request.name().trim(), request.description(),
            request.price(), createdBy));
        product.assignCategories(resolveCategories(request.categoryIds(), createdBy), email);
        return productResponse(products.save(product));
    }
    @Transactional
    public ProductResponse updateProduct(String email, Long id, ProductRequest request) {
        Product product = products.findById(id).filter(p -> p.getCreatedBy().equals(owner(email).getEmail()))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
        product.setUpdatedBy(email);
        product.setName(request.name().trim());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.assignCategories(resolveCategories(request.categoryIds(), product.getCreatedBy()), email);
        return productResponse(product);
    }
    @Transactional
    public void deleteProduct(String email, Long id) {
        Product product = products.findById(id).filter(p -> p.getCreatedBy().equals(owner(email).getEmail()))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
        products.delete(product);
    }
    @Transactional(readOnly = true)
    public Page<CategoryResponse> categories(String email, Pageable pageable) {
        String createdBy = owner(email).getEmail();
        return categories.findAllByCreatedBy(createdBy, pageable).map(this::categoryResponse);
    }
    @Transactional(readOnly = true)
    public CategoryResponse category(String email, Long id) {
        Category category = categories.findById(id).filter(c -> c.getCreatedBy().equals(owner(email).getEmail()))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada"));
        return categoryResponse(category);
    }
    @Transactional
    public CategoryResponse createCategory(String email, CategoryRequest request) {
        String createdBy = owner(email).getEmail();
        String name = request.name().trim();
        if (categories.existsByNameIgnoreCaseAndCreatedBy(name, createdBy))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una categoría con ese nombre");
        return categoryResponse(categories.save(new Category(name, createdBy)));
    }
    @Transactional
    public CategoryResponse updateCategory(String email, Long id, CategoryRequest request) {
        Category category = categories.findById(id).filter(c -> c.getCreatedBy().equals(owner(email).getEmail()))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada"));
        String name = request.name().trim();
        if (!category.getName().equalsIgnoreCase(name)
            && categories.existsByNameIgnoreCaseAndCreatedBy(name, category.getCreatedBy()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una categoría con ese nombre");
        category.setUpdatedBy(email);
        category.setName(name);
        return categoryResponse(category);
    }

    @Transactional
    public void deleteCategory(String email, Long id) {
        Category category = categories.findById(id).filter(c -> c.getCreatedBy().equals(owner(email).getEmail()))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada"));
        if (productCategories.existsForCategory(id))
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "No se puede eliminar la categoría porque tiene productos asociados");
        categories.delete(category);
    }
    private AppUser owner(String email) {
        return users.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sesión no válida"));
    }
    private Set<Category> resolveCategories(Set<Long> ids, String createdBy) {
        if (ids == null || ids.isEmpty()) return new LinkedHashSet<>();
        Set<Category> result = new LinkedHashSet<>(categories.findAllByIdInAndCreatedBy(ids, createdBy));
        if (result.size() != ids.size())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Una o varias categorías no existen o no pertenecen a este usuario");
        return result;
    }
    private ProductResponse productResponse(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getDescription(), product.getPrice(),
            product.getProductCategories().stream().map(link -> categoryResponse(link.getCategory()))
                .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new)));
    }
    private CategoryResponse categoryResponse(Category category) { return new CategoryResponse(category.getId(), category.getName()); }
}
