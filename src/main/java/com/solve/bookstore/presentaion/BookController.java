package com.solve.bookstore.presentaion;

import com.solve.bookstore.application.BookService;
import com.solve.bookstore.application.dto.*;
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
     * 신규 도서 등록
     */
    @PostMapping
    public ResponseEntity<String> saveBook(BookCreateRequest request){
        bookService.createBook(request);
        return ResponseEntity.ok("success");
    }

    /**
     * 카테고리 업데이트
     */
    @PatchMapping("/{bookId}")
    public ResponseEntity<CategoryChangedResponse> changeCategories(
            @PathVariable String bookId,
            @RequestBody @Valid List<String> categoryIds){
        CategoryChangedResponse response
                = bookService.changeCategories(bookId, new CategoryUpdateRequest(categoryIds));
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{bookId}/status")
    public ResponseEntity<BookStatusChangeResponse> updateBookStatus(
            @PathVariable String bookId,
            @RequestBody @Valid BookStatusChangeRequest request
    ){
        BookStatusChangeResponse response = bookService.updateAndSaveBookStatus(bookId, request);
        return ResponseEntity.ok(response);
    }
}
