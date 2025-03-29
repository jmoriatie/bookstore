package com.solve.bookstore.infrastructure.entity;

import com.solve.bookstore.domain.Rental.model.RentalStatus;
import com.solve.bookstore.infrastructure.config.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "rentals")
@Getter
@NoArgsConstructor
public class RentalEntity extends BaseEntity {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private BookEntity book; // 도서 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity rentalUser; // 대여자 ID

    @Column(nullable = false)
    private LocalDateTime rentalDate; // 대여일자

    @Column(nullable = false)
    private LocalDateTime expectedReturnDate; // 반납예정일자
    private LocalDateTime returnDate; // 실제 반납일자

    @Column(nullable = false)
    private RentalStatus status; // 대여상태
}
