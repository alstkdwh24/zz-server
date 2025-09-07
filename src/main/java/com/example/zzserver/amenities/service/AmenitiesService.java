package com.example.zzserver.amenities.service;

import com.example.zzserver.amenities.dto.request.AmenitiesRequest;
import com.example.zzserver.amenities.dto.response.AmenitiesResponse;
import com.example.zzserver.amenities.entity.Amenities;
import com.example.zzserver.amenities.repsoitory.AmenitiesRepository;
import com.example.zzserver.config.exception.CustomException;
import com.example.zzserver.config.exception.ErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class AmenitiesService {

    private final AmenitiesRepository amenitiesRepository;

    /**
     * 편의시설 등록
     * @param request 편의시설 관련 Request Dto
     * @return uuid 편의시설의 uuid
     **/
    public UUID create(AmenitiesRequest request) {
        Amenities amenities = Amenities
                .builder()
                .name(request.getName())
                .iconUrl(request.getIconUrl())
                .build();
        return amenitiesRepository.save(amenities).getId();
    }

    /**
     * 전체 편의시설 조회
     * @return List<AmenitiesResponse>
     **/
    @Transactional(readOnly = true)
    public List<AmenitiesResponse> findAll() {
        return amenitiesRepository.findAll()
                .stream()
                .map(amenities -> new AmenitiesResponse(amenities.getId(),amenities.getName(),amenities.getIconUrl()))
                .collect(Collectors.toList());
    }

    /**
     * 편의시설 삭제
     * @param id 편의시설 조회에 필요한 uuid
     * @exception CustomException : AMENITIES_NOT_FOUND
     **/
    public void deleteById(UUID id) {
        if (!amenitiesRepository.existsById(id)) {
            throw new CustomException(ErrorCode.AMENITIES_NOT_FOUND);
        }
        amenitiesRepository.deleteById(id);
    }
}
