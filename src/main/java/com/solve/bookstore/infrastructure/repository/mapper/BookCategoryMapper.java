package com.solve.bookstore.infrastructure.repository.mapper;

import com.solve.bookstore.domain.bookcategory.model.BookCategory;
import com.solve.bookstore.domain.bookcategory.model.BookCategoryId;
import com.solve.bookstore.infrastructure.entity.BookCategoryEntity;
import org.springframework.stereotype.Component;

@Component
public class BookCategoryMapper {

    /**
     * Entity -> Domain
     */
    public BookCategory toDomain(BookCategoryEntity entity){
        if(entity == null) return null;

        BookCategoryId bookCategoryId = new BookCategoryId(
                entity.getId().getBookId(),
                entity.getId().getCategoryId()
        );
        return new BookCategory(bookCategoryId);
    }
}
