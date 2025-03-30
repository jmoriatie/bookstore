package com.solve.bookstore.application.dto;

import com.solve.bookstore.domain.book.model.BookId;

import java.util.List;

public record CategoryChangedResponse(
        List<BookId> bookIds
) {
}
