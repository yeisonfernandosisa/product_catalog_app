package com.catalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.catalog.model.ProductCategory;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

    @Query("select count(link) > 0 from ProductCategory link where link.category.id = :categoryId")
    boolean existsForCategory(@Param("categoryId") Long categoryId);
}
