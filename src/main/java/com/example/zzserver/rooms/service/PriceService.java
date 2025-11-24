package com.example.zzserver.rooms.service;

import com.example.zzserver.config.exception.CustomException;
import com.example.zzserver.config.exception.ErrorCode;
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
     * @param nights 숙박일수 (체크인에서 체크아웃사이의 기간)
     * @param now 현재 시간
     * @param room 방 객체
     * @return 할인율
     **/
    public BigDecimal calculatePrice(Rooms room,
                                     long nights,
                                     LocalDateTime now) {

        if (room.getBasePrice() == null) {
            throw new CustomException(ErrorCode.BASE_PRICE_NULL);
        }

        if (nights < 0) {
            throw new CustomException(ErrorCode.ACCOMMODATION_DAYS_NEGATIVE);
        }

        if (nights == 0) {
            throw new CustomException(ErrorCode.ACCOMMODATION_ZERO_NIGHTS);
        }

        BigDecimal original = room.getBasePrice()
                .multiply(BigDecimal.valueOf(nights));

        List<DiscountPolicy> policies = discountPolicyService.getApplicablePolicies(room, now);

        // 1.기간 할인 (WEEKDAY or SEASONAL 중 하나만 선택)
        DiscountPolicy periodPolicy = discountPolicyService.selectPeriodPolicy(policies);

        if (periodPolicy != null) {
            validateRate(periodPolicy.getRate());
            original = apply(original, periodPolicy.getRate());
        }

        // 2.멤버십 할인
        DiscountPolicy candidates = discountPolicyService.selectMembershipPolicy(policies);

        if (candidates != null) {
            validateRate(candidates.getRate());
            original = apply(original, candidates.getRate());
        }

        // 최종값 음수 방지
        if (original.compareTo(BigDecimal.ZERO) < 0) {
            throw new CustomException(ErrorCode.PRICE_NOT_NEGATIVE);
        }

        return original;
    }

    // 할인율 유효성 검사
    private void validateRate(Double rate) {
        if (rate == null) {
            throw new CustomException(ErrorCode.RATE_NOT_NULL);
        }
        if (rate < 0 || rate > 1) {
            throw new CustomException(ErrorCode.INVALID_RATE_RANGE);
        }
    }

    //할인율 적용
    private BigDecimal apply(BigDecimal price, double rate) {
        return price.subtract(price.multiply(BigDecimal.valueOf(rate)));
    }
}
