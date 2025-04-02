package com.solve.bookstore.application;

import com.solve.bookstore.application.dto.BookStatusChangeResponse;
import com.solve.bookstore.domain.Rental.repository.RentalRepository;
import com.solve.bookstore.domain.book.model.Book;
import com.solve.bookstore.domain.book.model.BookId;
import com.solve.bookstore.domain.book.repository.BookRepository;
import org.springframework.transaction.annotation.Transactional;

public class RentalService {

    private final BookRepository bookRepository;
    private final RentalRepository rentalRepository;

    public RentalService(BookRepository bookRepository, RentalRepository rentalRepository) {
        this.bookRepository = bookRepository;
        this.rentalRepository = rentalRepository;
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

    /**
     * 도서 반납
     * 요구사항: 훼손, 분실로 도서 상태 변경
     */
    @Transactional
    public BookStatusChangeResponse returnBook(String bookId) {
        Book book = bookRepository.findById(new BookId(bookId));
        if(book.getStatus().isAvailable())
            throw new RuntimeException("대여되지 않은 도서 입니다. ID: "+bookId);

        book.returnBook();
        Book savedBook =  bookRepository.save(book);

        // TODO Rental domain 컨트롤

        return BookStatusChangeResponse.success(savedBook.getId().toString(), savedBook.getStatus().name());
    }
}
