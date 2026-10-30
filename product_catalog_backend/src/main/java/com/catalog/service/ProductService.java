package com.catalog.service;

import com.catalog.dto.CatalogDtos.CategoryResponse;
import com.catalog.dto.CatalogDtos.ProductRequest;
import com.catalog.dto.CatalogDtos.ProductResponse;
import com.catalog.model.AppUser;
import com.catalog.model.Category;
import com.catalog.model.Product;
import com.catalog.repository.CategoryRepository;
import com.catalog.repository.ProductRepository;
import com.catalog.repository.UserRepository;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProductService {
  private final ProductRepository products;
  private final CategoryRepository categories;
  private final UserRepository users;

  public ProductService(
      ProductRepository products, CategoryRepository categories, UserRepository users) {
    this.products = products;
    this.categories = categories;
    this.users = users;
  }

  @Transactional(readOnly = true)
  public Page<ProductResponse> products(String email, Long categoryId, Pageable pageable) {
    String createdBy = requireUser(email).getEmail();
    Page<Product> result =
        categoryId == null
            ? products.findAllByCreatedBy(createdBy, pageable)
            : products.findAllByCreatedByAndProductCategories_Category_Id(
                createdBy, categoryId, pageable);

    return result.map(this::toResponse);
  }

  @Transactional(readOnly = true)
  public ProductResponse product(String email, Long id) {
    Product product = findOwnedProduct(email, id);
    return toResponse(product);
  }

  @Transactional
  public ProductResponse createProduct(String email, ProductRequest request) {
    String createdBy = requireUser(email).getEmail();
    Product product =
        products.saveAndFlush(
            new Product(request.name().trim(), request.description(), request.price(), createdBy));
    product.assignCategories(resolveCategories(request.categoryIds(), createdBy), email);

    return toResponse(products.save(product));
  }

  @Transactional
  public ProductResponse updateProduct(String email, Long id, ProductRequest request) {
    Product product = findOwnedProduct(email, id);
    product.setUpdatedBy(email);
    product.setName(request.name().trim());
    product.setDescription(request.description());
    product.setPrice(request.price());
    product.assignCategories(
        resolveCategories(request.categoryIds(), product.getCreatedBy()), email);

    return toResponse(product);
  }

  @Transactional
  public void deleteProduct(String email, Long id) {
    products.delete(findOwnedProduct(email, id));
  }

  private Product findOwnedProduct(String email, Long id) {
    String createdBy = requireUser(email).getEmail();
    return products
        .findById(id)
        .filter(product -> product.getCreatedBy().equals(createdBy))
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
  }

  private Set<Category> resolveCategories(Set<Long> ids, String createdBy) {
    if (ids == null || ids.isEmpty()) {
      return new LinkedHashSet<>();
    }

    Set<Category> result =
        new LinkedHashSet<>(categories.findAllByIdInAndCreatedBy(ids, createdBy));
    if (result.size() != ids.size()) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          "Una o varias categorías no existen o no pertenecen a este usuario");
    }
    return result;
  }

  private AppUser requireUser(String email) {
    return users
        .findByEmail(email)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sesión no válida"));
  }

  private ProductResponse toResponse(Product product) {
    Set<CategoryResponse> categoryResponses =
        product.getProductCategories().stream()
            .map(link -> link.getCategory())
            .map(category -> new CategoryResponse(category.getId(), category.getName()))
            .collect(Collectors.toCollection(LinkedHashSet::new));

    return new ProductResponse(
        product.getId(),
        product.getName(),
        product.getDescription(),
        product.getPrice(),
        categoryResponses);
  }
}
