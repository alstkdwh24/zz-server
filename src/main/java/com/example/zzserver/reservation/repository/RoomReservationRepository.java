package com.example.zzserver.reservation.repository;

import com.example.zzserver.reservation.consts.ReservationStatus;
import com.example.zzserver.reservation.entity.RoomReservations;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomReservationRepository extends JpaRepository<RoomReservations, UUID> {

    // 유저별 예약 내역
    @Query("SELECT r FROM RoomReservations r WHERE r.memberId = :memberId")
    List<RoomReservations> findByMemberId(@Param("memberId") UUID memberId);

    // 특정 방(Room) 예약 내역
    @Query("SELECT r FROM RoomReservations r WHERE r.roomId = :roomId")
    List<RoomReservations> findByRoomId(@Param("roomId") UUID roomId);

    // 상태별 예약 조회
    @Query("SELECT r FROM RoomReservations r WHERE r.status = :status")
    List<RoomReservations> findByStatus(@Param("status") ReservationStatus status);

    // 유저 + 상태 조건 조회
    @Query("SELECT r FROM RoomReservations r WHERE r.memberId = :memberId AND r.status = :status")
    List<RoomReservations> findByMemberIdAndStatus(@Param("memberId") UUID memberId, @Param("status") ReservationStatus status);

    // 예약 겹침 여부 체크
    @Query("""
        SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
        FROM RoomReservations r
        WHERE r.roomId = :roomId
          AND r.status = 'CONFIRMED'
          AND r.checkIn < :checkOut
          AND r.checkOut > :checkIn
    """)
    boolean existsOverlappingReservation(
            @Param("roomId") UUID roomId,
            @Param("checkIn") LocalDateTime checkIn,
            @Param("checkOut") LocalDateTime checkOut
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM RoomReservations r WHERE r.id = :id")
    Optional<RoomReservations> findByIdForUpdate(@Param("id") UUID id);
}
