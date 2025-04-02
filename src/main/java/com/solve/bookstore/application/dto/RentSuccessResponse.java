package com.solve.bookstore.application.dto;

public record RentSuccessResponse(
        String rentalId,
        String bookId,
        String bookTitle,
        String message
) {
    public static RentSuccessResponse success(String rentalId, String bookId, String bookTitle){
        return new RentSuccessResponse(rentalId, bookId, bookTitle, "도서 대여 완료 도서명: "+bookTitle);
    }
}
