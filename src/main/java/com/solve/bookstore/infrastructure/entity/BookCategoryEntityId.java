package com.solve.bookstore.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class BookCategoryEntityId implements Serializable {

    @Column(name = "book_id")
    private String bookId;
    @Column(name = "category_id")
    private String categoryId;

    public BookCategoryEntityId(String bookId, String categoryId) {
        this.bookId = bookId;
        this.categoryId = categoryId;
    }
}
