package com.example.zzserver.payment.consts;

public enum PaymentStatus {
    PENDING,   // 결제 준비
    SUCCESS,   // 결제 성공
    FAILED,    // 결제 실패
    CANCELED   // 환불/취소
}
