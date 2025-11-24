package com.example.zzserver.rooms.service;

import com.example.zzserver.rooms.dto.request.RoomsRequest;
import com.example.zzserver.rooms.dto.response.RoomsResponse;
import com.example.zzserver.rooms.entity.Rooms;
import com.example.zzserver.rooms.repository.RoomsRepository;
import com.example.zzserver.config.exception.CustomException;
import com.example.zzserver.config.exception.ErrorCode;
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

    /**
     * 방생성
     * @param request 방생성에 필요한 DTO
     * @param imageFiles 방생성에 필요한 이미지 MultipartFile List
     * @return uuid 생성시 나오는 uuid
     **/
    public UUID create(RoomsRequest.Request request, List<MultipartFile> imageFiles) {
        UUID roomImageId = saveRooms(request);

        if(imageFiles != null && !imageFiles.isEmpty()) {
            roomImageService.uploadRoomsImages(roomImageId, imageFiles);
        }
        return roomImageId;
    }

    /**
     * 방 조회
     * @param roomsId 방조회에서 필요한 uuid
     * @return RoomResponse
     **/
    public RoomsResponse findById(UUID roomsId) {
        return roomsRepository.findById(roomsId)
                .map(rooms -> RoomsResponse.from(rooms))
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
    }

    /**
     * 숙소번호로 방 조회
     * @param accommodationId 방목록을 조회하기 위한 숙소 uuid
     * @return List<RoomResponse>
     **/
    public List<RoomsResponse> getAllByAccommodation(UUID accommodationId) {
        return roomsRepository.findByAccommodationId(accommodationId).stream()
                .map(RoomsResponse::from)
                .toList();
    }

    /**
     * 방 수정
     * @param id 조회에 필요한 uuid
     * @param request 수정에 필요한 DTO
     * @param newImages 수정에 필요한 MultipartFile
     * @param deleteImageIds 수정에 필요한 이미지 uuid
     * @return uuid 수정된 방의 uuid
     **/
    public UUID update(
                        UUID id,
                        RoomsRequest.Update request,
                        List<MultipartFile> newImages,
                        List<UUID> deleteImageIds) {

        //방조회
        Rooms room = roomsRepository.findById(id).orElseThrow(()->new CustomException(ErrorCode.ROOM_NOT_FOUND));
        //방수정
        room.update(request.getName(), request.getMaxOccupacy(), request.isAvailable(), request.getPeopleCount());
        //이미지 삭제
        if(deleteImageIds != null && !deleteImageIds.isEmpty()) {
            roomImageService.deleteRoomImages(deleteImageIds);
        }
        //이미지 추가
        if( newImages != null && !newImages.isEmpty()) {
            roomImageService.uploadRoomsImages(id,newImages);
        }
        return id;
    }

    /**
     * 방 삭제
     * @param id 방 조회에 필요한 uuid
     * @exception CustomException : ROOM_NOT_FOUND
     **/
    public void delete(UUID id) {
        if(!roomsRepository.existsById(id)) {
            throw new CustomException(ErrorCode.ROOM_NOT_FOUND);
        }
        // 이미지 삭제
        roomImageService.deleteImagesByRoomId(id);
        // 방 삭제
        roomsRepository.deleteById(id);
    }


    /**
     * 방 생성
     **/
    private UUID saveRooms(RoomsRequest.Request request) {
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
