package com.solve.bookstore.application.dto;

import java.util.Set;

public record CategoryChangedResponse(
        Set<String> bookIds,
        Set<String> categoryIds,
        String message
) {
}
