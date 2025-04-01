package com.solve.bookstore.domain.book.repository;

import com.solve.bookstore.domain.book.model.Book;
import com.solve.bookstore.domain.book.model.BookId;

import java.util.List;
import java.util.Set;

public interface BookRepository {

    Book save(Book book);
    List<Book> findAllByIds(List<BookId> ids);
    List<Book> findAllByIds(Set<BookId> ids);
    
    Book findById(BookId id);
    List<Book> findByIsbn(String isbn);
    void deleteAll();
}
