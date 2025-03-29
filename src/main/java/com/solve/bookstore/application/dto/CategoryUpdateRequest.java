package com.solve.bookstore.application.dto;

import java.util.List;

public record CategoryUpdateRequest(
        List<String> categoryIds,
        String bookId
        ) {
}
