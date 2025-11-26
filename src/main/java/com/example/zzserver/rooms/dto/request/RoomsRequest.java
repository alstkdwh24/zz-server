package com.example.zzserver.rooms.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;
import java.util.UUID;

public class RoomsRequest {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        @NotBlank
        private UUID accommodationId;
        @NotBlank(message = "방 이름을 입력해 주세요.")
        private String name;
        //
        @Min(1)
        @NotBlank(message = "허용수를 입력해 주세요.")
        private long maxOccupacy;
        private boolean available;
        private Integer stockCount;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Update {
        private UUID accommodationId;
        private String name;
        private long maxOccupacy;
        private boolean available;
        private Integer stockCount;
        private List<UUID> deleteImageIds;
    }
}
