package com.solve.bookstore.domain.bookcategory.model;

import com.solve.bookstore.domain.book.model.Book;
import com.solve.bookstore.domain.category.model.Category;

import java.util.Collections;
import java.util.Set;

// BookSearch 를 위한 메서드
public record BookWithCategories(
        Book book,
        Set<Category> categories
) {
    public static BookWithCategories of(Book book, Set<Category> categories){
        return new BookWithCategories(book, categories.isEmpty()? Collections.emptySet() : categories);
    }
}
