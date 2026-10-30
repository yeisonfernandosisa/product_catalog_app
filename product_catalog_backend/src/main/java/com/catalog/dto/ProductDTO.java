package com.catalog.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Set;

public final class ProductDTO {
  private ProductDTO() {}

  public static class ProductRequestDTO {
    @NotBlank private String name;
    private String description;
    @NotNull @DecimalMin(value = "0.0", inclusive = false) private BigDecimal price;
    private Set<Long> categoryIds;

    public ProductRequestDTO() {}

    public ProductRequestDTO(
        String name, String description, BigDecimal price, Set<Long> categoryIds) {
      this.name = name;
      this.description = description;
      this.price = price;
      this.categoryIds = categoryIds;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public BigDecimal getPrice() {
      return price;
    }

    public void setPrice(BigDecimal price) {
      this.price = price;
    }

    public Set<Long> getCategoryIds() {
      return categoryIds;
    }

    public void setCategoryIds(Set<Long> categoryIds) {
      this.categoryIds = categoryIds;
    }
  }

  public static class ProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Set<CategoryDTO.CategoryResponseDTO> categories;

    public ProductResponseDTO() {}

    public ProductResponseDTO(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Set<CategoryDTO.CategoryResponseDTO> categories) {
      this.id = id;
      this.name = name;
      this.description = description;
      this.price = price;
      this.categories = categories;
    }

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public BigDecimal getPrice() {
      return price;
    }

    public void setPrice(BigDecimal price) {
      this.price = price;
    }

    public Set<CategoryDTO.CategoryResponseDTO> getCategories() {
      return categories;
    }

    public void setCategories(Set<CategoryDTO.CategoryResponseDTO> categories) {
      this.categories = categories;
    }
  }
}
