package com.solve.bookstore.domain.book.repository;

import com.solve.bookstore.domain.book.model.Book;
import com.solve.bookstore.domain.book.model.BookId;
import com.solve.bookstore.domain.book.model.Isbn;

import java.util.List;
import java.util.Set;

public interface BookRepository {

    Book save(Book book);
    Book findById(BookId id);
    List<Book> findByIds(Set<BookId> bookIds);
    List<Book> findByIsbn(Isbn isbn);
    void deleteAll();

    List<Book> findByTitleContaining(String title);
    List<Book> findByAuthorContaining(String author);
    List<Book> findByTitleContainingAndAuthorContaining(String title, String author);
}
