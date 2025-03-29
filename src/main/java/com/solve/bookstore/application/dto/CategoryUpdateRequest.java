package com.solve.bookstore.application.dto;

public record CategoryUpdateRequest(
        String categoryId,
        String bookId
        ) {
}
