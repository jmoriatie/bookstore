package com.solve.bookstore.presentation.error;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "에러 응답")
public record ErrorResponse(
        @Schema(description = "Error 메세지")
        String message,

        @Schema(description = "Error 목록")
        List<String> errors,

        @Schema(description = "에러 발생 시간")
        LocalDateTime timestamp
) {
    public ErrorResponse(String message, List<String> errors) {
        this(message, errors, LocalDateTime.now());
    }
}
