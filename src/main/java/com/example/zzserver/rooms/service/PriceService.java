package com.example.zzserver.rooms.service;

import com.example.zzserver.rooms.entity.DiscountPolicy;
import com.example.zzserver.rooms.entity.Rooms;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Service
@AllArgsConstructor
public class PriceService {

    private final DiscountPolicyService discountPolicyService;

    /**
     * 할인율 계산
     * @param nights 밤낮여부
     * @param now 현재 시간
     * @param room 방 객체
     * @return 할인율
     **/
    public BigDecimal calculatePrice(Rooms room,
                                     long nights,
                                     LocalDateTime now) {

        BigDecimal original = room.getBasePrice()
                .multiply(BigDecimal.valueOf(nights));

        List<DiscountPolicy> policies = discountPolicyService.getApplicablePolicies(room, now);

        // 1.기간 할인 (WEEKDAY or SEASONAL 중 하나만 선택)
        DiscountPolicy periodPolicy = discountPolicyService.selectPeriodPolicy(policies);

        if (periodPolicy != null) {
            original = apply(original, periodPolicy.getRate());
        }

        // 2.멤버십 할인
        DiscountPolicy candidates = discountPolicyService.selectMembershipPolicy(policies);

        if (candidates != null) {
            original = apply(original, candidates.getRate());
        }
        return original;
    }

    //할인율 적용
    private BigDecimal apply(BigDecimal price, double rate) {
        return price.subtract(price.multiply(BigDecimal.valueOf(rate)));
    }
}
