package com.solve.bookstore.domain.rental.repository;

import com.solve.bookstore.domain.rental.model.Rental;
import com.solve.bookstore.domain.book.model.BookId;

public interface RentalRepository {

    Rental saveNew(Rental rental);
    Rental saveForUpdate(Rental rental);
    Rental findTopByBookId(BookId bookId);
    Long countByBookIdInAndReturnDateNull(BookId bookId);
}
