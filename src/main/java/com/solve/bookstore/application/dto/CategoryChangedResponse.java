package com.solve.bookstore.application.dto;

import com.solve.bookstore.domain.book.model.BookId;

import java.util.Set;

public record CategoryChangedResponse(
        Set<BookId> bookIds
) {
}
