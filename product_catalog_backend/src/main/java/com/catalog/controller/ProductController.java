package com.catalog.controller;

import com.catalog.dto.ProductDTO.ProductRequestDTO;
import com.catalog.dto.ProductDTO.ProductResponseDTO;
import com.catalog.service.ProductService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {
  private final ProductService service;

  public ProductController(ProductService service) {
    this.service = service;
  }

  @GetMapping
  public Page<ProductResponseDTO> list(
      Authentication auth,
      @RequestParam(required = false) Long categoryId,
      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable pageable) {
    return service.products(auth.getName(), categoryId, pageable);
  }

  @GetMapping("/{id}")
  public ProductResponseDTO get(Authentication auth, @PathVariable Long id) {
    return service.product(auth.getName(), id);
  }

  @PostMapping
  public ProductResponseDTO create(
      Authentication auth, @Valid @RequestBody ProductRequestDTO request) {
    return service.createProduct(auth.getName(), request);
  }

  @PutMapping("/{id}")
  public ProductResponseDTO update(
      Authentication auth, @PathVariable Long id, @Valid @RequestBody ProductRequestDTO request) {
    return service.updateProduct(auth.getName(), id, request);
  }

  @DeleteMapping("/{id}")
  public void delete(Authentication auth, @PathVariable Long id) {
    service.deleteProduct(auth.getName(), id);
  }
}
