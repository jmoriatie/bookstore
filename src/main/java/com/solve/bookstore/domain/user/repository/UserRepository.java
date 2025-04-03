package com.solve.bookstore.domain.user.repository;

import com.solve.bookstore.domain.user.model.User;
import com.solve.bookstore.domain.user.model.UserId;

public interface UserRepository {
    User save(User user);
    User findById(UserId id);
    void deleteAll();
    User findByEmail(String email);
}
