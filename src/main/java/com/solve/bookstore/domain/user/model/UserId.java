package com.solve.bookstore.domain.user.model;

import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode
public class UserId {
    private final String id;

    public UserId() {
        this.id = UUID.randomUUID().toString();
    }

    public UserId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return this.id;
    }
}
