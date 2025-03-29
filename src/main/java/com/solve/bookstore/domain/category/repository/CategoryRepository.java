package com.solve.bookstore.domain.category.repository;

import com.solve.bookstore.domain.category.model.Category;
import com.solve.bookstore.domain.category.model.CategoryId;

import java.util.Optional;

public interface CategoryRepository {

    Optional<Category> findById(CategoryId id);
}
