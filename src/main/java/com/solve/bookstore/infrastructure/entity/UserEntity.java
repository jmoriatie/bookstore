package com.solve.bookstore.infrastructure.entity;

import com.solve.bookstore.infrastructure.config.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserEntity extends BaseEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String tel;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    public UserEntity(String id, String name, String tel, String email, String password, String role) {
        this.id = id;
        this.name = name;
        this.tel = tel;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
