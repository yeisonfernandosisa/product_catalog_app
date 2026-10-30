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
@RequestMapping("/api/products")
public class ProductController {
    private final CatalogService service;
    public ProductController(CatalogService service) { this.service = service; }
    @GetMapping public Page<ProductResponse> list(Authentication auth, @RequestParam(required = false) Long categoryId,
        @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return service.products(auth.getName(), categoryId, pageable);
    }
    @GetMapping("/{id}") public ProductResponse get(Authentication auth, @PathVariable Long id) {
        return service.product(auth.getName(), id);
    }
    @PostMapping public ProductResponse create(Authentication auth, @Valid @RequestBody ProductRequest request) {
        return service.createProduct(auth.getName(), request);
    }
    @PutMapping("/{id}") public ProductResponse update(Authentication auth, @PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return service.updateProduct(auth.getName(), id, request);
    }
    @DeleteMapping("/{id}") public void delete(Authentication auth, @PathVariable Long id) {
        service.deleteProduct(auth.getName(), id);
    }
}
