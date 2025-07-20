package com.example.zzserver.accommodation.dto.response;

import com.example.zzserver.accommodation.entity.Amenities;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AmenitiesResponse {
    private UUID id;
    private String name;
    private String iconUrl;
}
