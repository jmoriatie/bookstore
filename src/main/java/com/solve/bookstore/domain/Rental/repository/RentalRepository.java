package com.solve.bookstore.domain.Rental.repository;

import com.solve.bookstore.domain.Rental.model.Rental;
import com.solve.bookstore.domain.book.model.BookId;

public interface RentalRepository {

    Rental saveNew(Rental rental);
    Rental saveForUpdate(Rental rental);
    Rental findTopByBookId(BookId bookId);
    Long countByBookIdInAndReturnDateNull(BookId bookId);
}
