package com.example.zzserver.accommodation.service;

import com.example.zzserver.accommodation.dto.request.RoomsRequest;
import com.example.zzserver.accommodation.dto.response.RoomsResponse;
import com.example.zzserver.accommodation.entity.Rooms;
import com.example.zzserver.accommodation.repository.RoomsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RoomsService {

    private final RoomsRepository roomsRepository;

    private final RoomImageService roomImageService;

    // 방생성
    public UUID create(RoomsRequest request, List<MultipartFile> imageFiles) {
        UUID roomImageId = saveRooms(request);

        if(imageFiles != null && !imageFiles.isEmpty()) {
            roomImageService.uploadRoomsImages(roomImageId, imageFiles);
        }
        return roomImageId;
    }

    // 방 조회
    public RoomsResponse findById(UUID roomsId) {
        return roomsRepository.findById(roomsId)
                .map(rooms -> RoomsResponse.from(rooms))
                .orElseThrow(() -> new RuntimeException("방을 찾을 수 없습니다."));
    }

    // 숙소번호로 방 조회
    public List<RoomsResponse> getAllByAccommodation(UUID accommodationId) {
        return roomsRepository.findByAccommodationId(accommodationId).stream()
                .map(RoomsResponse::from)
                .toList();
    }

    // 방 수정
    public UUID update(
                        UUID id,
                        RoomsRequest request,
                        List<MultipartFile> newImages,
                        List<UUID> deleteImageIds) {
        //방조회
        Rooms room = roomsRepository.findById(id).orElseThrow(()->new RuntimeException("방이 없습니다."));
        //방수정
        room.update(request.getName(), request.getMaxOccupacy(), request.isAvailable(), request.getPeopleCount());
        //이미지 삭제
        if(deleteImageIds.isEmpty() && deleteImageIds != null) {
            roomImageService.deleteRoomImages(deleteImageIds);
        }
        //이미지 추가
        if( newImages != null && !newImages.isEmpty()) {
            roomImageService.uploadRoomsImages(id,newImages);
        }
        return id;
    }

    // 방 삭제
    public void delete(UUID id) {
        if(!roomsRepository.existsById(id)) {
            throw new RuntimeException("방이 존재하지 않습니다: " + id);
        }
        // 이미지 삭제
        roomImageService.deleteImagesByRoomId(id);
        // 방 삭제
        roomsRepository.deleteById(id);
    }


    private UUID saveRooms(RoomsRequest request) {
        Rooms rooms = Rooms
                .builder()
                .accommodationId(request.getAccommodationId())
                .name(request.getName())
                .maxOccupacy(request.getMaxOccupacy())
                .peopleCount(request.getPeopleCount())
                .available(request.isAvailable())
                .build();

        return roomsRepository.save(rooms).getId();
    }
}
