package com.solve.bookstore.presentation;

import com.solve.bookstore.application.BookService;
import com.solve.bookstore.application.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "도서 관리", description = "도서 등록, 카테고리 변경, 상태 변경 관련 API")
@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @Operation(summary = "신규 도서 등록: ADMIN",
            description = """
                    새로운 도서를 등록합니다. ISBN이 중복되는 경우 기존 도서 정보로 저장됩니다.
                                        
                    [Auth - 테스트 계정 정보]
                    - ID: admin@admin.com
                    - PW: admin
                    """
    )
    @PostMapping
    public ResponseEntity<BookCreatedResponse> saveBook(
            @Parameter(
                    description = "도서 등록 정보",
                    required = true,
                    schema = @Schema(
                            examples = """
                                    {
                                      "title": "테스트 도서",
                                      "author": "김테테",
                                      "description": "테스트로 등록된 도서입니다 .",
                                      "isbn": "12345678",
                                      "category_ids": ["cid-1", "cid-2"]
                                    }
                                    """
                    )
            )
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "도서 등록 정보",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = BookCreateRequest.class),
                            examples = @ExampleObject(
                                    name = "도서등록예제",
                                    value = """
                                            {
                                              "title": "테스트 도서",
                                              "author": "김테테",
                                              "description": "테스트로 등록된 도서입니다 .",
                                              "isbn": "12345678",
                                              "category_ids": ["cid-1", "cid-2"]
                                            }
                                            """
                            )
                    )
            )
            @RequestBody
            @Valid BookCreateRequest request
    ) {
        BookCreatedResponse response = bookService.createBook(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "도서 카테고리 변경: ADMIN",
            description = """
                    도서의 카테고리를 변경합니다. 동일 ISBN을 가진 모든 도서의 카테고리가 변경됩니다.
                                        
                    [Auth - 테스트 계정 정보]
                    - ID: admin@admin.com
                    - PW: admin
                    """
    )
    @PatchMapping("/{bookId}")
    public ResponseEntity<CategoryChangedResponse> changeCategories(
            @Parameter(description = "도서 ID", required = true, example = "tbid-222")
            @PathVariable String bookId,
            @Parameter(description = "도서 상태", required = true,
                    schema = @Schema(
                            example = """
                                    {
                                      "category_ids": ["cid-1", "cid-2"]
                                    }
                                    """
                    ))
            @RequestBody @Valid CategoryChangeRequest request) {
        CategoryChangedResponse response
                = bookService.changeCategories(bookId, new CategoryUpdateRequest(request.categoryIds()));
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "도서 상태 변경: ADMIN",
            description = """
                    도서의 상태를 변경합니다. (대여가능, 대여중, 훼손, 분실)
                                        
                    [Auth - 테스트 계정 정보]
                    - ID: admin@admin.com
                    - PW: admin
                    """)
    @PatchMapping("/{bookId}/status")
    public ResponseEntity<BookStatusChangeResponse> updateBookStatus(
            @Parameter(description = "도서 ID", required = true, example = "tbid-222")
            @PathVariable String bookId,
            @Parameter(description = "도서 상태", required = true,
                    schema = @Schema(
                            example = """
                                    {
                                      "book_status": "LOST"
                                    }
                                    """
                    ))
            @RequestBody @Valid BookStatusChangeRequest request
    ) {
        BookStatusChangeResponse response = bookService.updateAndSaveBookStatus(bookId, request);
        return ResponseEntity.ok(response);
    }
}
