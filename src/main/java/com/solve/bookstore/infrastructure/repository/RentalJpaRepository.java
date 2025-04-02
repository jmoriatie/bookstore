package com.solve.bookstore.infrastructure.repository;

import com.solve.bookstore.infrastructure.entity.RentalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RentalJpaRepository extends JpaRepository<RentalEntity, String> {

    Long countByBook_IdInAndReturnDateNull(String bookId);
    Optional<RentalEntity> findTopByBook_IdIn(String bookId);
}
