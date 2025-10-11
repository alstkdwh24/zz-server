package com.example.zzserver.payment.dto.response;

import com.example.zzserver.payment.consts.PaymentStatus;
import com.example.zzserver.payment.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class PaymentResponseDto {

    private UUID id;
    private UUID reservationId;
    private Long amount;
    private String method;
    private String merchantUid;
    private PaymentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
    private LocalDateTime canceledAt;

    public static PaymentResponseDto from(Payment payment) {
        return PaymentResponseDto
                .builder()
                .id(payment.getId())
                .reservationId(payment.getReservationId())
                .amount(payment.getAmount())
                .method(payment.getMethod())
                .merchantUid(payment.getMerchantUid())
                .status(payment.getStatus())
                .createdAt(payment.getCreatedAt())
                .approvedAt(payment.getApprovedAt())
                .canceledAt(payment.getCanceledAt())
                .build();
    }
}
