package com.catalog.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ProductCategoryId implements Serializable {
    @Column(name = "product_id")
    private Long productId;
    @Column(name = "category_id")
    private Long categoryId;

    protected ProductCategoryId() {}
    public ProductCategoryId(Long productId, Long categoryId) {
        this.productId = productId;
        this.categoryId = categoryId;
    }

    @Override public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof ProductCategoryId that)) return false;
        return Objects.equals(productId, that.productId) && Objects.equals(categoryId, that.categoryId);
    }

    @Override public int hashCode() {
        return Objects.hash(productId, categoryId);
    }
}
