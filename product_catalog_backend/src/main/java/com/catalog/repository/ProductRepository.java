package com.catalog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.catalog.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findAllByCreatedBy(String createdBy, Pageable pageable);

    Page<Product> findAllByCreatedByAndProductCategories_Category_Id(
            String createdBy, Long categoryId, Pageable pageable);

    @Modifying
    @Query(
            """
        UPDATE Product product
        SET product.createdBy = :newOwner,
            product.updatedBy = :actor,
            product.updatedAt = CURRENT_TIMESTAMP
        WHERE product.createdBy = :oldOwner
        """)
    int transferCreatedBy(
            @Param("oldOwner") String oldOwner,
            @Param("newOwner") String newOwner,
            @Param("actor") String actor);
}
