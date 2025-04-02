package com.solve.bookstore.infrastructure.repository;

import com.solve.bookstore.infrastructure.entity.RentalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RentalJpaRepository extends JpaRepository<RentalEntity, String> {

    @Query("SELECT COUNT(r) FROM RentalEntity r WHERE r.book.id = :bookId")
    Long countByBook_IdInAndReturnDateNull(String bookId);

    @Query("SELECT r FROM RentalEntity r WHERE r.book.id = :bookId")
    Optional<RentalEntity> findTopByBook_IdIn(String bookId);
}
