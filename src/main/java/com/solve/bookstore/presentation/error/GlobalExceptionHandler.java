package com.solve.bookstore.presentation.error;

import com.solve.bookstore.presentation.BookController;
import com.solve.bookstore.presentation.BookSearchController;
import com.solve.bookstore.presentation.RentalController;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice(annotations = {RestController.class},
        basePackageClasses = {
                BookController.class, BookSearchController.class, RentalController.class
        })
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    // @Valid 관련 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error ->
                        String.format("%s: %s (입력값: %s)",
                                error.getField(),
                                error.getDefaultMessage(),
                                error.getRejectedValue()
                        ))
                .collect(Collectors.toList());
        log.error("Validation failed: {}", errors);
        return createErrorResponse(
                HttpStatus.BAD_REQUEST,
                "잘못된 요청 - 입력값을 확인해주세요.",
                errors
        );
    }

    // @Validated 관련 예외 처리
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(violation ->
                        String.format("%s: %s (입력값: %s)",
                                violation.getPropertyPath(),
                                violation.getMessage(),
                                violation.getInvalidValue()
                        ))
                .collect(Collectors.toList());
        log.error("Constraint violation: {}", errors);
        return createErrorResponse(
                HttpStatus.BAD_REQUEST,
                "잘못된 요청 - 입력값을 확인해주세요.",
                errors
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("IllegalArgumentException: ", ex);
        return createErrorResponse(
                HttpStatus.BAD_REQUEST,
                "잘못된 요청",
                List.of(ex.getMessage())
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        log.error("RuntimeException: ", ex);
        return createErrorResponse(
                HttpStatus.BAD_REQUEST,
                "잘못된 요청 - 관리자에게 문의하세요",
                List.of(ex.getMessage())
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(
            Exception ex) {
        log.error("Unexpected error occurred: ", ex);
        return createErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "예상치 못한 오류가 발생했습니다",
                List.of("서버 관리자에게 문의해주세요")
        );
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(
            HttpStatus status,
            String message,
            List<String> errors) {
        return ResponseEntity
                .status(status)
                .body(new ErrorResponse(message, errors));
    }
}
