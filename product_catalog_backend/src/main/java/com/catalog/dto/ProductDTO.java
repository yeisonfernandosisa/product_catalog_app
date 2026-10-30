package com.catalog.dto;

import java.math.BigDecimal;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ProductDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "no puede estar vacío")
    private String name;

    private String description;

    @NotNull(message = "es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "debe ser mayor que cero")
    private BigDecimal price;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Set<Long> categoryIds;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Set<CategoryDTO> categories;

    public ProductDTO() {
    }

    public ProductDTO(String name, String description, BigDecimal price, Set<Long> categoryIds) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.categoryIds = categoryIds;
    }

    public ProductDTO(
            Long id, String name, String description, BigDecimal price, Set<CategoryDTO> categories) {
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

    public Set<Long> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(Set<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public Set<CategoryDTO> getCategories() {
        return categories;
    }

    public void setCategories(Set<CategoryDTO> categories) {
        this.categories = categories;
    }
}
