package com.solve.bookstore.domain.User.repository;

import com.solve.bookstore.domain.User.model.User;
import com.solve.bookstore.domain.User.model.UserId;

public interface UserRepository {
    User findById(UserId id);
}
