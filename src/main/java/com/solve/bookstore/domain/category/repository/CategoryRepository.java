package com.solve.bookstore.domain.category.repository;

import com.solve.bookstore.domain.category.model.Category;
import com.solve.bookstore.domain.category.model.CategoryId;

import java.util.List;
import java.util.Set;

public interface CategoryRepository {

    Category save(Category category);
    List<Category> saveAll(List<Category> categories);
    Category findById(CategoryId id);
    List<Category> findAllByIds(Set<CategoryId> ids);
    Set<CategoryId> findAllIdsByIds(Set<CategoryId> ids);
    void deleteAll();
}
