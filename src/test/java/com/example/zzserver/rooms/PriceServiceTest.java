package com.example.zzserver.rooms;

import com.example.zzserver.rooms.consts.DiscountType;
import com.example.zzserver.rooms.entity.DiscountPolicy;
import com.example.zzserver.rooms.entity.Rooms;
import com.example.zzserver.rooms.service.DiscountPolicyService;
import com.example.zzserver.rooms.service.PriceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PriceServiceTest {

    @Mock
    private DiscountPolicyService discountPolicyService;

    @InjectMocks
    private PriceService priceService;

    @Test
    @DisplayName("정상 가격 계산 - 기간할인 + 멤버십할인 적용")
    void calculatePrice_success() {
        Rooms room = Rooms.builder()
                .basePrice(BigDecimal.valueOf(100000))
                .build();

        long nights = 2;
        LocalDateTime now = LocalDateTime.now();

        DiscountPolicy weekday = DiscountPolicy.builder()
                .type(DiscountType.WEEKDAY)
                .rate(0.10)     // 10%
                .build();

        DiscountPolicy membership = DiscountPolicy.builder()
                .type(DiscountType.MEMBERSHIP)
                .rate(0.05)     // 5%
                .build();

        when(discountPolicyService.getApplicablePolicies(room, now))
                .thenReturn(List.of(weekday, membership));

        when(discountPolicyService.selectPeriodPolicy(List.of(weekday, membership)))
                .thenReturn(weekday);
        when(discountPolicyService.selectMembershipPolicy(List.of(weekday, membership)))
                .thenReturn(membership);

        BigDecimal result = priceService.calculatePrice(room, nights, now);

        // 계산 과정
        // 원가: 100000 * 2 = 200000
        // 기간 할인: 200000 - (10% = 20000) = 180000
        // 멤버십 할인: 180000 - (5% = 9000) = 171000
        assertThat(result.compareTo(BigDecimal.valueOf(171000))).isZero();
    }

    @Test
    @DisplayName("할인 정책이 없으면 원가 그대로")
    void calculatePrice_noDiscount() {
        Rooms room = Rooms.builder()
                .basePrice(BigDecimal.valueOf(150000))
                .build();

        long nights = 1;
        LocalDateTime now = LocalDateTime.now();

        when(discountPolicyService.getApplicablePolicies(room, now))
                .thenReturn(List.of());
        when(discountPolicyService.selectPeriodPolicy(List.of()))
                .thenReturn(null);
        when(discountPolicyService.selectMembershipPolicy(List.of()))
                .thenReturn(null);

        BigDecimal result = priceService.calculatePrice(room, nights, now);

        assertThat(result).isEqualTo(BigDecimal.valueOf(150000));
    }

    @Test
    @DisplayName("기간 할인만 존재")
    void onlyPeriodPolicy() {
        Rooms room = Rooms.builder()
                .basePrice(BigDecimal.valueOf(100000))
                .build();

        DiscountPolicy weekday = DiscountPolicy.builder()
                .type(DiscountType.WEEKDAY)
                .rate(0.20)
                .build();

        when(discountPolicyService.getApplicablePolicies(any(), any()))
                .thenReturn(List.of(weekday));
        when(discountPolicyService.selectPeriodPolicy(any())).thenReturn(weekday);
        when(discountPolicyService.selectMembershipPolicy(any())).thenReturn(null);

        BigDecimal result = priceService.calculatePrice(room, 1, LocalDateTime.now());

        assertThat(result.compareTo(BigDecimal.valueOf(80000))).isZero();
    }

    @Test
    @DisplayName("멤버십 할인만 존재")
    void onlyMembershipPolicy() {
        Rooms room = Rooms.builder()
                .basePrice(BigDecimal.valueOf(120000))
                .build();

        DiscountPolicy member = DiscountPolicy.builder()
                .type(DiscountType.MEMBERSHIP)
                .rate(0.15)
                .build();

        when(discountPolicyService.getApplicablePolicies(any(), any()))
                .thenReturn(List.of(member));
        when(discountPolicyService.selectPeriodPolicy(any())).thenReturn(null);
        when(discountPolicyService.selectMembershipPolicy(any())).thenReturn(member);

        BigDecimal result = priceService.calculatePrice(room, 2, LocalDateTime.now());

        assertThat(result.compareTo(BigDecimal.valueOf(204000))).isZero();
    }

    @Test
    @DisplayName("기간 정책이 여러 개일 때 우선순위 낮은 것이 선택됨")
    void multiplePeriodPolicies() {
        Rooms room = Rooms.builder()
                .basePrice(BigDecimal.valueOf(100000))
                .build();

        DiscountPolicy weekday = DiscountPolicy.builder()
                .type(DiscountType.WEEKDAY)
                .rate(0.10)
                .build();
        DiscountPolicy seasonal = DiscountPolicy.builder()
                .type(DiscountType.SEASONAL)
                .rate(0.30)
                .build();

        when(discountPolicyService.getApplicablePolicies(any(), any()))
                .thenReturn(List.of(seasonal, weekday));
        when(discountPolicyService.selectPeriodPolicy(any()))
                .thenReturn(weekday); // 우선순위 1

        when(discountPolicyService.selectMembershipPolicy(any()))
                .thenReturn(null);

        BigDecimal result = priceService.calculatePrice(room, 1, LocalDateTime.now());

        assertThat(result.compareTo(BigDecimal.valueOf(90000))).isZero();
    }

    @Test
    @DisplayName("숙박일수가 0일이면 항상 0원")
    void zeroNights() {
        Rooms room = Rooms.builder()
                .basePrice(BigDecimal.valueOf(100000))
                .build();

        BigDecimal result = priceService.calculatePrice(room, 0, LocalDateTime.now());

        assertThat(result.compareTo(BigDecimal.ZERO)).isZero();
    }

    @Test
    @DisplayName("basePrice = null → NPE 던짐")
    void nullBasePrice() {
        Rooms room = Rooms.builder()
                .basePrice(null)
                .build();

        assertThatThrownBy(() ->
                priceService.calculatePrice(room, 2, LocalDateTime.now()))
                .isInstanceOf(NullPointerException.class);
    }
}
