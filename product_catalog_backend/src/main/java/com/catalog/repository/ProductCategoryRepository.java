package com.catalog.repository;

import com.catalog.model.ProductCategory;
import com.catalog.model.ProductCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, ProductCategoryId> {
    @Query("select count(link) > 0 from ProductCategory link where link.category.id = :categoryId")
    boolean existsForCategory(@Param("categoryId") Long categoryId);
}
