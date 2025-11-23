package com.example.zzserver.rooms;

import com.example.zzserver.config.exception.CustomException;
import com.example.zzserver.config.exception.ErrorCode;
import com.example.zzserver.rooms.consts.DiscountScope;
import com.example.zzserver.rooms.consts.DiscountType;
import com.example.zzserver.rooms.entity.DiscountPolicy;
import com.example.zzserver.rooms.entity.Rooms;
import com.example.zzserver.rooms.repository.DisCountPolicyRepository;
import com.example.zzserver.rooms.service.DiscountPolicyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DiscountPolicyServiceTest {

    @Mock
    private DisCountPolicyRepository repository;

    @InjectMocks
    private DiscountPolicyService policyService;

    @Test
    @DisplayName("정책 저장 성공")
    void savePolicy_success() {

        DiscountPolicy policy = DiscountPolicy
                .builder()
                .type(DiscountType.WEEKDAY)
                .scope(DiscountScope.ROOM)
                .accommodationId(UUID.randomUUID())
                .roomId(UUID.randomUUID())
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(5))
                .build();

        when(repository.existsConflict(
                any(), any(), any(), any(), any(), any()
        )).thenReturn(false);

        when(repository.save(any())).thenReturn(policy);

        DiscountPolicy saved = policyService.savePolicy(policy);

        assertThat(saved).isNotNull();
        assertThat(saved.getType()).isEqualTo(DiscountType.WEEKDAY);
    }

    @Test
    @DisplayName("정책 저장 실패 - 충돌 발생")
    void savePolicy_conflict() {
        DiscountPolicy policy = DiscountPolicy.builder()
                .type(DiscountType.SEASONAL)
                .scope(DiscountScope.ACCOMMODATION)
                .accommodationId(UUID.randomUUID())
                .roomId(null)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(10))
                .build();

        when(repository.existsConflict(any(), any(), any(), any(), any(), any()))
                .thenReturn(true);

        assertThatThrownBy(() -> policyService.savePolicy(policy))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.DISCOUNT_POLICY_CONFLICT.getMessage());
    }

    @Test
    @DisplayName("적용 가능한 정책 조회 - 룸 & 숙소 ID 모두 만족")
    void getApplicablePolicies_success() {
        UUID accomId = UUID.randomUUID();
        UUID roomId = UUID.randomUUID();

        Rooms room = Rooms.builder()
                .id(roomId)
                .accommodationId(accomId)
                .build();

        LocalDateTime now = LocalDateTime.now();

        DiscountPolicy p1 = DiscountPolicy.builder()
                .type(DiscountType.WEEKDAY)
                .scope(DiscountScope.ROOM)
                .roomId(roomId)
                .active(true)
                .accommodationId(accomId)
                .startDate(now.minusDays(1))
                .endDate(now.plusDays(1))
                .build();

        when(repository.findActivePolicies(now))
                .thenReturn(List.of(p1));

        List<DiscountPolicy> result = policyService.getApplicablePolicies(room, now);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getType()).isEqualTo(DiscountType.WEEKDAY);
    }

    @Test
    @DisplayName("적용 가능한 정책 없음으로 빈 리스트")
    void getApplicablePolicies_empty() {
        Rooms room = Rooms.builder()
                .id(UUID.randomUUID())
                .accommodationId(UUID.randomUUID())
                .build();

        when(repository.findActivePolicies(any()))
                .thenReturn(List.of());

        List<DiscountPolicy> result = policyService.getApplicablePolicies(room, LocalDateTime.now());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("기간 할인 선택 - WEEKDAY vs SEASONAL 우선순위 확인")
    void selectPeriodPolicy_priority() {

        DiscountPolicy weekday = DiscountPolicy.builder()
                .type(DiscountType.WEEKDAY)
                .rate(0.10)
                .build();

        DiscountPolicy seasonal = DiscountPolicy.builder()
                .type(DiscountType.SEASONAL)
                .rate(0.50)
                .build();

        // WEEKDAY(priority 1) < SEASONAL(priority 2)
        List<DiscountPolicy> list = List.of(seasonal, weekday);

        DiscountPolicy selected = policyService.selectPeriodPolicy(list);

        assertThat(selected.getType()).isEqualTo(DiscountType.SEASONAL);
    }

    @Test
    @DisplayName("기간 할인 없음으로 null 반환")
    void selectPeriodPolicy_none() {
        DiscountPolicy membership = DiscountPolicy.builder()
                .type(DiscountType.MEMBERSHIP)
                .rate(0.05)
                .build();

        DiscountPolicy selected = policyService.selectPeriodPolicy(List.of(membership));

        assertThat(selected).isNull();
    }

    @Test
    @DisplayName("멤버십 할인 선택 성공")
    void selectMembershipPolicy_success() {
        DiscountPolicy membership = DiscountPolicy.builder()
                .type(DiscountType.MEMBERSHIP)
                .rate(0.10)
                .build();

        DiscountPolicy weekday = DiscountPolicy.builder()
                .type(DiscountType.WEEKDAY)
                .rate(0.10)
                .build();

        DiscountPolicy selected = policyService.selectMembershipPolicy(List.of(membership, weekday));

        assertThat(selected.getType()).isEqualTo(DiscountType.MEMBERSHIP);
    }

    @Test
    @DisplayName("멤버십 할인 없음으로 null 반환")
    void selectMembershipPolicy_none() {
        DiscountPolicy weekday = DiscountPolicy.builder()
                .type(DiscountType.WEEKDAY)
                .rate(0.10)
                .build();

        DiscountPolicy selected = policyService.selectMembershipPolicy(List.of(weekday));

        assertThat(selected).isNull();
    }
}
