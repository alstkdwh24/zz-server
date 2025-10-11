package com.example.zzserver.accommodation.service;

import com.example.zzserver.accommodation.dto.request.FileMetadata;
import com.example.zzserver.accommodation.dto.response.AccommodationImageResponse;
import com.example.zzserver.accommodation.entity.AccommodationImages;
import com.example.zzserver.accommodation.repository.AccommodationImagesRepository;
import com.example.zzserver.config.exception.CustomException;
import com.example.zzserver.config.exception.ErrorCode;
import com.example.zzserver.config.handler.FileHandler;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class AccommodationImagesService {

    private final AccommodationImagesRepository accommodationImagesRepository;

    private final FileHandler fileHandler;

    /**
     * 다중 이미지 업로드 및 DB 저장
     * @param accommodationId - 이미지가 속한 숙박 UUID
     * @param files - 업로드할 Multipart 이미지 파일 리스트
     * @return 저장된 AccommodationImages의 UUID 목록
     **/
    public List<UUID> uploadImages(UUID accommodationId, List<MultipartFile> files) {
        // "accommodations" 폴더 하위에 저장
        List<FileMetadata> uploaded = fileHandler.uploadFiles(files, "accommodations");

        // DB 저장용 엔티티로 변환
        List<AccommodationImages> images = uploaded.stream()
                .map(meta -> AccommodationImages.builder()
                        .accommodationId(accommodationId)
                        .imageUrl(meta.getAccessUrl())  // 실제 접근 가능한 URL
                        .displayed(false)               // 기본은 미표시
                        .build())
                .toList();

        return accommodationImagesRepository
                .saveAll(images)
                .stream()
                .map(AccommodationImages::getId)
                .toList();
    }

    /**
     * 특정 숙소(accommodationId)에 연결된 이미지 목록 조회
     * @param accommodationId 숙소의 uuid
     * @return List<AccommodationImageResponse>
     **/
    @Transactional(readOnly = true)
    public List<AccommodationImageResponse> getImagesByAccommodation(UUID accommodationId) {
        return accommodationImagesRepository.findByAccommodationId(accommodationId).stream()
                .map(image -> AccommodationImageResponse.from(image))
                .toList();
    }

    /**
     * 단일 이미지 삭제
     * @param imageId 방이미지를 조회하는 uuid
     * @exception CustomException : ACCOMMODATION_IMAGE_NOT_FOUND
     **/
    public void deleteImage(UUID imageId) {
        AccommodationImages image = accommodationImagesRepository.findById(imageId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOMMODATION_IMAGE_NOT_FOUND));
        accommodationImagesRepository.delete(image);
        // TODO: fileHandler.delete(image.getImageUrl()) 호출 가능 (옵션)
    }

    /**
     * 다중 이미지 삭제
     * @param imageIds 이미지 삭제에 필요한 조회의 uuid
     **/
    public void deleteImages(List<UUID> imageIds) {
        List<AccommodationImages> targets = accommodationImagesRepository.findAllById(imageIds);
        if (!targets.isEmpty()) {
            accommodationImagesRepository.deleteAll(targets);
            fileHandler.deleteAccommodationFiles(targets);
        }
    }

    /**
     * 특정 숙소의 모든 이미지 삭제
     * @param accommodationId 숙소엔티티의 uuid
     **/
    public void deleteImagesByAccommodationId(UUID accommodationId) {
        List<AccommodationImages> targets = accommodationImagesRepository
                .findByAccommodationId(accommodationId);
        if (!targets.isEmpty()) {
            accommodationImagesRepository.deleteAll(targets);
            fileHandler.deleteAccommodationFiles(targets);
        }
    }
}
