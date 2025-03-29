package com.solve.bookstore.domain.category.repository;

import com.solve.bookstore.domain.category.model.Category;
import com.solve.bookstore.domain.category.model.CategoryId;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CategoryRepository {

    Optional<Category> findById(CategoryId id);
    List<Category> findAllByIds(Set<CategoryId> ids);
    List<Category> findAllByIds(List<CategoryId> ids);
}
