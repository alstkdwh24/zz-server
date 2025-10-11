package com.example.zzserver.payment.dto.response;

import lombok.*;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PortOnePaymentDto {

    private int code;
    private String message;
    private PaymentResponse response;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class PaymentResponse {
        private String imp_uid;
        private String merchant_uid;
        private Long amount;
        private String status;
        private String pay_method;
        private Long paid_at;
    }
}
