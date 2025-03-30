package com.solve.bookstore.domain.book.repository;

import com.solve.bookstore.domain.book.model.Book;
import com.solve.bookstore.domain.book.model.BookId;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookRepository {

    BookId save(Book book);

    List<BookId> saveAll(List<Book> books);
    List<BookId> saveAll(Set<Book> books);

    List<Book> findAllByIds(List<BookId> ids);
    List<Book> findAllByIds(Set<BookId> ids);
    
    Optional<Book> findById(BookId id);
    List<Book> findByIsbn(String isbn);

    int deleteAll();
}
