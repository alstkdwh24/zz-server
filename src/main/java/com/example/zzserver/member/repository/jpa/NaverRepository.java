package com.example.zzserver.member.repository.jpa;

import com.example.zzserver.member.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NaverRepository extends JpaRepository<RefreshToken, UUID> {
}
