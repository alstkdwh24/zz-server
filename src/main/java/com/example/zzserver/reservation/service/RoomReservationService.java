package com.example.zzserver.reservation.service;

import com.example.zzserver.accommodation.entity.Rooms;
import com.example.zzserver.accommodation.repository.RoomsRepository;
import com.example.zzserver.cart.entity.Cart;
import com.example.zzserver.cart.repository.CartRepository;
import com.example.zzserver.config.exception.CustomException;
import com.example.zzserver.config.exception.ErrorCode;
import com.example.zzserver.reservation.consts.ReservationStatus;
import com.example.zzserver.reservation.dto.ReservationDto;
import com.example.zzserver.reservation.entity.RoomReservations;
import com.example.zzserver.reservation.repository.RoomReservationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class RoomReservationService {

    private final RoomReservationRepository roomReservationRepository;

    private final RoomsRepository roomsRepository;

    private final CartRepository cartRepository;

    /**
     * 특정 회원(Member) 예약 내역 조회
     * @param memberId 회원 엔티티의 uuid
     * @return List<ReservationDto.Response>
     **/
    @Transactional(readOnly = true)
    public List<ReservationDto.Response> getReservationsByMember(UUID memberId) {
        return roomReservationRepository.findByMemberId(memberId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 특정 방(Room) 예약 내역 조회
     * @param roomId 방 엔티티의 uuid
     * @return List<ReservationDto.Response>
     **/
    @Transactional(readOnly = true)
    public List<ReservationDto.Response> getReservationsByRoom(UUID roomId) {
        return roomReservationRepository.findByRoomId(roomId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 상태별 예약 조회
     * @param status 예약 상태
     * @return List<ReservationDto.Response>
     **/
    @Transactional(readOnly = true)
    public List<ReservationDto.Response> getReservationsByStatus(ReservationStatus status) {
        return roomReservationRepository.findByStatus(status)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 유저별 + 상태 조건 예약 조회
     * @param memberId 회원 엔티티의 uuid
     * @param status 예약 상태
     * @return List<ReservationDto.Response>
     **/
    public List<ReservationDto.Response> getReservationsByMemberAndStatus(UUID memberId, ReservationStatus status) {
        return roomReservationRepository.findByMemberIdAndStatus(memberId, status)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 예약 생성
     * @param request 예약에 필요한 요청 dto
     * @exception CustomException : RESERVATION_OVERLAP, CART_NOT_FOUND, ROOM_NOT_FOUND
     * @return uuid 예약 엔티티에 생성된 uuid
     **/
    public UUID createReservation(ReservationDto.Request request) {
        // 장바구니 조회
        Cart cart = cartRepository.findById(request.getCartId())
                .orElseThrow(() -> new CustomException(ErrorCode.CART_NOT_FOUND));

        // 방 확인 -> 동시성 고려
        Rooms room = roomsRepository.findByIdForUpdate(cart.getRoomId())
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
        // 예약 충돌 확인
        boolean overlap = roomReservationRepository
                .existsOverlappingReservation(cart.getRoomId(), cart.getCheckInDate(),cart.getCheckOutDate());

        if (overlap) {
            throw new CustomException(ErrorCode.RESERVATION_OVERLAP);
        }
        // 예약 생성시 재고 차감
        room.decreaseAvailable(request.getRoomCount());

        // 예약 저장
        RoomReservations reservation = RoomReservations.builder()
                .memberId(request.getMemberId())
                .roomId(request.getRoomId())
                .checkIn(request.getCheckInDate())
                .checkOut(request.getCheckOutDate())
                .reservedAt(LocalDateTime.now())
                .status(ReservationStatus.PENDING)
                .build();

        // 장바구니 비우기
        cartRepository.delete(cart);
        // 예약 저장
        return roomReservationRepository.save(reservation).getId();
    }

    /**
     * 예약 확인 (PENDING -> CONFIRMED)
     * @param reservationId 예약 엔티티의 uuid
     **/
    public void confirmReservation(UUID reservationId) {
        RoomReservations reservation = roomReservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
        // 예약 확인
        reservation.confirm(); 
    }

    /**
     * 예약 취소 (CANCEL로 변경)
     * @param request 예약 취소에 필요한 dto
     **/
    public void cancelReservation(ReservationDto.Request request) {
        RoomReservations reservation = roomReservationRepository.findById(request.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
        //예약 취소
        reservation.cancel();

        // Room 재고 복구 (락 걸기)
        Rooms room = roomsRepository.findByIdForUpdate(reservation.getRoomId())
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));

        //재고 증가
        room.increaseAvailable(request.getRoomCount());
    }


    private ReservationDto.Response toDto(RoomReservations reservations) {
        return ReservationDto.Response
                .builder()
                .id(reservations.getId())
                .memberId(reservations.getMemberId())
                .roomId(reservations.getRoomId())
                .cartId(reservations.getCartId())
                .checkInDate(reservations.getCheckIn())
                .checkOutDate(reservations.getCheckOut())
                .reservedAt(reservations.getReservedAt())
                .build();
    }
}
