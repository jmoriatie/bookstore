package com.solve.bookstore.application.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CategoryUpdateRequest(
        @NotNull(message = "업데이트할 카테고리 목록을 입력해주세요.")
        List<String> categoryIds
        ) {
}
