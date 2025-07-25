package com.example.zzserver.accommodation.restcontroller;

import com.example.zzserver.accommodation.dto.request.RoomsRequest;
import com.example.zzserver.accommodation.dto.response.RoomsResponse;
import com.example.zzserver.accommodation.service.RoomsService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class RoomsController {

    private final RoomsService roomsService;

    // 방 등록
    @PostMapping
    public ResponseEntity<RoomsResponse> createRoom(@RequestBody RoomsRequest request) {
        return ResponseEntity.ok(roomsService.create(request));
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
    public ResponseEntity<RoomsResponse> updateRoom(@PathVariable UUID id, @RequestBody RoomsRequest request) {
        return ResponseEntity.ok(roomsService.update(id, request));
    }

    // 방 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable UUID id) {
        roomsService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
