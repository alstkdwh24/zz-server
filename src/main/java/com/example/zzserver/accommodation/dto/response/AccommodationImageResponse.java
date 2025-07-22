package com.example.zzserver.accommodation.dto.response;

import com.example.zzserver.accommodation.entity.AccommodationImages;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationImageResponse {

    private UUID id;
    private String imageUrl;
    private boolean displayed;

    public static AccommodationImageResponse from(AccommodationImages entity) {
        return AccommodationImageResponse
                .builder()
                .id(entity.getId())
                .imageUrl(entity.getImageUrl())
                .displayed(entity.isDisplayed())
                .build();
    }
}
