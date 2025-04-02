package com.solve.bookstore.infrastructure.repository;

import com.solve.bookstore.infrastructure.entity.RentalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalJpaRepository extends JpaRepository<RentalEntity, String> {
}
