package com.solve.bookstore.infrastructure.repository;

import com.solve.bookstore.infrastructure.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserEntity, String> {
}
