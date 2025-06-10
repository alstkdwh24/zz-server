package com.example.zzserver.member.repository;

import com.example.zzserver.member.entity.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class MemberRepository {

    private final EntityManager entityManager;

    public MemberRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void create(Member member) {
        entityManager.persist(member);
    }

    public Member findMemberById(UUID Id) {
        return entityManager.find(Member.class, Id);
    }

    public Optional<Member> findMemberByUserId(String userId) {
        TypedQuery<Member> typedQuery = entityManager.createQuery("SELECT m FROM Member m WHERE m.userId = :userId", Member.class);
        typedQuery.setParameter("userId", userId);


        try {
            Member member = typedQuery.getSingleResult();
            return Optional.ofNullable(member);
        } catch (NoResultException e) {
            return Optional.empty();
        }

    }

    public List<Member> findAll(){
        return entityManager.createQuery("SELECT m FROM Member m", Member.class).getResultList();
    }

    public List<Member> findByName(String name){
        return entityManager.createQuery("SELECT m FROM Member m WHERE m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }

}
