package com.solve.bookstore.domain.book.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@Getter
@EqualsAndHashCode
public class BookId {
    private final String id;

    public BookId() {
        this.id = UUID.randomUUID().toString();
    }

    public BookId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return this.id;
    }
}
