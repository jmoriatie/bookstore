package com.solve.bookstore.infrastructure.repository;

import com.solve.bookstore.domain.category.model.Category;
import com.solve.bookstore.domain.category.model.CategoryId;
import com.solve.bookstore.domain.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

    private final CategoryJpaRepository categoryJpaRepository;

    @Override
    public Optional<Category> findById(CategoryId id) {
        return Optional.empty();
    }

    @Override
    public List<Category> findAllIdsByIds(Set<CategoryId> ids) {
        return null;
    }

    @Override
    public List<Category> findAllIdsByIds(List<CategoryId> ids) {
        return null;
    }

    @Override
    public int deleteAll() {
        return 0;
    }
}
