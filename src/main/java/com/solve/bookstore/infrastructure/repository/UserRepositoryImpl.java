package com.solve.bookstore.infrastructure.repository;

import com.solve.bookstore.domain.User.model.User;
import com.solve.bookstore.domain.User.model.UserId;
import com.solve.bookstore.domain.User.repository.UserRepository;
import com.solve.bookstore.infrastructure.entity.UserEntity;
import com.solve.bookstore.infrastructure.repository.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;

    @Override
    public User save(User user) {
        UserEntity entity = userJpaRepository.save(userMapper.toEntity(user));
        return userMapper.toDomain(entity);
    }

    @Override
    public User findById(UserId id) {
        UserEntity entity = userJpaRepository.findById(id.toString())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 유저입니다. userId: " + id));
        return userMapper.toDomain(entity);
    }

    @Override
    public void deleteAll() {
        userJpaRepository.deleteAll();
    }
}
