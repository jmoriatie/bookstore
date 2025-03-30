package com.solve.bookstore.infrastructure.repository;

import com.solve.bookstore.domain.book.model.Book;
import com.solve.bookstore.domain.book.model.BookId;
import com.solve.bookstore.domain.book.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * BookRepository 데코레이터 패턴 적용
 */
@Repository
@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepository {

    private final BookJpaRepository bookJpaRepository; // BookRepository jpa 활용

    @Override
    public BookId save(Book book) {
        return null;
    }

    @Override
    public List<BookId> saveAll(List<Book> books) {
        return null;
    }

    @Override
    public List<BookId> saveAll(Set<Book> books) {
        return null;
    }

    @Override
    public List<Book> findAllByIds(List<BookId> ids) {
        return null;
    }

    @Override
    public List<Book> findAllByIds(Set<BookId> ids) {
        return null;
    }

    @Override
    public Optional<Book> findById(BookId id) {
        return Optional.empty();
    }

    @Override
    public List<Book> findByIsbn(String isbn) {
        return null;
    }

    @Override
    public int deleteAll() {
        return 0;
    }
}
