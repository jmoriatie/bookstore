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

    public static BookStatus fromStr(String status){
        try {
            return BookStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("없는 도서상태 값 입니다. statusStr: "+status);
        }
    }

    public boolean isAvailable(){
        return this == AVAILABLE;
    }

    public boolean isRented(){
        return this == RENTED;
    }

    public boolean isNotAvailable(){
        return this == NOT_AVAILABLE || this == LOST || this == DAMAGED;
    }
}
