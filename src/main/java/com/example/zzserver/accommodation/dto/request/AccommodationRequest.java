package com.example.zzserver.accommodation.dto.request;

import com.example.zzserver.accommodation.consts.AccommodationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationRequest {
    private UUID bussinessUserId;
    private String name;
    private String zipCode;
    private String address;
    private String detailAddress;
    private Double latitude;
    private Double longitude;
    private String phoneNumber;
    private AccommodationType type;
}
