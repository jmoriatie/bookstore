package com.solve.bookstore.application.dto;

import jakarta.validation.constraints.NotEmpty;

public record BookStatusChangeRequest(
        @NotEmpty(message = "변경할 도서 상태를 입력해주세요.")
        String bookStatus
) {
}
