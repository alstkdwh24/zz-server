package com.example.zzserver.member.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Entity
@Table(name = "MEMBER")
public class Members {

    @jakarta.persistence.Id
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @Column(name = "userPw", nullable = false)

    private String userPw;
    @Column(name = "email", length = 50, updatable = false, unique = true)

    private String email;
    @Column(name = "name", nullable = false)

    private String name;

    @Column(name = "nickname", nullable = false, unique = true)
    private String nickname;

    @Enumerated(EnumType.STRING) // Enum 타입으로 변경
    private Role role;

    public Members() {
    }
    public Members(UUID id, String userPw, String email, String name, Role role) {
        this.id = id;
        this.userPw = userPw;
        this.email = email;
        this.name = name;
        this.role = role;
    }
    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }





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
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

}
