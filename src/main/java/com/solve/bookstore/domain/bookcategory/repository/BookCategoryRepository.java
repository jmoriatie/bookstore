package com.solve.bookstore.domain.bookcategory.repository;

import com.solve.bookstore.domain.book.model.BookId;
import com.solve.bookstore.domain.bookcategory.model.BookCategory;
import com.solve.bookstore.domain.category.model.CategoryId;

import java.util.List;
import java.util.Set;

public interface BookCategoryRepository {

    BookCategory save(BookCategory bookCategory);

    List<BookCategory> findByBookId(BookId bookId);
    List<BookCategory> findByBookIds(Set<BookId> bookIds);
    Set<CategoryId> findCategoryIdByBookIdIn(BookId bookId);

    int deleteByBookIdIn(Set<BookId> bookIds);

    List<BookCategory> saveAll(List<BookCategory> bookCategories);

    void deleteAll();
}
