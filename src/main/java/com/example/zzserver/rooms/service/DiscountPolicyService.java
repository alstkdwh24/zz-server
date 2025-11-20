package com.example.zzserver.rooms.service;

import com.example.zzserver.config.exception.CustomException;
import com.example.zzserver.config.exception.ErrorCode;
import com.example.zzserver.rooms.consts.DiscountType;
import com.example.zzserver.rooms.entity.DiscountPolicy;
import com.example.zzserver.rooms.entity.Rooms;
import com.example.zzserver.rooms.repository.DisCountPolicyRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
public class DiscountPolicyService {

    private final DisCountPolicyRepository disCountPolicyRepository;

    /**
     * 정책 저장
     * @param policy 정책 엔티티
     * @return 할인 정책 객체
     **/
    public DiscountPolicy savePolicy(DiscountPolicy policy) {
        validateNewPolicy(policy);
        return disCountPolicyRepository.save(policy);
    }

    /**
     * 정책 등록시 검증
     **/
    public void validateNewPolicy(DiscountPolicy policy) {
        boolean conflict = disCountPolicyRepository.existsConflict(
                policy.getType(),
                policy.getScope(),
                policy.getAccommodationId(),
                policy.getRoomId(),
                policy.getStartDate(),
                policy.getEndDate()
        );

        if (conflict) {
            throw new CustomException(ErrorCode.DISCOUNT_POLICY_CONFLICT);
        }
    }

    public List<DiscountPolicy> getApplicablePolicies(Rooms room, LocalDateTime now) {
        return disCountPolicyRepository.findActivePolicies(now).stream()
                .filter(p -> p.isValidForRoom(room.getId(), room.getAccommodationId(), now))
                .toList();
    }

    public DiscountPolicy selectPeriodPolicy(List<DiscountPolicy> list) {
        return list.stream()
                .filter(p -> p.getType() == DiscountType.WEEKDAY || p.getType() == DiscountType.SEASONAL)
                .sorted(Comparator.comparingInt(p -> p.getType().getPriority()))
                .findFirst()
                .orElse(null);
    }

    public DiscountPolicy selectMembershipPolicy(List<DiscountPolicy> list) {
        return list.stream()
                .filter(p -> p.getType() == DiscountType.MEMBERSHIP)
                .findFirst()
                .orElse(null);
    }
}
