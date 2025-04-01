package com.solve.bookstore.presentaion;

import com.solve.bookstore.application.BookService;
import com.solve.bookstore.application.dto.BookCreateRequest;
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
}
