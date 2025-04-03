package com.solve.bookstore.infrastructure.repository;

import com.solve.bookstore.domain.user.model.User;
import com.solve.bookstore.domain.user.model.UserId;
import com.solve.bookstore.domain.user.repository.UserRepository;
import com.solve.bookstore.infrastructure.entity.UserEntity;
import com.solve.bookstore.infrastructure.repository.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;

    @Override
    public User save(User user) {
        UserEntity entity = userJpaRepository.save(userMapper.toEntity(user));
        log.info("saved User id={}, name={}, tel={}, email={}, password={}, role={}", entity.getId(),entity.getName(), entity.getTel(), entity.getEmail(), entity.getPassword(), entity.getRole());
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

    @Override
    public User findByEmail(String email) {
        UserEntity entity = userJpaRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 고객 이메일 입니다. email: " + email));
        return userMapper.toDomain(entity);
    }
}
