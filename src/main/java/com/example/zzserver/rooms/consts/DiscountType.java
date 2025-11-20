package com.example.zzserver.rooms.consts;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DiscountType {
    WEEKDAY(3),       // 평일 할인
    SEASONAL(2),      // 시즌 할인
    MEMBERSHIP(1);    // 쿠폰 할인

    private final int priority; // 숫자 작을수록 우선순위 높음
}
