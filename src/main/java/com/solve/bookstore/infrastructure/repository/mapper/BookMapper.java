package com.solve.bookstore.infrastructure.repository.mapper;

import com.solve.bookstore.domain.book.model.Book;
import com.solve.bookstore.domain.book.model.BookId;
import com.solve.bookstore.domain.book.model.BookStatus;
import com.solve.bookstore.domain.book.model.Isbn;
import com.solve.bookstore.infrastructure.entity.BookEntity;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    /**
     * Entity -> Domain
     */
    public Book toDomain(BookEntity entity){
        if(entity == null) return null;

        return Book.rebuild(
                new BookId(entity.getId()),
                entity.getTitle(),
                entity.getAuthor(),
                entity.getDescription(),
                BookStatus.valueOf(entity.getStatus()),
                new Isbn(entity.getIsbn())
        );
    }

    /**
     * Domain -> Entity
     */
    public BookEntity toEntity(Book domain){
        if(domain == null) return null;
        return BookEntity.from(
                domain.getId().toString(),
                domain.getTitle(),
                domain.getAuthor(),
                domain.getDescription(),
                domain.getStatus().name(),
                domain.getIsbn().toString()
        );
    }
}
