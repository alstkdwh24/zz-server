package com.example.zzserver.accommodation.dto.response;

import com.example.zzserver.accommodation.consts.AccommodationType;
import com.example.zzserver.accommodation.entity.Accommodations;
import com.example.zzserver.address.domain.Address;
import lombok.*;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationResponseDto {

  private UUID id;

  private String name;

  private Address address;

  private Double latitude;

  private Double longitude;

  private AccommodationType type;

  private Boolean displayed;

  public static AccommodationResponseDto from(Accommodations accommodation) {
    return new AccommodationResponseDto(
        accommodation.getId(),
        accommodation.getName(),
        accommodation.getAddress(),
        accommodation.getLatitude(),
        accommodation.getLongitude(),
        accommodation.getType(),
        accommodation.isDisplayed());
  }
}
