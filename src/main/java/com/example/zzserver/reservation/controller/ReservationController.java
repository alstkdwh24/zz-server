package com.example.zzserver.reservation.controller;

import com.example.zzserver.reservation.consts.ReservationStatus;
import com.example.zzserver.reservation.dto.ReservationDto;
import com.example.zzserver.reservation.service.RoomReservationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reservation")
@AllArgsConstructor
public class ReservationController {

    private final RoomReservationService roomReservationService;

    @GetMapping("/member/{id}")
    public ResponseEntity<List<ReservationDto.Response>> getReservationsByMember(@PathVariable("id") UUID memberId) {
        return ResponseEntity.ok(roomReservationService.getReservationsByMember(memberId));
    }

    @GetMapping("/room/{id}")
    public ResponseEntity<?> getReservationsByRoom(@PathVariable("id") UUID roomId) {
        return ResponseEntity.ok(roomReservationService.getReservationsByRoom(roomId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getReservationsByStatus(@PathVariable("status") ReservationStatus status){
        return ResponseEntity.ok(roomReservationService.getReservationsByStatus(status));
    }

    @PostMapping
    public ResponseEntity<UUID> createReservation(@RequestBody @Valid ReservationDto.Request request) {
        UUID reservationId = roomReservationService.createReservation(request);
        return ResponseEntity.ok(reservationId);
    }

    @PatchMapping("/{reservationId}/confirm")
    public ResponseEntity<Void> confirmReservation(@PathVariable UUID reservationId) {
        roomReservationService.confirmReservation(reservationId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/cancel")
    public ResponseEntity<Void> cancelReservation(@RequestBody @Valid ReservationDto.Request request) {
        roomReservationService.cancelReservation(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{memberId}/{status}")
    public ResponseEntity<?> getReservationsByMemberAndStatus(@PathVariable("memberId") UUID memberId, @PathVariable("status") ReservationStatus status) {
        return ResponseEntity.ok(roomReservationService.getReservationsByMemberAndStatus(memberId,status));
    }
}
