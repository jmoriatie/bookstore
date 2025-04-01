package com.solve.bookstore.domain.bookcategory.repository;

import com.solve.bookstore.domain.book.model.BookId;
import com.solve.bookstore.domain.bookcategory.model.BookCategory;
import com.solve.bookstore.domain.bookcategory.model.BookCategoryId;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookCategoryRepository {

    BookCategory save(BookCategory bookCategory);

    List<BookCategory> findByBookId(BookId bookId);
    List<BookCategory> findByBookIds(Set<BookId> bookIds);

    int deleteBookIds(Set<BookId> bookIds);

    List<BookCategory> saveAll(List<BookCategory> bookCategories);

    void deleteAll();
}
