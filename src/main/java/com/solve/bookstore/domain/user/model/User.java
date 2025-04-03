package com.solve.bookstore.domain.user.model;

import lombok.Getter;

@Getter
public class User {
    private UserId id;
    private String name;
    private String email;
    private String tel;
    private String password;
    private UserRole role;

    private User(UserId id, String name, String tel, String email, String password, UserRole role) {
        validate(name, tel, email, password, role);
        this.id = id != null? id : new UserId();
        this.name = name;
        this.tel = tel;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    private void validate(String name, String tel, String email, String password, UserRole role){
        if(name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("대여자 이름은 필수 값 입니다.");
        if(tel == null || tel.trim().isEmpty())
            throw new IllegalArgumentException("대여자 전화번호는 필수 값 입니다.");
        if(email == null || email.trim().isEmpty())
            throw new IllegalArgumentException("대여자 이메일은 필수 값 입니다.");
        if(password == null || password.trim().isEmpty())
            throw new IllegalArgumentException("대여자 비밀번호는 필수 값 입니다.");
        if(role == null )
            throw new IllegalArgumentException("고객 Role 은 필수 값 입니다.");
    }

    public static User create(String name, String tel, String email, String password, UserRole role){
        return new User(null, name, tel, email, password, role);
    }

    public static User of(UserId id, String name, String tel, String email, String password, UserRole role){
        return new User(id, name, tel, email, password, role);
    }
}
