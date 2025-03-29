package com.solve.bookstore.infrastructure.repository;

import com.solve.bookstore.domain.book.model.Book;
import com.solve.bookstore.domain.book.model.BookId;
import com.solve.bookstore.domain.book.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * BookRepository 데코레이터 패턴 적용
 */
@Repository
@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepository {

    private final BookJpaRepository bookJpaRepository; // BookRepository jpa 활용

    @Override
    public Book save(Book book) {
        return null;
    }

    @Override
    public Optional<Book> findById(BookId id) {
        return Optional.empty();
    }
}
