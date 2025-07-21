package com.example.zzserver.accommodation.service;

import com.example.zzserver.accommodation.dto.request.AmenitiesRequest;
import com.example.zzserver.accommodation.dto.response.AmenitiesResponse;
import com.example.zzserver.accommodation.entity.Amenities;
import com.example.zzserver.accommodation.repository.AmenitiesRepository;
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
     */
    public UUID create(AmenitiesRequest request) {
        Amenities amenities = new Amenities();
        amenities.setName(request.getName());
        amenities.setIconUrl(request.getIconUrl());
        return amenitiesRepository.save(amenities).getId();
    }

    /**
     * 전체 편의시설 조회
     */
    @Transactional(readOnly = true)
    public List<AmenitiesResponse> findAll() {
        return amenitiesRepository.findAll()
                .stream()
                .map(amenities -> new AmenitiesResponse(amenities.getId(),amenities.getName(),amenities.getIconUrl()))
                .collect(Collectors.toList());
    }

    /**
     * 편의시설 삭제
     */
    public void deleteById(UUID id) {
        if (!amenitiesRepository.existsById(id)) {
            throw new RuntimeException("편의시설이 존재하지 않습니다: " + id);
        }
        amenitiesRepository.deleteById(id);
    }
}
