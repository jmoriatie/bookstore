package com.solve.bookstore.application.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CategoryChangeRequest(
        @NotNull(message = "변경할 카테고리들은 null이 불가능합니다.")
        List<String> categoryIds
) {
}
