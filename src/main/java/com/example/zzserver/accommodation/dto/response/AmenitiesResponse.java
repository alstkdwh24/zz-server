package com.example.zzserver.accommodation.dto.response;

import com.example.zzserver.accommodation.entity.Amenities;
import junit.runner.Version;
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
