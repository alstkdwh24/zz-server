package com.example.zzserver.member.repository.jpa;

import com.example.zzserver.member.entity.Members;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface MemberRepository extends JpaRepository<Members, UUID> {
    Optional<Members> findMemberByEmail(String email);
}
