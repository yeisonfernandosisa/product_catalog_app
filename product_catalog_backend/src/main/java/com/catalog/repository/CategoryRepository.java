package com.catalog.repository;

import com.catalog.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Page<Category> findAllByCreatedBy(String createdBy, Pageable pageable);
    Collection<Category> findAllByIdInAndCreatedBy(Collection<Long> ids, String createdBy);
    boolean existsByNameIgnoreCaseAndCreatedBy(String name, String createdBy);
}
