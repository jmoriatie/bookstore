package com.solve.bookstore.presentation;

import com.solve.bookstore.application.BookSearchService;
import com.solve.bookstore.application.dto.BookSearchResponse;
import com.solve.bookstore.domain.category.model.CategoryId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "도서 검색", description = "도서 검색 관련 API")
@RestController
@RequestMapping("/api/v1/books/search")
@RequiredArgsConstructor
@Validated
public class BookSearchController {

    private final BookSearchService bookSearchService;

    @Operation(summary = "제목으로 도서 검색", description = "도서 제목으로 도서를 검색합니다. 부분 일치 검색이 가능합니다.")
    @GetMapping("/title")
    public ResponseEntity<BookSearchResponse> searchByTitle(
            @Parameter(description = "검색할 도서 제목", required = true, example = "단순하게")
            @NotEmpty(message = "검색어를 입력해주세요.")
            @RequestParam String searchTerm
    ) {
        return ResponseEntity.ok(bookSearchService.findBooksByTitle(searchTerm));
    }

    @Operation(summary = "저자(지은이)로 도서 검색", description = "도서 저자(지은이)로 도서를 검색합니다. 부분 일치 검색이 가능합니다.")
    @GetMapping("/author")
    public ResponseEntity<BookSearchResponse> searchByAuthor(
            @Parameter(description = "검색할 저자(지은이)", required = true, example = "지승")
            @NotEmpty(message = "검색어를 입력해주세요.")
            @RequestParam String searchTerm
    ) {
        return ResponseEntity.ok(bookSearchService.findBooksByAuthor(searchTerm));
    }

    @Operation(summary = "카테고리로 도서 검색", description = "카테고리 ID로 해당 카테고리에 속한 도서들을 검색합니다.")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<BookSearchResponse> searchByCategory(
            @Parameter(description = "카테고리 ID", required = true, example = "cid-2")
            @PathVariable String categoryId
    ) {
        return ResponseEntity.ok(bookSearchService.findBooksByCategory(new CategoryId(categoryId)));
    }

    @Operation(summary = "ISBN으로 도서 검색", description = "ISBN으로 도서를 검색합니다. 동일 ISBN을 가진 모든 도서를 찾습니다.")
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookSearchResponse> searchByIsbn(
            @Parameter(description = "도서 ISBN", required = true, example = "ISBN-99999")
            @PathVariable String isbn
    ) {
        return ResponseEntity.ok(bookSearchService.getSameIsbnBooks(isbn));
    }
} 