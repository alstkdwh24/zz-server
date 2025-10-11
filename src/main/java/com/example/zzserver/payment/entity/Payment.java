package com.example.zzserver.payment.entity;

import com.example.zzserver.payment.consts.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@ToString
@Entity
@Getter
@Table(name="PAYMENT")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // ROOM_RESERVATION 의 외래키
    @Column(nullable = false)
    private UUID reservationId;

    @Column(unique = true, length = 100)
    private String portonePaymentId;

    @Column(unique = true, nullable = false, length = 400)
    private String merchantUid;

    private Long amount;

    private String method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    private String pgCode;
    private String pgMessage;

    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
    private LocalDateTime canceledAt;

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = PaymentStatus.PENDING;
            this.createdAt = LocalDateTime.now();
        }
    }

    public void markSuccess(String impUid, String method, LocalDateTime approvedAt) {
        if (this.status != PaymentStatus.PENDING) {
            throw new IllegalStateException("PENDING 상태에서만 SUCCESS 전환 가능");
        }
        this.portonePaymentId = impUid;
        this.status = PaymentStatus.SUCCESS;
        this.method = method;
        this.approvedAt = approvedAt;
    }

    public void markFail(String code, String message) {
        if (this.status == PaymentStatus.SUCCESS) {
            throw new IllegalStateException("이미 성공한 결제는 FAIL로 변경 불가");
        }
        this.status = PaymentStatus.FAILED;
        this.pgCode = code;
        this.pgMessage = message;
    }

    public void markCanceled(String code, String message) {
        if (this.status == PaymentStatus.CANCELED) {
            throw new IllegalStateException("이미 취소된 결제입니다.");
        }
        this.status = PaymentStatus.CANCELED;
        this.pgCode = code;
        this.pgMessage = message;
        this.canceledAt = LocalDateTime.now();
    }
}
