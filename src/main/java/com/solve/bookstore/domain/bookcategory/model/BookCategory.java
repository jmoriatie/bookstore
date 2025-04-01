package com.solve.bookstore.domain.bookcategory.model;

import com.solve.bookstore.domain.book.model.BookId;
import com.solve.bookstore.domain.category.model.CategoryId;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class BookCategory {
    private BookCategoryId id;

    public BookCategory(BookCategoryId id) {
        this.id = id;
    }

    public BookId getBookId(){
        return new BookId(this.id.getBookId());
    }

    public CategoryId getCategoryId(){
        return new CategoryId(this.id.getCategoryId());
    }

    public static BookCategory create(BookId bookId, CategoryId categoryId){
        return new BookCategory(new BookCategoryId(bookId.toString(), categoryId.toString()));
    }
}
