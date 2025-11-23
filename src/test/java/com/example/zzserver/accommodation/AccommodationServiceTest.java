package com.example.zzserver.accommodation;

import com.example.zzserver.accommodation.consts.AccommodationType;
import com.example.zzserver.accommodation.dto.request.AccommodationRequest;
import com.example.zzserver.accommodation.dto.response.AccommodationResponseDto;
import com.example.zzserver.accommodation.entity.Accommodations;
import com.example.zzserver.accommodation.repository.AccommodationRepository;
import com.example.zzserver.accommodation.service.AccommodationImagesService;
import com.example.zzserver.accommodation.service.AccommodationService;
import com.example.zzserver.address.domain.Address;
import com.example.zzserver.config.exception.CustomException;
import com.example.zzserver.config.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccommodationServiceTest {

    @Mock
    private AccommodationRepository accommodationRepository;

    @Mock
    private AccommodationImagesService accommodationImagesService;

    @InjectMocks
    private AccommodationService accommodationService;


    @Test
    @DisplayName("숙소 조회 성공")
    void findById_success() {
        UUID id = UUID.randomUUID();

        Accommodations acc = Accommodations.builder()
                .id(id)
                .name("테스트 숙소")
                .address(Address.of("11111", "서울시", "101호"))
                .displayed(true)
                .build();

        when(accommodationRepository.findById(id)).thenReturn(Optional.of(acc));

        AccommodationResponseDto result = accommodationService.findById(id);

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo(acc.getName());
    }

    @Test
    @DisplayName("숙소 조회 실패 - NOT FOUND")
    void findById_fail_notFound() {
        UUID id = UUID.randomUUID();
        when(accommodationRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accommodationService.findById(id))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.ACCOMMODATION_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("숙소 생성 성공 (이미지 포함)")
    void createAccommodation_success() {
        UUID id = UUID.randomUUID();

        AccommodationRequest req = AccommodationRequest.builder()
                .name("테스트 숙소")
                .zipCode("11111")
                .address("서울")
                .detailAddress("101호")
                .latitude(1.1)
                .longitude(2.2)
                .type(AccommodationType.MOTEL)
                .bussinessUserId(id)
                .phoneNumber("01012341234")
                .build();

        List<MultipartFile> files = List.of(mock(MultipartFile.class));

        Accommodations acc = Accommodations.builder()
                .id(id)
                .name(req.getName())
                .address(Address.of(req.getZipCode(), req.getAddress(), req.getDetailAddress()))
                .displayed(true)
                .build();

        when(accommodationRepository.save(any())).thenReturn(acc);

        UUID result = accommodationService.createAccommodation(req, files);

        assertThat(result).isEqualTo(id);
        verify(accommodationImagesService, times(1)).uploadImages(eq(id), eq(files));
    }

    @Test
    @DisplayName("숙소 생성 성공 (이미지 없음)")
    void createAccommodation_noImages() {
        UUID id = UUID.randomUUID();

        AccommodationRequest req = AccommodationRequest.builder()
                .name("테스트 숙소")
                .zipCode("11111")
                .address("서울")
                .detailAddress("101호")
                .latitude(1.1)
                .longitude(2.2)
                .type(AccommodationType.HOTEL)
                .bussinessUserId(id)
                .phoneNumber("01012341234")
                .build();

        Accommodations acc = Accommodations.builder()
                .id(id)
                .name(req.getName())
                .address(Address.of(req.getZipCode(), req.getAddress(), req.getDetailAddress()))
                .displayed(true)
                .build();

        when(accommodationRepository.save(any())).thenReturn(acc);

        UUID result = accommodationService.createAccommodation(req, null);

        assertThat(result).isEqualTo(id);
        verify(accommodationImagesService, never()).uploadImages(any(), any());
    }

    @Test
    @DisplayName("숙소 수정 성공")
    void updateAccommodations_success() {
        UUID id = UUID.randomUUID();

        AccommodationRequest req = AccommodationRequest.builder()
                .name("수정된 숙소")
                .zipCode("22222")
                .address("부산")
                .detailAddress("202호")
                .latitude(3.3)
                .longitude(4.4)
                .type(AccommodationType.MOTEL)
                .build();

        Accommodations acc = mock(Accommodations.class);
        when(accommodationRepository.findById(id)).thenReturn(Optional.of(acc));

        List<MultipartFile> newImages = List.of(mock(MultipartFile.class));
        List<UUID> deleteIds = List.of(UUID.randomUUID());

        UUID result = accommodationService.updateAccommodations(id, req, newImages, deleteIds);

        assertThat(result).isEqualTo(id);
        verify(acc, times(1))
                .update(eq(req.getName()), eq(req.getPhoneNumber()),
                        any(Address.class), eq(req.getLatitude()), eq(req.getLongitude()), eq(req.getType()));

        verify(accommodationImagesService).deleteImages(deleteIds);
        verify(accommodationImagesService).uploadImages(id, newImages);
    }

    @Test
    @DisplayName("숙소 수정 실패 - NOT FOUND")
    void updateAccommodations_fail_notFound() {
        UUID id = UUID.randomUUID();
        AccommodationRequest req = AccommodationRequest.builder().name("ABC").build();

        when(accommodationRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accommodationService.updateAccommodations(id, req, null, null))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.ACCOMMODATION_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("숙소 삭제 성공")
    void delete_success() {
        UUID id = UUID.randomUUID();
        when(accommodationRepository.existsById(id)).thenReturn(true);

        accommodationService.deleteById(id);

        verify(accommodationImagesService, times(1)).deleteImagesByAccommodationId(id);
        verify(accommodationRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("숙소 삭제 실패 - 존재하지 않음")
    void delete_fail_notFound() {
        UUID id = UUID.randomUUID();
        when(accommodationRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> accommodationService.deleteById(id))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.ACCOMMODATION_NOT_FOUND.getMessage());
    }

}
