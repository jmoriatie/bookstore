package com.solve.bookstore.application;

import com.solve.bookstore.application.dto.BookStatusChangeResponse;
import com.solve.bookstore.application.dto.ReturnBookResponse;
import com.solve.bookstore.domain.Rental.model.Rental;
import com.solve.bookstore.domain.Rental.repository.RentalRepository;
import com.solve.bookstore.domain.book.model.Book;
import com.solve.bookstore.domain.book.model.BookId;
import com.solve.bookstore.domain.book.repository.BookRepository;
import org.springframework.transaction.annotation.Transactional;

public class RentalService {

    private final BookRepository bookRepository;
    private final RentalRepository rentalRepository;
    private final BookService bookService;

    public RentalService(BookRepository bookRepository, RentalRepository rentalRepository, BookService bookService) {
        this.bookRepository = bookRepository;
        this.rentalRepository = rentalRepository;
        this.bookService = bookService;
    }


    // TODO 구현 -> 메서드 분리 -> Class 분리 단계별 리팩토링 필요

    // TODO 훼손, 분실 대여 중단
    //  - 대여 불가로 변경 여부 확인 isAvailableForRental

    // --- 필요 ---
    // TODO CRUD 더?
    // TODO Exception 커스텀

    // NOTE 카운터에서 대여 관리자가 바코드를 찍고 입력하는 등의 시나리오
    // TODO 서치한 책과 동일한 대여가능 책 확인 - ISBN
    //  count & 실제 대여 가능 책 있는지 여부 확인

    /**
     * 도서 대여
     */
    public void rent(){
        // 대여 가능 여부 확인
    }

    // TODO TEST
    /**
     * 도서 반납
     * 요구사항: 훼손, 분실로 도서 상태 변경
     */
    @Transactional
    public ReturnBookResponse returnBook(String bookId) {
        Book book = bookService.getBook(bookId);
        if(book.getStatus().isAvailable())
            throw new RuntimeException("대여되지 않은 도서 입니다. ID: "+bookId);

        // Rental 로직
        validateReturnHistory(book.getId());
        Rental rental = rentalRepository.findTopByBookId(book.getId());
        rental.returnCompleted();
        rentalRepository.saveForUpdate(rental);

        // Book 로직
        book.returnBook();
        Book savedBook =  bookRepository.save(book);

        return ReturnBookResponse.success(savedBook.getId().toString(), savedBook.getTitle());
    }

    private void validateReturnHistory(BookId bookId) {
        Long rentalCount = rentalRepository.countByBookIdInAndReturnDateNull(bookId);
        if(rentalCount > 1)
            throw new RuntimeException("미반납 이력이 2개 이상입니다. 대여이력을 확인하세요 bookId: "+ bookId.toString());
    }
}
