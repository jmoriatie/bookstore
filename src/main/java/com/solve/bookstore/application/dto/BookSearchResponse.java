package com.solve.bookstore.application.dto;

import com.solve.bookstore.domain.book.model.Book;

import java.util.List;
import java.util.stream.Collectors;

public record BookSearchResponse(
        List<BookInfo> books
) {
    public record BookInfo(
            String bookId,
            String title,
            String author,
            String description,
            String isbn,
            String status
    ) {
        public static BookInfo from(Book book) {
            return new BookInfo(
                    book.getId().toString(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getDescription(),
                    book.getIsbn().toString(),
                    book.getStatus().name()
            );
        }
    }

    public static BookSearchResponse from(List<Book> books) {
        return new BookSearchResponse(
                books.stream()
                        .map(BookInfo::from)
                        .toList()
        );
    }
} 