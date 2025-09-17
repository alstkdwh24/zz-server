package com.example.zzserver.reservation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

public class ReservationDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private UUID id;
        @NonNull
        private UUID memberId;
        @NonNull
        private UUID roomId;
        @NonNull
        private UUID cartId;
        @NonNull
        private LocalDateTime checkInDate;
        @NonNull
        private LocalDateTime checkOutDate;
        @Min(1)
        private int roomCount;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response{
        private UUID id;
        private LocalDateTime checkInDate;
        private LocalDateTime checkOutDate;
        private LocalDateTime reservedAt;
        private UUID memberId;
        private UUID roomId;
        private UUID cartId;
    }
}
