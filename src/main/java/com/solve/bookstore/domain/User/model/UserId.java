package com.solve.bookstore.domain.User.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@Getter
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
