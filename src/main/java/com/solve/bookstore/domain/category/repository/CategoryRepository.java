package com.solve.bookstore.domain.category.repository;

import com.solve.bookstore.domain.category.model.Category;
import com.solve.bookstore.domain.category.model.CategoryId;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CategoryRepository {

    Optional<Category> findById(CategoryId id);
    Set<CategoryId> findAllIdsByIds(Set<CategoryId> ids);
    List<Category> findAllIdsByIds(List<CategoryId> ids);

    int deleteAll();
}
