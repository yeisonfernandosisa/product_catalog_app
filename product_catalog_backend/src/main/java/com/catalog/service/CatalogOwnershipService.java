package com.catalog.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.catalog.repository.CategoryRepository;
import com.catalog.repository.ProductRepository;

@Service
public class CatalogOwnershipService {

    private final ProductRepository products;
    private final CategoryRepository categories;

    public CatalogOwnershipService(ProductRepository products, CategoryRepository categories) {
        this.products = products;
        this.categories = categories;
    }

    @Transactional
    public void transferToNewEmail(String oldEmail, String newEmail, String actor) {
        if (oldEmail.equals(newEmail)) {
            return;
        }

        products.transferCreatedBy(oldEmail, newEmail, actor);
        categories.transferCreatedBy(oldEmail, newEmail, actor);
    }
}
