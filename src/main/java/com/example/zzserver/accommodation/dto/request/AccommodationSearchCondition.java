package com.example.zzserver.accommodation.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AccommodationSearchCondition {
    private String keyword;          // 숙소명 검색
    private String city;             // 지역
    private Integer minPrice;        // 최소 가격
    private Integer maxPrice;        // 최대 가격
    private Integer peopleCount;     // 인원 수
    private LocalDateTime checkIn;       // 체크인
    private LocalDateTime checkOut;      // 체크아웃
    private String sort;             // 정렬 (price, rating 등)
}
