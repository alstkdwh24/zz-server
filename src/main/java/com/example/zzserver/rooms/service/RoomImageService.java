package com.example.zzserver.rooms.service;

import com.example.zzserver.accommodation.dto.request.FileMetadata;
import com.example.zzserver.rooms.dto.response.RoomsImageResponse;
import com.example.zzserver.rooms.entity.RoomImages;
import com.example.zzserver.rooms.repository.RoomsImagesRepository;
import com.example.zzserver.config.FileHandler;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class RoomImageService {

    private final RoomsImagesRepository roomsImagesRepository;

    private final FileHandler fileHandler;

    /**
     * 방 번호를 대상으로 방이미지를 조회
     * @param roomId 방조회에 필요한 uuid
     * @return List<RoomsImageResponse>
     **/
    @Transactional(readOnly = true)
    public List<RoomsImageResponse> findAll(UUID roomId) {
         return roomsImagesRepository
                 .findByRoomId(roomId)
                 .stream()
                 .map(RoomsImageResponse::from)
                 .collect(Collectors.toList());
    }

    //이미지 단일 조회
    @Transactional(readOnly = true)
    public RoomsImageResponse findById(UUID id) {
        return roomsImagesRepository
                .findById(id)
                .map(RoomsImageResponse::from)
                .orElseThrow();
    }

    //방 이미지 업로드
    public List<UUID> uploadRoomsImages(UUID roomsImageId, List<MultipartFile> files) {
        List<FileMetadata> uploaded = fileHandler.uploadFiles(files, "roomsImage");

        List<RoomImages> images = uploaded.stream()
                .map(meta -> RoomImages.builder()
                        .roomId(roomsImageId)
                        .imageUrl(meta.getAccessUrl())
                        .displayed(false)
                        .build())
                .toList();

        return roomsImagesRepository
                .saveAll(images)
                .stream().map(RoomImages::getId)
                .toList();
    }

    //특정 방의  모든 이미지 삭제
    public void deleteImagesByRoomId(UUID roomId) {
        List<RoomImages> targets = roomsImagesRepository.findByRoomId(roomId);
        if(!targets.isEmpty()) {
            roomsImagesRepository.deleteAll(targets);
            fileHandler.deleteRoomFiles(targets);
        }
    }

    //방 이미지 다중 삭제
    public void deleteRoomImages(List<UUID> roomImageId) {
        List<RoomImages> targets  = roomsImagesRepository.findAllById(roomImageId);

        if(!targets.isEmpty()) {
            roomsImagesRepository.deleteAll(targets);
            fileHandler.deleteRoomFiles(targets);
        }
    }
}
