package com.example.zzserver.accommodation.service;

import com.example.zzserver.accommodation.dto.request.AccommodationRequest;
import com.example.zzserver.accommodation.dto.request.AccommodationSearchCondition;
import com.example.zzserver.accommodation.dto.response.AccommodationResponseDto;
import com.example.zzserver.accommodation.dto.response.AccommodationSearchResponse;
import com.example.zzserver.accommodation.entity.Accommodations;
import com.example.zzserver.accommodation.repository.AccommodationRepository;
import java.util.List;
import java.util.UUID;

import com.example.zzserver.address.domain.Address;
import com.example.zzserver.config.exception.CustomException;
import com.example.zzserver.config.exception.ErrorCode;
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

  /**
   * 숙소 목록(displayed가 true인 경우)
   * @return List<AccommodationResponse>
   **/
  @Transactional(readOnly = true)
  public List<AccommodationResponseDto> readDisplayedList() {
    List<Accommodations> all = accommodationRepository.findByDisplayedTrue();

    return all.stream()
        .map(AccommodationResponseDto::from)
        .toList();
  }

  /**
   * 숙소 조회
   * @param id 특정 숙소의 uuid
   * @exception CustomException : ACCOMMODATION_NOT_FOUND
   * @return AccommodationResponse
   **/
  @Transactional(readOnly = true)
  public AccommodationResponseDto findById(UUID id) {
    return accommodationRepository.findById(id)
            .map(AccommodationResponseDto::from)
            .orElseThrow(()-> new CustomException(ErrorCode.ACCOMMODATION_NOT_FOUND));
  }

  @Transactional(readOnly = true)
  public List<AccommodationSearchResponse> findBySearch(AccommodationSearchCondition condition) {
    return accommodationRepository.search(condition);
  }

  /**
   * 숙소 생성
   * @param accommodationRequest 숙소 생성에 필요한 요청 DTO
   * @param imageFiles 숙소 생성에 필요한 이미지 MultipartFile 리스트
   * @return uuid 생성시 나오는 uuid(saveAccommodationOnly)
   **/
  public UUID createAccommodation(AccommodationRequest accommodationRequest, List<MultipartFile> imageFiles) {
    UUID requestAccommodationId = saveAccommodationOnly(accommodationRequest);

    //첨부파일이 있는지의 여부
    if(imageFiles != null && !imageFiles.isEmpty()) {
      accommodationImagesService.uploadImages(requestAccommodationId, imageFiles);
    }

    return requestAccommodationId;
  }

  /**
   * 숙소 수정
   * @param id 숙소 조회에 필요한 uuid,
   * @param request 숙소 수정에 필요한 DTO,
   * @param newImages 숙소 수정에 필요한 MultipartFile
   * @param deleteImageIds 숙소 수정에 필요한 이미지 uuid
   * @exception CustomException : ACCOMMODATION_NOT_FOUND
   * @return uuid : 수정후 해당 숙소의 uuid
   **/
  public UUID updateAccommodations(UUID id,
                                   AccommodationRequest request,
                                   List<MultipartFile> newImages,
                                   List<UUID> deleteImageIds) {

    Accommodations accommodations = accommodationRepository.findById(id)
            .orElseThrow(() -> new CustomException(ErrorCode.ACCOMMODATION_NOT_FOUND));

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

  /**
   * 숙소 저장 메서드
   * @param request 숙소 저장에 필요한 요청 DTO
   * @return uuid 숙소 생성시 나오는 uuid
   **/
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

  /**
   * 숙소 삭제
   * @param id 숙소의 uuid
   * @exception CustomException : ACCOMMODATION_NOT_FOUND
   **/
  public void deleteById(UUID id) {
    if (!accommodationRepository.existsById(id)) {
      throw new CustomException(ErrorCode.ACCOMMODATION_NOT_FOUND);
    }

    // 이미지 먼저 삭제
    accommodationImagesService.deleteImagesByAccommodationId(id);

    // 숙소 삭제
    accommodationRepository.deleteById(id);
  }

}
