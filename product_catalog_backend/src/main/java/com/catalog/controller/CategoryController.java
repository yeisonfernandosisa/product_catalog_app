package com.catalog.controller;

import com.catalog.dto.CategoryDTO;
import com.catalog.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
  private final CategoryService service;

  public CategoryController(CategoryService service) {
    this.service = service;
  }

  @GetMapping
  public Page<CategoryDTO> list(
      Authentication auth,
      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable pageable) {
    return service.categories(auth.getName(), pageable);
  }

  @GetMapping("/{id}")
  public CategoryDTO get(Authentication auth, @PathVariable Long id) {
    return service.category(auth.getName(), id);
  }

  @PostMapping
  public CategoryDTO create(Authentication auth, @Valid @RequestBody CategoryDTO request) {
    return service.createCategory(auth.getName(), request);
  }

  @PutMapping("/{id}")
  public CategoryDTO update(
      Authentication auth, @PathVariable Long id, @Valid @RequestBody CategoryDTO request) {
    return service.updateCategory(auth.getName(), id, request);
  }

  @DeleteMapping("/{id}")
  public void delete(Authentication auth, @PathVariable Long id) {
    service.deleteCategory(auth.getName(), id);
  }
}
