package com.solve.bookstore.presentaion;

import com.solve.bookstore.application.BookService;
import com.solve.bookstore.application.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "도서 관리", description = "도서 등록, 카테고리 변경, 상태 변경 관련 API")
@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @Operation(summary = "신규 도서 등록", description = "새로운 도서를 등록합니다. ISBN이 중복되는 경우 기존 도서 정보로 저장됩니다.")
    @PostMapping
    public ResponseEntity<String> saveBook(
            @RequestBody @Valid BookCreateRequest request
    ){
        bookService.createBook(request);
        return ResponseEntity.ok("success");
    }

    @Operation(summary = "도서 카테고리 변경", description = "도서의 카테고리를 변경합니다. 동일 ISBN을 가진 모든 도서의 카테고리가 변경됩니다.")
    @PatchMapping("/{bookId}")
    public ResponseEntity<CategoryChangedResponse> changeCategories(
            @Parameter(description = "도서 ID", required = true)
            @PathVariable String bookId,
            @RequestBody @Valid List<String> categoryIds){
        CategoryChangedResponse response
                = bookService.changeCategories(bookId, new CategoryUpdateRequest(categoryIds));
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "도서 상태 변경", description = "도서의 상태를 변경합니다. (대여가능, 대여중, 훼손, 분실)")
    @PatchMapping("/{bookId}/status")
    public ResponseEntity<BookStatusChangeResponse> updateBookStatus(
            @Parameter(description = "도서 ID", required = true)
            @PathVariable String bookId,
            @RequestBody @Valid BookStatusChangeRequest request
    ){
        BookStatusChangeResponse response = bookService.updateAndSaveBookStatus(bookId, request);
        return ResponseEntity.ok(response);
    }
}
