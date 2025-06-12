package com.example.zzserver.accommodation.dto.response;

import com.example.zzserver.accommodation.entity.AccommodationType;
import com.example.zzserver.accommodation.entity.Accommodations;
import com.example.zzserver.address.domain.Address;
import java.util.UUID;

public class AccommodationResponseDto {

  private UUID id;

  private String name;

  private Address address;

  private Double latitude;

  private Double longitude;

  private AccommodationType type;

  private AccommodationResponseDto(UUID id, String name, Address address, Double latitude, Double longitude, AccommodationType type) {
    this.id = id;
    this.name = name;
    this.address = address;
    this.latitude = latitude;
    this.longitude = longitude;
    this.type = type;
  }

  public static AccommodationResponseDto from(Accommodations accommodation) {
    return new AccommodationResponseDto(
        accommodation.getId(),
        accommodation.getName(),
        accommodation.getAddress(),
        accommodation.getLatitude(),
        accommodation.getLongitude(),
        accommodation.getType());
  }
}
