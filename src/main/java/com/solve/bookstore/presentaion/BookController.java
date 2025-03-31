package com.solve.bookstore.presentaion;

import com.solve.bookstore.application.BookService;
import com.solve.bookstore.application.dto.CategoryChangedResponse;
import com.solve.bookstore.application.dto.CategoryUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    /**
     * 카테고리 업데이트
     */
    @PatchMapping("/{bookId}")
    public ResponseEntity<CategoryChangedResponse> changeCategorys(
            @PathVariable String bookId,
            @RequestBody @Valid List<String> categoryIds){
        CategoryChangedResponse response = bookService.changeCategorys(
                bookId, new CategoryUpdateRequest(categoryIds));
        return ResponseEntity.ok(response);
    }
}
