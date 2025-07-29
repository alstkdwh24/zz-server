package com.example.zzserver.member.repository;

import com.example.zzserver.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {
    Optional<Member> findMemberByEmail(String email);
}
