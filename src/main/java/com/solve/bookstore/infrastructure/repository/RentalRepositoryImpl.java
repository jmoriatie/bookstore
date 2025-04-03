package com.solve.bookstore.infrastructure.repository;

import com.solve.bookstore.domain.rental.model.Rental;
import com.solve.bookstore.domain.rental.repository.RentalRepository;
import com.solve.bookstore.domain.book.model.BookId;
import com.solve.bookstore.infrastructure.entity.BookEntity;
import com.solve.bookstore.infrastructure.entity.RentalEntity;
import com.solve.bookstore.infrastructure.entity.UserEntity;
import com.solve.bookstore.infrastructure.repository.mapper.RentalMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RentalRepositoryImpl implements RentalRepository {

    private final RentalJpaRepository rentalJpaRepository;
    private final BookJpaRepository bookJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final RentalMapper rentalMapper;

    @Override
    public Rental saveNew(Rental rental) {
        BookEntity bookEntity = bookJpaRepository.findById(rental.getBookId().toString())
                .orElseThrow(() -> new IllegalArgumentException("없는 BookId 입니다. id=" + rental.getBookId().toString()));
        UserEntity rentalUserEntity = userJpaRepository.findById(rental.getRentalUserId().toString())
                .orElseThrow(() -> new IllegalArgumentException("없는 UserId 입니다. id=" + rental.getRentalUserId().toString()));
        RentalEntity entity = rentalJpaRepository.save(rentalMapper.toEntity(rental, bookEntity, rentalUserEntity));
        return rentalMapper.toDomain(entity);
    }

    @Override
    public Rental saveForUpdate(Rental rental) {
        RentalEntity foundEntity = rentalJpaRepository.findById(rental.getId().toString())
                .orElseThrow(() -> new IllegalArgumentException("없는 대여 정보 입니다. id=" + rental.getRentalUserId().toString()));
        rentalMapper.updateEntityFromDomain(foundEntity, rental);
        RentalEntity entity = rentalJpaRepository.save(foundEntity);
        return rentalMapper.toDomain(entity);
    }

    // TODO update???


    @Override
    public Rental findTopByBookId(BookId bookId) {
        RentalEntity entity = rentalJpaRepository.findTopByBook_IdIn(bookId.toString())
                .orElseThrow();

        return rentalMapper.toDomain(entity);
    }

    @Override
    public Long countByBookIdInAndReturnDateNull(BookId bookId) {
        return rentalJpaRepository.countByBook_IdInAndReturnDateNull(bookId.toString());
    }
}
