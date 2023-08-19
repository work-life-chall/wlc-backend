package com.bonfire.challenge.entity;

import jakarta.persistence.*;
import lombok.*;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String comCode;

    private int role;

//    @Builder
//    public User(String id, String name) {
//        this.id = id;
//        this.name = name;
//    }
}
