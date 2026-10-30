package com.catalog.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Set;

public final class CatalogDtos {
    private CatalogDtos() {}
    public record CategoryRequest(@NotBlank String name) {}

    public record CategoryResponse(Long id, String name) {}
    
    public record ProductRequest(@NotBlank String name, String description,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal price,
        Set<Long> categoryIds) {}
    
    public record ProductResponse(Long id, String name, String description, BigDecimal price,
                                  Set<CategoryResponse> categories) {}
}
