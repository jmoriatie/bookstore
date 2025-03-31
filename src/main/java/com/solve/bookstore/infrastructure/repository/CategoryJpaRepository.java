package com.solve.bookstore.infrastructure.repository;

import com.solve.bookstore.infrastructure.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, String> {
}
