package com.solve.bookstore.infrastructure.repository;

import com.solve.bookstore.infrastructure.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookJpaRepository extends JpaRepository<BookEntity, String> {
}
