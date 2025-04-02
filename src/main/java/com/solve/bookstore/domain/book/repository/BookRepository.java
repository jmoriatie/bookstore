package com.solve.bookstore.domain.book.repository;

import com.solve.bookstore.domain.book.model.Book;
import com.solve.bookstore.domain.book.model.BookId;
import com.solve.bookstore.domain.book.model.Isbn;

import java.util.List;

public interface BookRepository {

    Book save(Book book);
    Book findById(BookId id);
    List<Book> findByIsbn(Isbn isbn);
    void deleteAll();
}
