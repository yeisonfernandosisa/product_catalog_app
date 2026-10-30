package com.catalog.model;

import jakarta.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "categories", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "created_by"}))
public class Category extends AuditableEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @OneToMany(mappedBy = "category")
    private Set<ProductCategory> productCategories = new LinkedHashSet<>();

    protected Category() {}
    public Category(String name, String actor) {
        this.name = name;
        initializeAudit(actor);
    }
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Set<ProductCategory> getProductCategories() { return productCategories; }
}
