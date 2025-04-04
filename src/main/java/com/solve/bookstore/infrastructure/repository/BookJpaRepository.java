package com.solve.bookstore.infrastructure.repository;

import com.solve.bookstore.infrastructure.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface BookJpaRepository extends JpaRepository<BookEntity, String> {
    List<BookEntity> findByIsbn(String isbn);
    List<BookEntity> findByIsbnAndStatus(String isbn, String status);
    List<BookEntity> findByIdIn(Set<String> ids);
    List<BookEntity> findByTitleContaining(String title);
    List<BookEntity> findByAuthorContaining(String author);
    List<BookEntity> findByTitleContainingOrAuthorContaining(String title, String author);
    List<BookEntity> findByStatus(String status);
}
