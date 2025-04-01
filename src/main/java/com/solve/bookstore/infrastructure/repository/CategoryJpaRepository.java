package com.solve.bookstore.infrastructure.repository;

import com.solve.bookstore.infrastructure.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, String> {
    @Query("SELECT c.id FROM CategoryEntity c WHERE c.id IN :categoryIds")
    Set<String> findAllIdsByIds(Set<String> categoryIds);
}
