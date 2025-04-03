package com.solve.bookstore.domain.rental.model;

import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode
public class RentalId {
    private final String id;

    public RentalId() {
        this.id = UUID.randomUUID().toString();
    }

    public RentalId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return this.id;
    }
}
