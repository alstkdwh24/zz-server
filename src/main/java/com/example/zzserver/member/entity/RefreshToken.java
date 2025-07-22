package com.example.zzserver.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "refresh_token")
public class RefreshToken {



   @Id
   private UUID id = UUID.randomUUID();


    @Column
    private String refresh_token;

   public RefreshToken() {}
    public RefreshToken(UUID id, String refresh_token) {
       this.id = id;
       this.refresh_token = refresh_token;
    }

    public void setRefresh_token(String refreshToken) {

        this.refresh_token = refreshToken;

    }
    public String getRefresh_token() {
        return refresh_token;
    }
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
}
