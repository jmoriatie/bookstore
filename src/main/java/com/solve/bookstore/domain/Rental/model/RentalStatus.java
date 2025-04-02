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

    public static RentalStatus fromStr(String status){
        try {
            return RentalStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("없는 렌탈상태 값 입니다. statusStr: "+status);
        }
    }

    public boolean isReturned(){
        return this == RETURNED;
    }

    public boolean isOverdue(){
        return this == OVERDUE;
    }
}
