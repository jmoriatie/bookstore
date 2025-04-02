package com.solve.bookstore.application.dto;

public record ReturnBookResponse(
        String rentalId,
        String bookId,
        String bookTitle,
        String message
) {

    public static ReturnBookResponse success(String rentalId, String bookId, String bookTitle){
        return new ReturnBookResponse(rentalId, bookId, bookTitle, "도서 반납 완료 도서명: "+bookTitle);
    }
}
