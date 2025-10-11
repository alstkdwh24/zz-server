package com.example.zzserver.member.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "MEMBER")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Members {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "userPw")

    private String userPw;
    @Column(name = "email", length = 50, updatable = false, unique = true)

    private String email;
    @Column(name = "name")

    private String name;

    @Column(name = "nickname", unique = true)
    private String nickname;

    @Enumerated(EnumType.STRING) // Enum 타입으로 변경
    @Column(name = "role", nullable = false)

    private Role role;


    public Members(UUID id, String userPw, String email, String name, Role role, String nickname) {
        if (id == null) throw new IllegalArgumentException("id is null or empty");

        if (nickname == null) throw new IllegalArgumentException("nickname cannot be null or empty");
        if (userPw == null || userPw.isEmpty()) throw new IllegalArgumentException("userPw cannot be null or empty");
        if (email == null || email.isEmpty()) throw new IllegalArgumentException("email cannot be null or empty");
        if (name == null || name.isEmpty()) throw new IllegalArgumentException("name cannot be null or empty");
        if (role == null) throw new IllegalArgumentException("role cannot be null or empty");


        this.nickname = nickname;
        this.id = id;
        this.userPw = userPw;
        this.email = email;
        this.name = name;
        this.role = role;
    }


    public void ChangeUserPw(String userPw) {
        if (userPw != null && !userPw.isEmpty())
            this.userPw = userPw;
    }


    public void ChangeEmail(String email) {
        if (email != null && !email.isEmpty()) {
            this.email = email;
        }
    }

    public void ChangeName(String name) {
        if (name != null && !name.isEmpty()) {
            this.name = name;
        }
    }


    public void ChangeRole(Role role) {
        if (role == null) throw new IllegalArgumentException("role cannot be null or empty");
        this.role = role;
    }

    public void ChangeNickname(String nickname) {
        if (nickname == null || nickname.isEmpty())
            throw new IllegalArgumentException("nickname cannot be null or empty");
        this.nickname = nickname;
    }

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID(); // 가입 시에만 UUID 생성
        }
    }
}
