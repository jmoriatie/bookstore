package com.solve.bookstore.application.dto;

public record BookStatusChangeResponse(
        String bookId,
        String bookStatus,
        String message
) {
    public static BookStatusChangeResponse success(String bookId, String bookStatus){
        return new BookStatusChangeResponse(bookId, bookStatus, "도서상태 변경 완료");
    }
}
