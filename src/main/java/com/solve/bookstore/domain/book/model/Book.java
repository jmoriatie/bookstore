package com.solve.bookstore.domain.book.model;
import lombok.Getter;

@Getter
public class Book {
    private BookId id;
    private String title; // 제목
    private String author; // 지은이
    private String description; // 도서 설명
    private BookStatus status; // 도서 상태
    private Isbn isbn; // 도서 타이틀 고유식별값

    private Book(BookId id, String title, String author, String description, BookStatus status, Isbn isbn) {
        validate(title, author, isbn);
        this.id = id != null? id : new BookId(); // ID 확인 후 없을 경우 새로 생성
        this.title = title;
        this.author = author;
        this.description = description;
        this.status = status; // 대여가능
        this.isbn = isbn;
    }

    private void validate(String title, String author, Isbn isbn){
        if(title == null || title.trim().isEmpty())
            throw new IllegalArgumentException("도서 제목은 필수 입니다.");
        if(author == null || author.trim().isEmpty())
            throw new IllegalArgumentException("저자는 필수 입니다.");
        if(isbn == null)
            throw new IllegalArgumentException("카테고리는 필수 입니다.");
    }

    public static Book create(String title, String author, String description, Isbn isbn){
        return new Book(null, title, author, description, BookStatus.AVAILABLE, isbn);
    }

    public static Book createFromExistBook(Book existBook){
        return new Book(
                null,
                existBook.getTitle(),
                existBook.getAuthor(),
                existBook.getDescription(),
                BookStatus.AVAILABLE,
                existBook.getIsbn()
        );
    }

    public static Book rebuild(BookId id, String title, String author, String description, BookStatus status, Isbn isbn){
        return new Book(id, title, author, description, status, isbn);
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
    public void returnBook(){
        this.status = BookStatus.AVAILABLE;
    }
}
