package com.solve.bookstore.domain.book.repository;

import com.solve.bookstore.domain.book.model.Book;
import com.solve.bookstore.domain.book.model.BookId;

import java.util.Optional;

// TODO POJO 유지 & 추상화
//  mock test
public interface BookRepository {

    Book save(Book book);

    Optional<Book> findById(BookId id);
}
