package com.example.zzserver.accommodation.dto.response;

import com.example.zzserver.accommodation.entity.RoomAmenities;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomAmenityResponse {
    private UUID id;
    private UUID roomId;
    private UUID amenityId;

    public static RoomAmenityResponse from(RoomAmenities roomAmenities) {
        return RoomAmenityResponse
                .builder()
                .roomId(roomAmenities.getRoomId())
                .amenityId(roomAmenities.getAmenityId())
                .build();
    }
}
