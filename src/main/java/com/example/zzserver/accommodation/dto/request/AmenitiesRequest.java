package com.example.zzserver.accommodation.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmenitiesRequest {

    private String name;
    private String iconUrl;

}
