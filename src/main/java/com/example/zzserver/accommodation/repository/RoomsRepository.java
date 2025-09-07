package com.example.zzserver.accommodation.repository;

import com.example.zzserver.accommodation.entity.Rooms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;

public interface RoomsRepository extends JpaRepository<Rooms, UUID> {

    List<Rooms> findByAccommodationId(UUID accommodationId);

    // 방 조회 (비관적 락 적용)
    @Lock(PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Rooms r WHERE r.id = :roomId")
    Optional<Rooms> findByIdForUpdate(@Param("roomId") UUID roomId);
}
