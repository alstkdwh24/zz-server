package com.example.zzserver.rooms.entity;

import com.example.zzserver.rooms.consts.DiscountScope;
import com.example.zzserver.rooms.consts.DiscountType;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Table
@Entity
@Getter
public class DiscountPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private DiscountType type; // WEEKDAY, SEASONAL, MEMBERSHIP 등

    private double rate; // 할인율 (예: 0.15 = 15%)
    private boolean active;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    // 할인 적용 범위 (전체, 특정 숙소, 특정 방)
    @Enumerated(EnumType.STRING)
    private DiscountScope scope;

    // 특정 숙소 대상 (null이면 전역)
    private UUID accommodationId;

    // 특정 방 대상 (null이면 전역)
    private UUID roomId;

    public boolean isValidForRoom(UUID roomId, UUID accommodationId, LocalDateTime now) {
        return this.active &&
                (this.accommodationId == null || this.accommodationId.equals(accommodationId)) &&
                (this.roomId == null || this.roomId.equals(roomId)) &&
                (now.isAfter(startDate) && now.isBefore(endDate));
    }
}
