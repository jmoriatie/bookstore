package com.solve.bookstore.infrastructure.repository;

import com.solve.bookstore.domain.book.model.BookId;
import com.solve.bookstore.domain.category.model.Category;
import com.solve.bookstore.domain.category.model.CategoryId;
import com.solve.bookstore.domain.category.repository.CategoryRepository;
import com.solve.bookstore.infrastructure.entity.CategoryEntity;
import com.solve.bookstore.infrastructure.repository.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Slf4j
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

    private final CategoryJpaRepository categoryJpaRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public Category save(Category category) {
        CategoryEntity entity = categoryJpaRepository.save(categoryMapper.toEntity(category));
        return categoryMapper.toDomain(entity);
    }

    @Override
    public List<Category> saveAll(List<Category> categories) {
        List<CategoryEntity> categoryEntities = categories.stream()
                .map(categoryMapper::toEntity)
                .toList();

        categoryEntities.forEach(c -> log.info("Saved Category id={}, name={}", c.getId(), c.getName()));

        List<CategoryEntity> savedEntities = categoryJpaRepository.saveAll(categoryEntities);
        return savedEntities.stream()
                .map(categoryMapper::toDomain)
                .toList();
    }

    @Override
    public Category findById(CategoryId id) {
        CategoryEntity entity = categoryJpaRepository.findById(id.toString())
                .orElseThrow(() -> new IllegalArgumentException("없는 카테고리 ID 입니다. ID: " + id));
        return categoryMapper.toDomain(entity);
    }

    @Override
    public List<Category> findAllByIds(Set<CategoryId> ids) {
        Set<String> idsStr = ids.stream()
                .map(CategoryId::toString)
                .collect(Collectors.toSet());
        List<CategoryEntity> foundEntity = categoryJpaRepository.findAllById(idsStr);
        return foundEntity.stream()
                .map(categoryMapper::toDomain)
                .toList();
    }

    @Override
    public Set<CategoryId> findAllIdsByIds(Set<CategoryId> ids) {
        Set<String> idsStr = ids.stream()
                .map(CategoryId::toString)
                .collect(Collectors.toSet());
        Set<String> foundIds = categoryJpaRepository.findAllIdsByIds(idsStr);
        return foundIds.stream()
                .map(CategoryId::new)
                .collect(Collectors.toSet());
    }

    @Override
    public void deleteAll() {
        categoryJpaRepository.deleteAll();
    }
    
    @Override
    public boolean existsById(CategoryId id) {
        return categoryJpaRepository.existsById(id.toString());
    }
}
