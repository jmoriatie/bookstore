package com.solve.bookstore.domain.Rental.model;

import com.solve.bookstore.domain.User.model.UserId;
import com.solve.bookstore.domain.book.model.BookId;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Rental {
    private RentalId id;
    private BookId bookId; // 도서 ID
    private UserId rentalUserId; // 대여자 ID
    private LocalDateTime rentalDate; // 대여일자
    private LocalDateTime expectedReturnDate; // 반납예정일자
    private LocalDateTime returnDate; // 실제 반납일자
    private RentalStatus status; // 대여상태

    public Rental(RentalId id, BookId bookId, UserId rentalUserId, LocalDateTime rentalDate, LocalDateTime expectedReturnDate) {
        validate(bookId, rentalUserId, rentalDate, expectedReturnDate);

        this.id = id != null? id : new RentalId();
        this.bookId = bookId;
        this.rentalUserId = rentalUserId;
        this.rentalDate = rentalDate;
        this.expectedReturnDate = expectedReturnDate;
        this.status = RentalStatus.ACTIVE;
    }

    private void validate(BookId bookId, UserId rentalUserId, LocalDateTime rentalDate, LocalDateTime expectedReturnDat){
        if(bookId == null) throw new IllegalArgumentException("도서 ID는 필수값입니다.");
        if(rentalUserId == null) throw new IllegalArgumentException("대여자 ID는 필수값입니다.");
        if(rentalDate == null) throw new IllegalArgumentException("대여일자는 필수값입니다.");
        if(expectedReturnDat == null) throw new IllegalArgumentException("반납 예정 일자는 필수값입니다.");
        if(expectedReturnDat.isBefore(LocalDateTime.now())){
            throw new IllegalArgumentException("반납 예정 일자는 현재 이후로 설정해주세요.");
        }
        if(expectedReturnDat.isBefore(rentalDate)){
            throw new IllegalArgumentException("반납 예정 일자는 대여일자 이후로 설정해주세요.");
        }
    }

    public void returnCompleted(){
        if(this.status == RentalStatus.RETURNED){
            throw new IllegalArgumentException("이미 반납된 도서 입니다.");
        }
        this.returnDate = LocalDateTime.now();
        this.status = RentalStatus.RETURNED;
    }

    // TODO 연체로 변경하는 스케쥴링 서비스 필요
    public void overdue(){
        if(LocalDateTime.now().isAfter(this.expectedReturnDate)){
            this.status = RentalStatus.OVERDUE;
        }
    }
}
