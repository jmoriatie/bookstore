package com.solve.bookstore.application;

import com.solve.bookstore.application.dto.BookStatusChangeRequest;
import com.solve.bookstore.application.dto.RentSuccessResponse;
import com.solve.bookstore.application.dto.ReturnBookResponse;
import com.solve.bookstore.domain.Rental.model.Rental;
import com.solve.bookstore.domain.Rental.model.RentalStatus;
import com.solve.bookstore.domain.Rental.repository.RentalRepository;
import com.solve.bookstore.domain.User.model.User;
import com.solve.bookstore.domain.User.repository.UserRepository;
import com.solve.bookstore.domain.book.model.Book;
import com.solve.bookstore.domain.book.model.BookStatus;
import com.solve.bookstore.domain.book.model.Isbn;
import com.solve.bookstore.domain.book.repository.BookRepository;
import com.solve.bookstore.infrastructure.entity.RentalEntity;
import com.solve.bookstore.infrastructure.repository.RentalJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest(properties = {
        "logging.level.com.solve.bookstore.application=DEBUG",
        "logging.level.com.solve.bookstore.infrastructure=DEBUG"
})
class RentalServiceTest {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RentalJpaRepository rentalJpaRepository;
    @Autowired
    private RentalRepository rentalRepository;
    @Autowired
    private RentalService rentalService;
    @Autowired
    TransactionTemplate transactionTemplate;

    User savedUser;
    Book savedBook;

    @BeforeEach
    void beforeEach(){
        bookRepository.deleteAll();
        userRepository.deleteAll();
        log.debug("### success bookRepository.deleteAll()");
        log.debug("### success userRepository.deleteAll()");

        initData();
    }
    @AfterEach
    void afterEach(){
        rentalJpaRepository.deleteAll(); // 이 후 서비스 테스트에 rental 데이터 영향 없애기 위해 변경
        log.debug("### success rentalJpaRepository.deleteAll()");
    }

    @Test
    @DisplayName("도서 대여(create)")
    void rent() {
        // given & when
        RentSuccessResponse response = rentalService.rent(savedBook.getId().toString(), savedUser.getId().toString());
        RentalEntity foundRental = rentalJpaRepository.findById(response.rentalId()).get();

        // then
        assertEquals(response.rentalId(), foundRental.getId());
        assertEquals(response.bookId(), savedBook.getId().toString());
        assertEquals(response.bookTitle(), savedBook.getTitle());
        assertEquals(foundRental.getRentalUser().getId(), savedUser.getId().toString());
    }

    @Test
    @DisplayName("도서 반납")
    void returnBook() {
        // given
        BookStatusChangeRequest bookStatusRequest = new BookStatusChangeRequest(BookStatus.AVAILABLE.name());
        Rental rental = Rental.create(
                savedBook.getId(),
                savedUser.getId(),
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().plusDays(5));
        Rental savedRental = rentalRepository.saveNew(rental);

        savedBook.rent();
        bookRepository.save(savedBook);
        // when
        ReturnBookResponse response = rentalService.returnBook(savedRental.getBookId().toString(), bookStatusRequest);
        RentalEntity foundRental = rentalJpaRepository.findById(response.rentalId()).get();

        String foundBookStatus = transactionTemplate.execute(status -> {
            RentalEntity tempRental = rentalJpaRepository.findById(response.rentalId()).get();
            return tempRental.getBook().getStatus();
        });

        // then
        assertEquals(response.rentalId(), foundRental.getId());
        assertEquals(response.bookId(), foundRental.getBook().getId());
        assertNotNull(foundRental.getRentalDate());
        assertEquals(foundBookStatus, BookStatus.AVAILABLE.name());
    }


    private void initData(){
        User user = new User(null, "나빌림", "01011119999");
        savedUser = userRepository.save(user);

        Book book = Book.create(
                "책은 도서다.",
                "나작가",
                "책설명",
                new Isbn("ISBN-new"));
        savedBook = bookRepository.save(book);
    }
}