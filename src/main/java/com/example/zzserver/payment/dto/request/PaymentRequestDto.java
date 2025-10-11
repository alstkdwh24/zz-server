package com.example.zzserver.payment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

public class PaymentRequestDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReadyRequest {
        private UUID reservationId;
        private Long amount;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SuccessRequest {
        private String portonePaymentId;
        private String merchantUid;
        private String method;
        private Long amount;
        private LocalDateTime approvedAt;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FailRequest {
        private String portonePaymentId;
        private String pgCode;
        private String pgMessage;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CancelRequest {
        private String portonePaymentId;
        private String pgCode;
        private String pgMessage;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefundRequest {
        private String portonePaymentId;
        private String reason;
    }
}
