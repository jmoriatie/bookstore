package com.solve.bookstore.domain.book.model;
import com.solve.bookstore.domain.Rental.model.Rental;
import com.solve.bookstore.domain.category.model.Category;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Book {
    private BookId id;
    private String title; // 제목
    private String author; // 지은이
    private Category category; // 카테고리
    private String description; // 도서 설명
    private BookStatus status; // 도서 상태

    @Builder
    public Book(BookId id, String title, String author, Category category, String description) {
        validate(title, author, category);
        this.id = id != null? id : new BookId(); // ID 확인 후 없을 경우 새로 생성
        this.title = title;
        this.author = author;
        this.category = category;
        this.description = description;
        this.status = BookStatus.AVAILABLE; // 대여가능
    }

    private void validate(String title, String author, Category category){
        if(title == null || title.trim().isEmpty()){
            throw new IllegalArgumentException("도서 제목은 필수 입니다.");
        }
        if(author == null || author.trim().isEmpty()){
            throw new IllegalArgumentException("저자는 필수 입니다.");
        }
        if(category == null){
            throw new IllegalArgumentException("카테고리는 필수 입니다.");
        }
    }

    public void updateCategory(Category newCategory){
        if(newCategory == null){
            throw new IllegalArgumentException("update 할 카테고리가 없습니다.");
        }
        this.category = newCategory;
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
