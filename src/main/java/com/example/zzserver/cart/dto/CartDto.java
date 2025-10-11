package com.example.zzserver.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

public class CartDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private LocalDateTime checkInDate;
        private LocalDateTime checkOutDate;
        private Long roomCount;
        private UUID memberId;
        private UUID roomId;
        private LocalDateTime createdTime;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private UUID id;
        private LocalDateTime checkInDate;
        private LocalDateTime checkOutDate;
        private Long roomCount;
        private UUID memberId;
        private UUID roomId;
        private LocalDateTime createdTime;
    }
}
