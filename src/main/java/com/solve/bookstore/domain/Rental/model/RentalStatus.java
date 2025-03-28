package com.solve.bookstore.domain.Rental.model;

import lombok.Getter;

@Getter
public enum RentalStatus {
    ACTIVE("대여중"),
    RETURNED("반납완료"),
    OVERDUE("연체중")
    ;

    private final String description;

    RentalStatus(String description) {
        this.description = description;
    }
}
