package com.solve.bookstore.infrastructure.entity;

import com.solve.bookstore.infrastructure.config.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
public class UserEntity extends BaseEntity {

    @Id
    private String id;

    private String name;
    private String tel;

    @OneToMany(mappedBy = "rentalUser")
    List<RentalEntity> rentals = new ArrayList<>();

    public UserEntity(String id, String name, String tel) {
        this.id = id;
        this.name = name;
        this.tel = tel;
    }
}
