package com.example.zzserver.amenities.controller;

import com.example.zzserver.amenities.dto.request.RoomAmenityRequest;
import com.example.zzserver.amenities.dto.response.RoomAmenityResponse;
import com.example.zzserver.amenities.service.RoomsAmenitiesService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rooms-amenities")
@AllArgsConstructor
public class RoomsAmenitiesController {

    private final RoomsAmenitiesService roomAmenityService;

    // 등록
    @PostMapping
    public ResponseEntity<UUID> create(@RequestBody RoomAmenityRequest request) {
        return ResponseEntity.ok(roomAmenityService.create(request.getRoomId(),request.getAmenityId()));
    }

    // 특정 방에 대한 편의시설 조회
    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<RoomAmenityResponse>> getAmenitiesByRoom(@PathVariable UUID roomId) {
        return ResponseEntity.ok(roomAmenityService.findByRoomId(roomId));
    }

    // 삭제 (roomId + amenityId 조합으로)
    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestBody RoomAmenityRequest request) {
        roomAmenityService.delete(request);
        return ResponseEntity.noContent().build();
    }
}
