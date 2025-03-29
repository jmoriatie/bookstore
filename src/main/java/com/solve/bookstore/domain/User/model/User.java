package com.solve.bookstore.domain.User.model;

import lombok.Getter;

@Getter
public class User {
    private UserId id;
    private String name;
    private String tel;

    public User(UserId id, String name, String tel) {
        validate(name, tel);
        this.id = id != null? id : new UserId();
        this.name = name;
        this.tel = tel;
    }

    private void validate(String name, String tel){
        if(name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("대여자 이름은 필수 값 입니다.");
        if(tel == null || tel.trim().isEmpty())
            throw new IllegalArgumentException("대여자 전화번호는 필수 값 입니다.");
    }
}
