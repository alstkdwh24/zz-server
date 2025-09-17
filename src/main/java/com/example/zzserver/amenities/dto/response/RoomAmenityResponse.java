package com.example.zzserver.amenities.dto.response;

import com.example.zzserver.amenities.entity.RoomAmenities;
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
