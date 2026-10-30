package com.catalog.controller;

import com.catalog.dto.CatalogDtos.*;
import com.catalog.service.CatalogService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CatalogService service;
    public CategoryController(CatalogService service) { this.service = service; }
    @GetMapping public Page<CategoryResponse> list(Authentication auth,
        @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return service.categories(auth.getName(), pageable);
    }
    @GetMapping("/{id}") public CategoryResponse get(Authentication auth, @PathVariable Long id) {
        return service.category(auth.getName(), id);
    }
    @PostMapping public CategoryResponse create(Authentication auth, @Valid @RequestBody CategoryRequest request) {
        return service.createCategory(auth.getName(), request);
    }
    @PutMapping("/{id}") public CategoryResponse update(Authentication auth, @PathVariable Long id,
        @Valid @RequestBody CategoryRequest request) {
        return service.updateCategory(auth.getName(), id, request);
    }
    @DeleteMapping("/{id}") public void delete(Authentication auth, @PathVariable Long id) {
        service.deleteCategory(auth.getName(), id);
    }
}
