package com.catalog.repository;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.catalog.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Page<Category> findAllByCreatedBy(String createdBy, Pageable pageable);

    Collection<Category> findAllByIdInAndCreatedBy(Collection<Long> ids, String createdBy);

    boolean existsByNameIgnoreCaseAndCreatedBy(String name, String createdBy);

    @Modifying
    @Query(
            """
        UPDATE Category category
        SET category.createdBy = :newOwner,
            category.updatedBy = :actor,
            category.updatedAt = CURRENT_TIMESTAMP
        WHERE category.createdBy = :oldOwner
        """)
    int transferCreatedBy(
            @Param("oldOwner") String oldOwner,
            @Param("newOwner") String newOwner,
            @Param("actor") String actor);
}
