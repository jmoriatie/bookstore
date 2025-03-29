package com.solve.bookstore.domain.bookcategory.model;

import com.solve.bookstore.domain.book.model.BookId;
import com.solve.bookstore.domain.category.model.CategoryId;

public class BookCategory {
    BookCategoryId id;

    public BookId getBookId(){
        return new BookId(this.id.getBookId());
    }

    public CategoryId getCategoryId(){
        return new CategoryId(this.id.getCategoryId());
    }
}
