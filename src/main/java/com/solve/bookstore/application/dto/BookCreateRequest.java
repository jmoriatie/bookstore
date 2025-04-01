package com.solve.bookstore.application.dto;

import com.solve.bookstore.domain.book.model.Book;
import com.solve.bookstore.domain.book.model.Isbn;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record BookCreateRequest(
        @NotEmpty(message = "제목을 입력해주세요.")
        String title,
        @NotEmpty(message = "작가를 입력해주세요.")
        String author,
        String description,
        @NotEmpty(message = "ISBN은 필수 값 입니다.")
        String isbn,
        @NotEmpty(message = "카테고리는 최소 1개 이상 선택해주세요.")
        List<String> categoryIds
) {
        // null 검증 추가
        public BookCreateRequest {
                if (categoryIds == null)
                        categoryIds = List.of(); // service validation 에서
        }

        public Book toDomain(){
                return Book.create(
                        this.title(),
                        this.author(),
                        this.description(),
                        new Isbn(this.isbn()));
        }
}
