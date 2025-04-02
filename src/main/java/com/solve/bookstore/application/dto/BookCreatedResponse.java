package com.solve.bookstore.application.dto;

public record BookCreatedResponse(
        String bookId,
        String bookStatus,
        String message
) {
    public static BookCreatedResponse from(String bookId, String bookStatus, String message){
        return new BookCreatedResponse(bookId, bookStatus, message);
    }
}
