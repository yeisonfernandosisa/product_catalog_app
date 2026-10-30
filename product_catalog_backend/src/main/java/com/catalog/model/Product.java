package com.catalog.model;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "products")
public class Product extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @SuppressWarnings("FieldMayBeFinal")
    private Set<ProductCategory> productCategories = new LinkedHashSet<>();

    protected Product() {
    }

    public Product(String name, String description, BigDecimal price, String actor) {
        this.name = name;
        this.description = description;
        this.price = price;
        initializeAudit(actor);
    }

    public Long getId() {
        return id;
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

    public Set<ProductCategory> getProductCategories() {
        return productCategories;
    }

    public void assignCategories(Set<Category> categories, String actor) {
        productCategories.removeIf(
                link
                -> categories.stream()
                        .noneMatch(category -> category.getId().equals(link.getCategory().getId())));
        Set<Long> assignedIds
                = productCategories.stream()
                        .map(link -> link.getCategory().getId())
                        .collect(java.util.stream.Collectors.toSet());
        categories.stream()
                .filter(category -> !assignedIds.contains(category.getId()))
                .map(category -> new ProductCategory(this, category, actor))
                .forEach(productCategories::add);
    }
}
