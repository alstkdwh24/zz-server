package com.example.zzserver.accommodation.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
public class AccommodationSearchResponse {
    private UUID id;
    private String name;
    private String city;
    private BigDecimal minPrice;  // 해당 숙소의 가장 싼 방 가격
    private BigDecimal maxPrice;  // 가장 비싼 방 가격
    private boolean available;    // 예약 가능 여부
    private String thumbnailImage;   // 숙소 이미지
    private String sort; // 정렬 기준
}
