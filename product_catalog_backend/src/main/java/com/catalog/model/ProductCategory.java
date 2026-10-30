package com.catalog.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "product_categories",
        uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "category_id"}))
public class ProductCategory extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    protected ProductCategory() {
    }

    public ProductCategory(Product product, Category category, String actor) {
        this.product = product;
        this.category = category;
        initializeAudit(actor);
    }

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public Category getCategory() {
        return category;
    }
}
