package com.example.zzserver.payment.repository;

import com.example.zzserver.payment.consts.PaymentStatus;
import com.example.zzserver.payment.entity.Payment;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    // 예약 ID로 결제 찾기
    Optional<Payment> findByReservationId(UUID reservationId);

    // 포트원 결제 ID로 결제 찾기
    Optional<Payment> findByPortonePaymentId(String portonePaymentId);

    // 상점 주문번호(merchantUid)로 결제 찾기
    Optional<Payment> findByMerchantUid(String merchantUid);

    // 결제 상태별 조회
    Optional<Payment> findByReservationIdAndStatus(UUID reservationId, PaymentStatus status);

    // merchantUid 기반 락
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Payment p WHERE p.merchantUid = :merchantUid")
    Optional<Payment> findByMerchantUidForUpdate(@Param("merchantUid") String merchantUid);

    // portonePaymentId 기반 락
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Payment p WHERE p.portonePaymentId = :portonePaymentId")
    Optional<Payment> findByPortonePaymentIdForUpdate(@Param("portonePaymentId") String portonePaymentId);

    // 예약 ID 기반으로 락 잡기 (예약 ↔ 결제 상태 전환 동기화)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Payment p WHERE p.reservationId = :reservationId")
    Optional<Payment> findByReservationIdForUpdate(@Param("reservationId") UUID reservationId);
}
