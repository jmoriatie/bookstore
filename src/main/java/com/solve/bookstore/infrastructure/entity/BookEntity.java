package com.solve.bookstore.infrastructure.entity;

import com.solve.bookstore.domain.book.model.BookStatus;
import com.solve.bookstore.infrastructure.config.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "books")
@Getter
@NoArgsConstructor
public class BookEntity extends BaseEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String title; // 제목

    @Column(nullable = false)
    private String author; // 지은이

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookCategoryEntity> bookCategorys = new ArrayList<>(); // 연관 매핑

    private String description; // 도서 설명

    @Column(nullable = false)
    private String status; // 도서 상태

    @Column(nullable = false)
    private String isbn; // 도서 타이틀 고유식별값

    @OneToMany(mappedBy = "book")
    private List<RentalEntity> rentals = new ArrayList<>();

    private BookEntity(String id, String title, String author, String description, String status, String isbn) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.status = status;
        this.isbn = isbn;
    }

    public static BookEntity create(String id, String title, String author, String description, String isbn){
        return new BookEntity(id, title, author, description, BookStatus.AVAILABLE.name(), isbn);
    }

    public static BookEntity from(String id, String title, String author, String description, String status, String isbn){
        return new BookEntity(id, title, author, description, status, isbn);
    }
}
