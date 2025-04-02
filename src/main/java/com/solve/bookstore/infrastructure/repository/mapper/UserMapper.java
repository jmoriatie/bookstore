package com.solve.bookstore.infrastructure.repository.mapper;

import com.solve.bookstore.domain.User.model.User;
import com.solve.bookstore.domain.User.model.UserId;
import com.solve.bookstore.infrastructure.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    /**
     * Entity -> Domain
     */
    public User toDomain(UserEntity entity){
        if(entity == null) return null;
        return new User(new UserId(entity.getId()), entity.getName(), entity.getTel());
    }

    /**
     * Domain -> Entity
     */
    public UserEntity toEntity(User domain){
        if(domain == null) return null;
        return new UserEntity(domain.getId().toString(), domain.getName(), domain.getTel());
    }
}
