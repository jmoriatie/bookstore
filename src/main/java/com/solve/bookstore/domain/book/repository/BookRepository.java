package com.solve.bookstore.domain.book.repository;

import com.solve.bookstore.domain.book.model.Book;
import com.solve.bookstore.domain.book.model.BookId;

import java.util.List;
import java.util.Optional;
import java.util.Set;

// TODO POJO 유지 & 추상화
//  mock test
public interface BookRepository {

    BookId save(Book book);

    List<BookId> saveAll(List<Book> books);
    List<BookId> saveAll(Set<Book> books);

    Optional<Book> findById(BookId id);
    List<Book> findByIsbn(String isbn);
}
