package com.catalog.service;

import com.catalog.dto.CatalogDtos.CategoryRequest;
import com.catalog.dto.CatalogDtos.CategoryResponse;
import com.catalog.model.AppUser;
import com.catalog.model.Category;
import com.catalog.repository.CategoryRepository;
import com.catalog.repository.ProductCategoryRepository;
import com.catalog.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CategoryService {
  private final CategoryRepository categories;
  private final ProductCategoryRepository productCategories;
  private final UserRepository users;

  public CategoryService(
      CategoryRepository categories,
      ProductCategoryRepository productCategories,
      UserRepository users) {
    this.categories = categories;
    this.productCategories = productCategories;
    this.users = users;
  }

  @Transactional(readOnly = true)
  public Page<CategoryResponse> categories(String email, Pageable pageable) {
    String createdBy = requireUser(email).getEmail();
    return categories.findAllByCreatedBy(createdBy, pageable).map(this::toResponse);
  }

  @Transactional(readOnly = true)
  public CategoryResponse category(String email, Long id) {
    return toResponse(findOwnedCategory(email, id));
  }

  @Transactional
  public CategoryResponse createCategory(String email, CategoryRequest request) {
    String createdBy = requireUser(email).getEmail();
    String name = request.name().trim();
    if (categories.existsByNameIgnoreCaseAndCreatedBy(name, createdBy)) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "Ya existe una categoría con ese nombre");
    }

    return toResponse(categories.save(new Category(name, createdBy)));
  }

  @Transactional
  public CategoryResponse updateCategory(String email, Long id, CategoryRequest request) {
    Category category = findOwnedCategory(email, id);
    String name = request.name().trim();
    if (!category.getName().equalsIgnoreCase(name)
        && categories.existsByNameIgnoreCaseAndCreatedBy(name, category.getCreatedBy())) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "Ya existe una categoría con ese nombre");
    }

    category.setUpdatedBy(email);
    category.setName(name);
    return toResponse(category);
  }

  @Transactional
  public void deleteCategory(String email, Long id) {
    Category category = findOwnedCategory(email, id);
    if (productCategories.existsForCategory(id)) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT,
          "No se puede eliminar la categoría porque tiene productos asociados");
    }

    categories.delete(category);
  }

  private Category findOwnedCategory(String email, Long id) {
    String createdBy = requireUser(email).getEmail();
    return categories
        .findById(id)
        .filter(category -> category.getCreatedBy().equals(createdBy))
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada"));
  }

  private AppUser requireUser(String email) {
    return users
        .findByEmail(email)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sesión no válida"));
  }

  private CategoryResponse toResponse(Category category) {
    return new CategoryResponse(category.getId(), category.getName());
  }
}
