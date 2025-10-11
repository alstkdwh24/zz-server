package com.example.zzserver.amenities.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AmenitiesResponse {
    private UUID id;
    private String name;
    private String iconUrl;


}
