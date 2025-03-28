package com.solve.bookstore.domain.book.model;

import lombok.Getter;

@Getter
public enum BookStatus {
    AVAILABLE("대여 가능"),
    RENTED("대여중"),
    NOT_AVAILABLE("대여 불가"),
    LOST("분실됨"),
    DAMAGED("훼손됨");

    private final String status;

    BookStatus(String status) {
        this.status = status;
    }
}
