package com.example.zzserver.member.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Entity
@Table(name = "MEMBER")
public class Member {

    @jakarta.persistence.Id
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;
//    @Column(name = "userId", nullable = false, unique = true)
//    private String userId;
    @Column(name = "userPw", nullable = false)

    private String userPw;
    @Column(name = "email", length = 50, updatable = false, unique = true)

    private String email;
    @Column(name = "name", nullable = false)

    private String name;

    @Enumerated(EnumType.STRING) // Enum 타입으로 변경
    private Role role;

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

//    public String getUserId() {
//        return userId;
//    }
//
//    public void setUserId(String userId) {
//        this.userId = userId;
//
//    }

    public String getUserPw() {
        return userPw;
    }

    public void setUserPw(String userPw) {
        this.userPw = userPw;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

}
