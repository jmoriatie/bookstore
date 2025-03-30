package com.solve.bookstore.infrastructure.repository;

import com.solve.bookstore.domain.category.model.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryJpaRepository extends JpaRepository<Category, String> {
}
