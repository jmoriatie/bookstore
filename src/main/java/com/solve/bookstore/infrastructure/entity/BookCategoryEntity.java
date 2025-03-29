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

    @Embedded
    BookCategoryEntityId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    BookEntity book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    CategoryEntity category;

    public BookCategoryEntity(BookEntity book, CategoryEntity category) {
        this.id = new BookCategoryEntityId(book.getId(), category.getId());
        this.book = book;
        this.category = category;
    }
}
