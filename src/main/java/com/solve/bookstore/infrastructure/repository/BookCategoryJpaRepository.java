package com.solve.bookstore.infrastructure.repository;

import com.solve.bookstore.infrastructure.entity.BookCategoryEntity;
import com.solve.bookstore.infrastructure.entity.BookCategoryEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookCategoryJpaRepository extends JpaRepository<BookCategoryEntity, BookCategoryEntityId> {
}
