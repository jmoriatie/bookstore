package com.solve.bookstore.domain.bookcategory.model;

import com.solve.bookstore.domain.book.model.BookId;
import com.solve.bookstore.domain.category.model.CategoryId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@EqualsAndHashCode
public class BookCategoryId {
    private final String bookId;
    private final String categoryId;

    public BookCategoryId(String bookId, String categoryId) {
        this.bookId = bookId;
        this.categoryId = categoryId;
    }

    @Override
    public String toString() {
        return this.bookId+":"+this.categoryId;
    }
}
