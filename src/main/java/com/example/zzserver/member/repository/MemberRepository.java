package com.example.zzserver.member.repository;

import com.example.zzserver.member.entity.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {
    Optional<Member> findMemberByUserId(String userId);
    List<Member> findByName(String name);
}
