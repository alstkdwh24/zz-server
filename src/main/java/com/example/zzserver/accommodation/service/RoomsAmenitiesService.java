package com.example.zzserver.accommodation.service;

import com.example.zzserver.accommodation.dto.request.RoomAmenityRequest;
import com.example.zzserver.accommodation.dto.response.RoomAmenityResponse;
import com.example.zzserver.accommodation.entity.RoomAmenities;
import com.example.zzserver.accommodation.repository.RoomAmenitiesRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RoomsAmenitiesService {

    private final RoomAmenitiesRepository roomAmenitiesRepository;

    // 1. 등록
    public UUID create(UUID roomId, UUID amenityId) {
        RoomAmenities roomAmenity = new RoomAmenities();
        roomAmenity.setRoomId(roomId);
        roomAmenity.setAmenityId(amenityId);
        return roomAmenitiesRepository.save(roomAmenity).getRoomId();
    }

    // 2. 특정 방에 대한 편의시설 전체 조회
    public List<RoomAmenityResponse> findByRoomId(UUID roomId) {
        return roomAmenitiesRepository.findByRoomId(roomId)
                .stream()
                .map(RoomAmenityResponse::from)
                .collect(Collectors.toList());
    }

    // 3. 삭제
    public void delete(RoomAmenityRequest request) {
        UUID roomId = request.getRoomId();
        UUID amenityId = request.getAmenityId();

        RoomAmenities target = roomAmenitiesRepository
                .findByRoomIdAndAmenityId(roomId, amenityId);
        roomAmenitiesRepository.delete(target);
    }
}
