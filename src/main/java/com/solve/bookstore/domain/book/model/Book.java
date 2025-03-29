package com.solve.bookstore.domain.book.model;
import com.solve.bookstore.domain.Rental.model.Rental;
import com.solve.bookstore.domain.category.model.Category;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class Book {
    private BookId id;
    private String title; // 제목
    private String author; // 지은이
    private List<Category> categorys; // 카테고리
    private String description; // 도서 설명
    private BookStatus status; // 도서 상태
    private Isbn isbn; // 도서 타이틀 고유식별값

    @Builder
    public Book(BookId id, String title, String author, List<Category> categorys, String description, Isbn isbn) {
        this.id = id != null? id : new BookId(); // ID 확인 후 없을 경우 새로 생성
        this.title = title;
        this.author = author;
        this.categorys = categorys;
        this.description = description;
        this.status = BookStatus.AVAILABLE; // 대여가능
        this.isbn = isbn;
    }

    private void validate(String title, String author, Category category, Isbn isbn){
        if(title == null || title.trim().isEmpty()){
            throw new IllegalArgumentException("도서 제목은 필수 입니다.");
        }
        if(author == null || author.trim().isEmpty()){
            throw new IllegalArgumentException("저자는 필수 입니다.");
        }
        if(category == null){
            throw new IllegalArgumentException("카테고리는 필수 입니다.");
        }
        if(isbn == null){
            throw new IllegalArgumentException("카테고리는 필수 입니다.");
        }
    }

    public void updateCategorys(List<Category> newCategorys){
        if(newCategorys.isEmpty()){
            throw new IllegalArgumentException("update 할 카테고리가 없습니다.");
        }
        this.categorys = newCategorys;
    }

    public void updateStatus(BookStatus newStatus){
        this.status = newStatus;
    }

    // 렌탈 가능 여부 확인
    public boolean isAvailableForRental(){
        return this.status == BookStatus.AVAILABLE;
    }

    // 렌탈
    public void rent(){
        if(!isAvailableForRental()){
            throw new IllegalArgumentException("대여할 수 없는 도서 입니다");
        }
        this.status = BookStatus.RENTED;
    }
    // 반납
    public void returnBook(Rental rental){
        this.status = BookStatus.AVAILABLE;
    }
}
