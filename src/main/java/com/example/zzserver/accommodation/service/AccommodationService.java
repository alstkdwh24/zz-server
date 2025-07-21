package com.example.zzserver.accommodation.service;

import com.example.zzserver.accommodation.dto.request.AccommodationRequest;
import com.example.zzserver.accommodation.dto.response.AccommodationResponseDto;
import com.example.zzserver.accommodation.entity.Accommodations;
import com.example.zzserver.accommodation.repository.AccommodationRepository;
import java.util.List;
import java.util.UUID;

import com.example.zzserver.address.domain.Address;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@AllArgsConstructor
public class AccommodationService {

  private final AccommodationRepository accommodationRepository;

  private final AccommodationImagesService accommodationImagesService;


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

  public UUID createAccommodation(AccommodationRequest accommodationRequest, List<MultipartFile> imageFiles) {
    UUID requestAccommodationId = saveAccommodationOnly(accommodationRequest);

    //첨부파일이 있는지의 여부
    if(imageFiles != null && !imageFiles.isEmpty()) {
      accommodationImagesService.uploadImages(requestAccommodationId, imageFiles);
    }

    return requestAccommodationId;
  }

  public UUID updateAccommodations(UUID id,
                                   AccommodationRequest request,
                                   List<MultipartFile> newImages,
                                   List<UUID> deleteImageIds) {

    Accommodations accommodations = accommodationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("숙소가 존재하지 않습니다: " + id));

    Address newAddress = Address.of(request.getZipCode(), request.getAddress(), request.getDetailAddress());
    accommodations.update(request.getName(), request.getPhoneNumber(), newAddress,
            request.getLatitude(), request.getLongitude(), request.getType());

    // 이미지 삭제
    if (deleteImageIds != null && !deleteImageIds.isEmpty()) {
      accommodationImagesService.deleteImages(deleteImageIds);
    }

    // 새 이미지 추가
    if (newImages != null && !newImages.isEmpty()) {
      accommodationImagesService.uploadImages(id, newImages);
    }
    return id;
  }

  private UUID saveAccommodationOnly(AccommodationRequest request) {
    Address address = Address.of(
            request.getZipCode(),
            request.getAddress(),
            request.getDetailAddress()
    );

    Accommodations accommodations = Accommodations
            .builder()
            .name(request.getName())
            .bussinessUserId(request.getBussinessUserId())
            .type(request.getType())
            .displayed(true)
            .phoneNumber(request.getPhoneNumber())
            .address(address)
            .latitude(request.getLatitude())
            .longitude(request.getLongitude())
            .build();

    return accommodationRepository.save(accommodations).getId();
  }

  public void deleteById(UUID id) {
    if (!accommodationRepository.existsById(id)) {
      throw new RuntimeException("숙소가 존재하지 않습니다: " + id);
    }

    // 이미지 먼저 삭제
    accommodationImagesService.deleteImagesByAccommodationId(id);

    // 숙소 삭제
    accommodationRepository.deleteById(id);
  }

}
