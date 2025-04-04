package com.solve.bookstore.domain.book.event;

import com.solve.bookstore.domain.book.model.BookId;

import java.util.List;
import java.util.Set;

public record UpdatedBookEvent(
        Set<BookId> bookIds
) {
}
