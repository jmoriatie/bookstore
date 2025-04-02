package com.solve.bookstore.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RentalRepositoryImpl {

    private final RentalJpaRepository rentalJpaRepository;

}
