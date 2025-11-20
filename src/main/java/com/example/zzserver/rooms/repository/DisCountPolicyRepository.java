package com.example.zzserver.rooms.repository;

import com.example.zzserver.rooms.consts.DiscountScope;
import com.example.zzserver.rooms.consts.DiscountType;
import com.example.zzserver.rooms.entity.DiscountPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface DisCountPolicyRepository extends JpaRepository<DiscountPolicy,Long> {

    @Query("""
        select 
            p 
        from 
            DiscountPolicy p
        where
            p.active = true and :now between p.startDate and p.endDate
    """)
    List<DiscountPolicy> findActivePolicies(@Param("now") LocalDateTime now);

    @Query("""
        SELECT 
            CASE 
                WHEN COUNT(p) > 0 
            THEN 
                true 
            ELSE 
                false END
        FROM 
            DiscountPolicy p
        WHERE 
            p.active = true
        AND p.type = :type
        AND p.scope = :scope
        AND (:accommodationId IS NULL OR p.accommodationId = :accommodationId)
        AND (:roomId IS NULL OR p.roomId = :roomId)
        AND p.startDate < :endDate
        AND p.endDate > :startDate
    """)
    boolean existsConflict(
            @Param("type") DiscountType type,
            @Param("scope") DiscountScope scope,
            @Param("accommodationId") UUID accommodationId,
            @Param("roomId") UUID roomId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

}
