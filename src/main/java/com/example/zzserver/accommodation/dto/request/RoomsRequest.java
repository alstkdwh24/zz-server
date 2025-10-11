package com.example.zzserver.accommodation.dto.request;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomsRequest {
    private UUID accommodationId;
    private String name;
    private long maxOccupacy;
    private boolean available;
    private Integer peopleCount;

    public class Update {
    }
}
