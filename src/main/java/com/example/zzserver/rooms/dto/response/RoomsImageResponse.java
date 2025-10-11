package com.example.zzserver.rooms.dto.response;

import com.example.zzserver.rooms.entity.RoomImages;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomsImageResponse {

    private UUID id;
    private String imageUrl;
    private boolean displayed;
    private UUID roomId;

    public static RoomsImageResponse from(RoomImages images) {
        return RoomsImageResponse
                .builder()
                .id(images.getId())
                .imageUrl(images.getImageUrl())
                .roomId(images.getRoomId())
                .imageUrl(images.getImageUrl())
                .build();
    }
}
