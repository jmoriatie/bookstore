package com.solve.bookstore.presentaion;

import com.solve.bookstore.application.BookSearchService;
import com.solve.bookstore.application.dto.BookSearchRequest;
import com.solve.bookstore.application.dto.BookSearchResponse;
import com.solve.bookstore.domain.book.model.Book;
import com.solve.bookstore.domain.book.model.Isbn;
import com.solve.bookstore.domain.category.model.CategoryId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/books/search")
@RequiredArgsConstructor
public class BookSearchController {

    private final BookSearchService bookSearchService;

    @GetMapping("/title")
    public ResponseEntity<BookSearchResponse> searchByTitle(
            @Valid @RequestBody BookSearchRequest request
    ) {
        BookSearchResponse response = bookSearchService.findBooksByTitle(request.searchTerm());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/author")
    public ResponseEntity<BookSearchResponse> searchByAuthor(
            @Valid @RequestBody BookSearchRequest request
    ) {
        BookSearchResponse response = bookSearchService.findBooksByAuthor(request.searchTerm());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<BookSearchResponse> searchByCategory(
            @PathVariable String categoryId
    ) {
        BookSearchResponse response = bookSearchService.findBooksByCategory(new CategoryId(categoryId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookSearchResponse> searchByIsbn(
            @PathVariable String isbn
    ) {
        Book book = Book.create("", "", "", new Isbn(isbn));
        BookSearchResponse response = bookSearchService.getSameIsbnBooks(book);
        return ResponseEntity.ok(response);
    }
} 