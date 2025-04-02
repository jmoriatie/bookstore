package com.solve.bookstore.application.dto;

public record BookStatusChangeResponse(
        String bookId,
        String bookStatus,
        String message
) {
    public static BookStatusChangeResponse success(String bookId, String bookStatus){
        return new BookStatusChangeResponse(bookId, bookStatus, "도서상태 변경 완료");
    }

    public static BookStatusChangeResponse notAvailable(String bookId, String bookStatus){
        return new BookStatusChangeResponse(bookId, bookStatus, "대여불가 상태의 도서입니다.");
    }
}
