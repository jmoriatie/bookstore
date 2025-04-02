package com.solve.bookstore.domain.book.model;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Isbn {
    String serialNumber;

    public Isbn(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    @Override
    public String toString() {
        return this.serialNumber;
    }
}
