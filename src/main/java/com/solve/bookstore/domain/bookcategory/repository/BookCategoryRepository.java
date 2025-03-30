package com.solve.bookstore.domain.bookcategory.repository;

import com.solve.bookstore.domain.book.model.BookId;
import com.solve.bookstore.domain.bookcategory.model.BookCategory;
import com.solve.bookstore.domain.bookcategory.model.BookCategoryId;
import com.solve.bookstore.domain.category.model.CategoryId;

import java.util.List;
import java.util.Set;

public interface BookCategoryRepository {

    List<BookCategory> findByBookIds(Set<BookId> bookIds);
    BookCategoryId save(BookCategory bookCategory);

    int deleteBookIds(Set<BookId> bookIds);

    List<BookCategoryId> savaAll(List<BookCategory> bookCategories);
}
