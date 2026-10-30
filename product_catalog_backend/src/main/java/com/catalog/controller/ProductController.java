package com.catalog.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.catalog.dto.ProductDTO;
import com.catalog.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public Page<ProductDTO> list(
            Authentication auth,
            @RequestParam(required = false) Long categoryId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.products(auth.getName(), categoryId, pageable);
    }

    @GetMapping("/{id}")
    public ProductDTO get(Authentication auth, @PathVariable Long id) {
        return service.product(auth.getName(), id);
    }

    @PostMapping
    public ProductDTO create(Authentication auth, @Valid @RequestBody ProductDTO request) {
        return service.createProduct(auth.getName(), request);
    }

    @PutMapping("/{id}")
    public ProductDTO update(
            Authentication auth, @PathVariable Long id, @Valid @RequestBody ProductDTO request) {
        return service.updateProduct(auth.getName(), id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(Authentication auth, @PathVariable Long id) {
        service.deleteProduct(auth.getName(), id);
    }
}
