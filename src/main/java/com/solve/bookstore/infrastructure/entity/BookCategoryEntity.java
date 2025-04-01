package com.solve.bookstore.infrastructure.entity;

import com.solve.bookstore.infrastructure.config.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "book_categorys")
@Getter
@NoArgsConstructor
public class BookCategoryEntity extends BaseEntity {

    @EmbeddedId
    BookCategoryEntityId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    @MapsId("bookId")
    BookEntity book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @MapsId("categoryId")
    CategoryEntity category;

    private BookCategoryEntity(BookEntity book, CategoryEntity category) {
        this.id = new BookCategoryEntityId(book.getId(), category.getId());
        this.book = book;
        this.category = category;
    }

    private BookCategoryEntity(BookCategoryEntityId entityId, BookEntity book, CategoryEntity category) {
        this.id = entityId;
        this.book = book;
        this.category = category;
    }

    public static BookCategoryEntity create(BookEntity bookEntity, CategoryEntity categoryEntity){
        if(bookEntity == null) throw new IllegalArgumentException("BookEntity 를 입력해주세요.");
        if(categoryEntity == null) throw new IllegalArgumentException("CategoryEntity 를 입력해주세요");
        return new BookCategoryEntity(bookEntity, categoryEntity);
    }

    public static BookCategoryEntity rebuild(BookCategoryEntityId entityId, BookEntity bookEntity, CategoryEntity categoryEntity){
        return new BookCategoryEntity(entityId, bookEntity, categoryEntity);
    }
}
