package com.solve.bookstore.infrastructure.repository.mapper;

import com.solve.bookstore.domain.Rental.model.Rental;
import com.solve.bookstore.domain.Rental.model.RentalId;
import com.solve.bookstore.domain.User.model.UserId;
import com.solve.bookstore.domain.book.model.BookId;
import com.solve.bookstore.infrastructure.entity.BookEntity;
import com.solve.bookstore.infrastructure.entity.RentalEntity;
import com.solve.bookstore.infrastructure.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class RentalMapper {

    /**
     * Entity -> Domain
     */
    public Rental toDomain(RentalEntity entity){
        if(entity == null) return null;
        return Rental.rebuild(
                new RentalId(entity.getId()),
                new BookId(entity.getBook().getId()),
                new UserId(entity.getRentalUser().getId()),
                entity.getRentalDate(),
                entity.getReturnDate(),
                entity.getExpectedReturnDate(),
                entity.getStatus()
        );
    }

    /**
     * Domain -> Entity
     */
    public RentalEntity toEntity(Rental domain, BookEntity book, UserEntity rentalUser){
        if(domain == null || book == null || rentalUser == null) return null;
        return RentalEntity.from(domain.getId().toString(), book, rentalUser, domain.getRentalDate(), domain.getExpectedReturnDate(), domain.getReturnDate(), domain.getStatus());
    }

    public void updateEntityFromDomain(RentalEntity rentalEntity, Rental domain){
        if(rentalEntity == null || domain == null) return;
        rentalEntity.update(domain.getExpectedReturnDate(), domain.getReturnDate(), domain.getStatus());
    }
}
