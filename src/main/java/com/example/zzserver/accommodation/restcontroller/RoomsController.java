package com.example.zzserver.accommodation.restcontroller;

import com.example.zzserver.accommodation.dto.response.RoomsResponse;
import com.example.zzserver.accommodation.service.RoomsService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class RoomsController {

    private final RoomsService roomsService;

    // 방 등록
    @PostMapping
    public ResponseEntity<UUID> createRoom(@RequestBody RoomsRequest.Request request,
                                           @RequestPart(value = "room-images",required = false) List<MultipartFile> roomImages) {
        return ResponseEntity.ok(roomsService.create(request,roomImages));
    }

    // 특정 방 조회
    @GetMapping("/{id}")
    public ResponseEntity<RoomsResponse> getRoom(@PathVariable UUID id) {
        return ResponseEntity.ok(roomsService.findById(id));
    }

    // 숙소에 소속된 방 전체 조회
    @GetMapping("/accommodation/{accommodationId}")
    public ResponseEntity<List<RoomsResponse>> getRoomsByAccommodation(@PathVariable UUID accommodationId) {
        return ResponseEntity.ok(roomsService.getAllByAccommodation(accommodationId));
    }

    // 방 정보 수정
    @PatchMapping("/{id}")
    public ResponseEntity<UUID> updateRoom(@PathVariable UUID id,
                                           @RequestBody RoomsRequest.Update request,
                                           @RequestPart(value = "room-images",required = false)List<MultipartFile> images) {
        return ResponseEntity.ok(roomsService.update(id, request,images,request.getDeleteImageIds()));
    }

    // 방 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable UUID id) {
        roomsService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
