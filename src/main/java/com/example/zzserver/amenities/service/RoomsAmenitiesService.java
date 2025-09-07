package com.example.zzserver.amenities.service;

import com.example.zzserver.amenities.dto.request.RoomAmenityRequest;
import com.example.zzserver.amenities.dto.response.RoomAmenityResponse;
import com.example.zzserver.amenities.entity.RoomAmenities;
import com.example.zzserver.amenities.repsoitory.RoomAmenitiesRepository;
import com.example.zzserver.config.exception.CustomException;
import com.example.zzserver.config.exception.ErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RoomsAmenitiesService {

    private final RoomAmenitiesRepository roomAmenitiesRepository;

    /**
     * 방 편의시설 등록
     * @param roomId 방엔티티의 uuid
     * @param amenityId 편의시설엔티티의 uuid
     * @return uuid : 방 편의시설의 uuid 생성값
     **/
    public UUID create(UUID roomId, UUID amenityId) {
        RoomAmenities roomAmenity = RoomAmenities
                .builder()
                .roomId(roomId)
                .amenityId(amenityId)
                .build();
        return roomAmenitiesRepository.save(roomAmenity).getRoomId();
    }

    /**
     * 특정 방에 대한 편의시설 전체 조회
     * @param roomId 방을 조회하기 위한 방엔티티의 uuid
     * @return List<RoomAmenityResponse>
     **/
    public List<RoomAmenityResponse> findByRoomId(UUID roomId) {
        return roomAmenitiesRepository.findByRoomId(roomId)
                .stream()
                .map(RoomAmenityResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 특정 방에 대한 편의시설 삭제
     * @param request 삭제에 필요한 DTO
     * @exception CustomException : AMENITIES_NOT_FOUND
     **/
    public void delete(RoomAmenityRequest request) {
        UUID roomId = request.getRoomId();
        UUID amenityId = request.getAmenityId();

        Optional<RoomAmenities> target = Optional.ofNullable(roomAmenitiesRepository
                .findByRoomIdAndAmenityId(roomId, amenityId)
                .orElseThrow(() -> new CustomException(ErrorCode.AMENITIES_NOT_FOUND)));
        roomAmenitiesRepository.delete(target.get());
    }
}
