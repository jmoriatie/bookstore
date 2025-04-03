package com.solve.bookstore.infrastructure.repository.mapper;

import com.solve.bookstore.domain.user.model.User;
import com.solve.bookstore.domain.user.model.UserId;
import com.solve.bookstore.domain.user.model.UserRole;
import com.solve.bookstore.infrastructure.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    /**
     * Entity -> Domain
     */
    public User toDomain(UserEntity entity){
        if(entity == null) return null;
        return User.of(new UserId(entity.getId()), entity.getName(), entity.getTel(), entity.getEmail(), entity.getPassword(), UserRole.valueOf(entity.getRole()));
    }

    /**
     * Domain -> Entity
     */
    public UserEntity toEntity(User domain){
        if(domain == null) return null;
        return new UserEntity(domain.getId().toString(), domain.getName(), domain.getTel(), domain.getEmail(), domain.getPassword(), domain.getRole().name());
    }
}
