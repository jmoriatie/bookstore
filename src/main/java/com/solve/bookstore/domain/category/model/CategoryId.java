package com.solve.bookstore.domain.category.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@EqualsAndHashCode
public class CategoryId {
    private final String id;

    public CategoryId() {
        this.id = UUID.randomUUID().toString();
    }

    public CategoryId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return this.id;
    }
}
