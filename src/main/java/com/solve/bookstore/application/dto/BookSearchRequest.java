package com.solve.bookstore.application.dto;

import jakarta.validation.constraints.NotEmpty;

public record BookSearchRequest(
        @NotEmpty(message = "검색어를 입력해주세요.")
        String searchTerm
) {
} 