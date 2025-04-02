package com.solve.bookstore.application.dto;

public record ReturnBookResponse(
        String bookId,
        String bookTitle,
        String message
) {

    public static ReturnBookResponse success(String bookId, String bookTitle){
        return new ReturnBookResponse(bookId, bookTitle, "도서 반납 완료 도서명: "+bookTitle);
    }
}
