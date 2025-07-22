package com.example.zzserver.accommodation.dto.response;

import com.example.zzserver.accommodation.entity.Rooms;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomsResponse {

    private UUID id;
    private String name;
    private long maxOccupacy;
    private boolean available;
    private Integer peopleCount;

    public static RoomsResponse from(Rooms room) {
        return new RoomsResponse(
                room.getId(),
                room.getName(),
                room.getMaxOccupacy(),
                room.isAvailable(),
                room.getPeopleCount()
        );
    }
}
