package com.example.zzserver.rooms;


import com.example.zzserver.config.exception.CustomException;
import com.example.zzserver.config.exception.ErrorCode;
import com.example.zzserver.rooms.dto.request.RoomsRequest;
import com.example.zzserver.rooms.dto.response.RoomsResponse;
import com.example.zzserver.rooms.entity.Rooms;
import com.example.zzserver.rooms.repository.RoomsRepository;
import com.example.zzserver.rooms.service.RoomImageService;
import com.example.zzserver.rooms.service.RoomsService;
import org.assertj.core.api.Assertions;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class roomServiceTest {

    @Mock
    private RoomsRepository roomsRepository;

    @Mock
    private RoomImageService roomImageService;

    @InjectMocks
    private RoomsService roomsService;


    @Test
    @DisplayName("방 조회 성공")
    void findRoom_success() {
        UUID roomId = UUID.randomUUID();

        Rooms room = Rooms.builder()
                .id(roomId)
                .name("Deluxe Room")
                .peopleCount(5)
                .build();

        when(roomsRepository.findById(roomId)).thenReturn(Optional.of(room));

        RoomsResponse result = roomsService.findById(roomId);

        Assertions.assertThat(result.getId()).isEqualTo(roomId);
    }

    @Test
    @DisplayName("방 조회 실패 - NOT_FOUND")
    void findRoom_fail() {
        UUID id = UUID.randomUUID();
        when(roomsRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomsService.findById(id))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.ROOM_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("재고 차감 성공")
    void decreaseStock_success() {
        Rooms room = Rooms.builder()
                .id(UUID.randomUUID())
                .peopleCount(3)
                .build();

        room.decreaseAvailable(2);

        Assertions.assertThat(room.getPeopleCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("재고 차감 실패 - 부족함")
    void decreaseStock_fail() {
        Rooms room = Rooms.builder()
                .id(UUID.randomUUID())
                .peopleCount(1)
                .build();

        assertThatThrownBy(() -> room.decreaseAvailable(5))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("방 생성 성공 - 이미지 없음")
    void createRoom_noImages_success() {
        UUID id = UUID.randomUUID();

        RoomsRequest.Request req = RoomsRequest.Request
                .builder()
                .accommodationId(UUID.randomUUID())
                .name("Deluxe")
                .maxOccupacy(4)
                .peopleCount(4)
                .available(true)
                .build();

        Rooms saved = Rooms.builder()
                .id(id)
                .name(req.getName())
                .maxOccupacy(req.getMaxOccupacy())
                .peopleCount(req.getPeopleCount())
                .available(req.isAvailable())
                .accommodationId(req.getAccommodationId())
                .build();

        when(roomsRepository.save(any(Rooms.class))).thenReturn(saved);

        UUID result = roomsService.create(req, null);

        assertThat(result).isEqualTo(id);
        verify(roomImageService, never()).uploadRoomsImages(any(), any());
    }

    @Test
    @DisplayName("방 생성 성공 - 이미지 포함")
    void createRoom_withImages_success() {
        UUID id = UUID.randomUUID();

        RoomsRequest.Request req = RoomsRequest.Request.builder()
                .accommodationId(UUID.randomUUID())
                .name("Standard")
                .maxOccupacy(2)
                .peopleCount(2)
                .available(true)
                .build();

        when(roomsRepository.save(any())).thenReturn(
                Rooms.builder().id(id).build()
        );

        List<MultipartFile> images = List.of(mock(MultipartFile.class));

        UUID result = roomsService.create(req, images);

        assertThat(result).isEqualTo(id);
        verify(roomImageService, times(1)).uploadRoomsImages(eq(id), eq(images));
    }

    @Test
    @DisplayName("방 수정 성공")
    void updateRoom_success() {
        UUID roomId = UUID.randomUUID();
        Rooms room = Rooms.builder()
                .id(roomId)
                .name("Old")
                .maxOccupacy(2)
                .peopleCount(2)
                .available(true)
                .build();

        RoomsRequest.Update req = RoomsRequest.Update.builder()
                .name("NewName")
                .maxOccupacy(5)
                .peopleCount(5)
                .available(false)
                .build();

        when(roomsRepository.findById(roomId)).thenReturn(Optional.of(room));

        List<MultipartFile> newImages = List.of(mock(MultipartFile.class));
        List<UUID> deleteImageIds = List.of(UUID.randomUUID());

        roomsService.update(roomId, req, newImages, deleteImageIds);

        assertThat(room.getName()).isEqualTo("NewName");
        assertThat(room.getPeopleCount()).isEqualTo(5);
        assertThat(room.isAvailable()).isFalse();

        //verify(roomImageService, times(1)).deleteRoomImages(deleteImageIds);
        verify(roomImageService, times(1)).uploadRoomsImages(roomId, newImages);
    }

    @Test
    @DisplayName("방 수정 실패 - Room 없음")
    void updateRoom_notFound() {
        UUID id = UUID.randomUUID();

        RoomsRequest.Update req = RoomsRequest.Update.builder()
                .name("test")
                .maxOccupacy(4)
                .peopleCount(4)
                .available(true)
                .build();

        when(roomsRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomsService.update(id, req, null, null))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.ROOM_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("방 삭제 성공")
    void deleteRoom_success() {
        UUID id = UUID.randomUUID();

        when(roomsRepository.existsById(id)).thenReturn(true);

        roomsService.delete(id);

        verify(roomImageService, times(1)).deleteImagesByRoomId(id);
        verify(roomsRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("방 삭제 실패 - Room 없음")
    void deleteRoom_notFound() {
        UUID id = UUID.randomUUID();

        when(roomsRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> roomsService.delete(id))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.ROOM_NOT_FOUND.getMessage());
    }
}
