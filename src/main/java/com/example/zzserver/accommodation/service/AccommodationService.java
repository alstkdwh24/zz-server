package com.example.zzserver.accommodation.service;

import com.example.zzserver.accommodation.dto.request.AccommodationRequest;
import com.example.zzserver.accommodation.dto.response.AccommodationResponseDto;
import com.example.zzserver.accommodation.entity.Accommodations;
import com.example.zzserver.accommodation.repository.AccommodationRepository;
import java.util.List;
import java.util.UUID;

import com.example.zzserver.address.domain.Address;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AccommodationService {

  private final AccommodationRepository accommodationRepository;

  public AccommodationService(AccommodationRepository accommodationRepository) {
    this.accommodationRepository = accommodationRepository;
  }

  @Transactional(readOnly = true)
  public List<AccommodationResponseDto> readDisplayedList() {
    List<Accommodations> all = accommodationRepository.findByDisplayedTrue();

    return all.stream()
        .map(AccommodationResponseDto::from)
        .toList();
  }

  @Transactional(readOnly = true)
  public AccommodationResponseDto findById(UUID id) {
    return accommodationRepository.findById(id)
            .map(AccommodationResponseDto::from)
            .orElseThrow(()-> new RuntimeException(id+"를 찾을수 없습니다."));
  }

  public UUID createAccommodation(AccommodationRequest accommodationRequest) {

    Address address = Address.of(
            accommodationRequest.getZipCode(),
            accommodationRequest.getAddress(),
            accommodationRequest.getDetailAddress()
    );

    Accommodations accommodations = Accommodations
            .builder()
            .name(accommodationRequest.getName())
            .bussinessUserId(accommodationRequest.getBussinessUserId())
            .type(accommodationRequest.getType())
            .phoneNumber(accommodationRequest.getPhoneNumber())
            .address(address)
            .latitude(accommodationRequest.getLatitude())
            .longitude(accommodationRequest.getLongitude())
            .displayed(false)
            .build();

    return accommodationRepository.save(accommodations).getId();
  }

  public UUID updateAccommodations(UUID id, AccommodationRequest accommodationRequest) {
    Accommodations accommodations = accommodationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("숙소가 존재하지 않습니다: " + id));

    Address newAddress = Address.of(
            accommodationRequest.getZipCode(),
            accommodationRequest.getAddress(),
            accommodationRequest.getDetailAddress()
    );

    accommodations.update(
            accommodationRequest.getName(),
            accommodationRequest.getPhoneNumber(),
            newAddress,
            accommodationRequest.getLatitude(),
            accommodationRequest.getLongitude(),
            accommodationRequest.getType()
    );
    return id;
  }

  public void deleteById(UUID id) {
    if(!accommodationRepository.existsById(id)) {
      new RuntimeException("숙소가 존재하지 않습니다: " + id);
    }
    accommodationRepository.deleteById(id);
  }

}
