package com.solve.bookstore.infrastructure.repository;

import com.solve.bookstore.infrastructure.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookJpaRepository extends JpaRepository<BookEntity, String> {
    List<BookEntity> findByIsbn(String isbn);
}
