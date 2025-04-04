package com.solve.bookstore.application;

import com.solve.bookstore.application.dto.BookStatusChangeRequest;
import com.solve.bookstore.application.dto.RentSuccessResponse;
import com.solve.bookstore.application.dto.ReturnBookResponse;
import com.solve.bookstore.domain.rental.model.Rental;
import com.solve.bookstore.domain.rental.repository.RentalRepository;
import com.solve.bookstore.domain.user.model.User;
import com.solve.bookstore.domain.user.model.UserId;
import com.solve.bookstore.domain.user.repository.UserRepository;
import com.solve.bookstore.domain.book.model.Book;
import com.solve.bookstore.domain.book.model.BookId;
import com.solve.bookstore.domain.book.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class RentalService {

    private final BookRepository bookRepository;
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final BookService bookService;

    private final int RESPECTED_RETURN_DATE = 10; // 10일

    public RentalService(BookRepository bookRepository, RentalRepository rentalRepository, UserRepository userRepository, BookService bookService) {
        this.bookRepository = bookRepository;
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
        this.bookService = bookService;
    }

    /**
     * 도서 대여
     */
    public RentSuccessResponse rent(String bookId, String rentalUserId){
        Book book = bookService.getBook(bookId);
        validateRent(book);

        book.rent();
        Book savedBook = bookRepository.save(book);

        User rentalUser = userRepository.findById(new UserId(rentalUserId));
        Rental rental = Rental.create(
                book.getId(),
                rentalUser.getId(),
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(RESPECTED_RETURN_DATE));

        Rental savedRental = rentalRepository.saveNew(rental);
        return RentSuccessResponse.success(savedRental.getId().toString(), savedBook.getId().toString(), savedBook.getTitle());
    }

    /**
     * 도서 반납
     * 요구사항: 훼손, 분실로 도서 상태 변경
     */
    @Transactional
    public ReturnBookResponse returnBook(String bookId, BookStatusChangeRequest request) {
        Book book = bookService.getBook(bookId);
        validateReturnBook(book);

        // Rental 로직
        Rental rental = rentalRepository.findTopByBookId(book.getId());
        rental.returnCompleted();
        rentalRepository.saveForUpdate(rental);

        // Book 로직 - 반납시 도서의 상태변경(대여가능, 분실, 훼손 등)
        bookService.updateBookStatus(request.bookStatus(), book);
        Book savedBook =  bookRepository.save(book);

        return ReturnBookResponse.success(rental.getId().toString(), savedBook.getId().toString(), savedBook.getTitle());
    }

    private void validateRent(Book book){
        if(book.getStatus().isNotAvailable())
            throw new RuntimeException("대여가 불가능한 도서 입니다. ID: "+book.getId().toString());
        validateNotReturnHistory(book.getId());
    }
    private void validateReturnBook(Book book){
        if(book.getStatus().isAvailable())
            throw new RuntimeException("대여되지 않은 도서 입니다. ID: "+book.getId().toString());
        validateUpperTwoReturnHistory(book.getId());
    }
    private void validateNotReturnHistory(BookId bookId){
        Long rentalCount = rentalRepository.countByBookIdInAndReturnDateNull(bookId);
        if(rentalCount > 0)
            throw new RuntimeException("미반납 이력이 있는 도서입니다. bookId: "+ bookId.toString());
    }
    private void validateUpperTwoReturnHistory(BookId bookId) {
        Long rentalCount = rentalRepository.countByBookIdInAndReturnDateNull(bookId);
        if(rentalCount > 1)
            throw new RuntimeException("미반납 이력이 2개 이상입니다. 대여이력을 확인하세요 bookId: "+ bookId.toString());
    }
}
