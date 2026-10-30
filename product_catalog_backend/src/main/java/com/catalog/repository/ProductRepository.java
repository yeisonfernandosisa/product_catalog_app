package com.catalog.repository;

import com.catalog.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findAllByCreatedBy(String createdBy, Pageable pageable);
    Page<Product> findAllByCreatedByAndProductCategories_Category_Id(String createdBy, Long categoryId, Pageable pageable);
}
