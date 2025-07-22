package com.example.zzserver.accommodation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomAmenityRequest {
    private UUID roomId;
    private UUID amenityId;
}
