package com.example.zzserver.reservation;

import com.example.zzserver.cart.entity.Cart;
import com.example.zzserver.cart.repository.CartRepository;
import com.example.zzserver.config.exception.CustomException;
import com.example.zzserver.config.exception.ErrorCode;
import com.example.zzserver.reservation.dto.ReservationDto;
import com.example.zzserver.reservation.entity.RoomReservations;
import com.example.zzserver.reservation.repository.RoomReservationRepository;
import com.example.zzserver.reservation.service.RoomReservationService;
import com.example.zzserver.rooms.entity.Rooms;
import com.example.zzserver.rooms.repository.RoomsRepository;
import com.example.zzserver.rooms.service.PriceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private RoomsRepository roomsRepository;

    @Mock
    private RoomReservationRepository reservationRepository;

    @Mock
    private PriceService priceService;

    @InjectMocks
    private RoomReservationService reservationService;

    private Cart cart;
    private Rooms room;

    @BeforeEach
    void init() {

        cart = Cart.builder()
                .id(UUID.randomUUID())
                .memberId(UUID.randomUUID())
                .roomId(UUID.randomUUID())
                .checkInDate(LocalDateTime.now())
                .checkOutDate(LocalDateTime.now().plusDays(2))
                .build();

        room = Rooms.builder()
                .id(cart.getRoomId())
                .basePrice(BigDecimal.valueOf(100_000))
                .stockCount(5)
                .build();
    }

    @Test
    @DisplayName("예약 생성 성공")
    void createReservation_success() {
        UUID reservationId = UUID.randomUUID();

        ReservationDto.Request req = ReservationDto.Request
                .builder()
                .cartId(cart.getId())
                .memberId(cart.getMemberId())
                .roomId(cart.getRoomId())
                .roomCount(1)
                .checkInDate(cart.getCheckInDate())
                .checkOutDate(cart.getCheckOutDate())
                .build();

        when(cartRepository.findById(cart.getId())).thenReturn(Optional.of(cart));
        when(roomsRepository.findByIdForUpdate(cart.getRoomId())).thenReturn(Optional.of(room));
        when(reservationRepository.existsOverlappingReservation(any(), any(), any()))
                .thenReturn(false);

        long nights = DAYS.between(cart.getCheckInDate(), cart.getCheckOutDate());
        when(priceService.calculatePrice(any(), nights, any()))
                .thenReturn(BigDecimal.valueOf(180_000));

        when(reservationRepository.save(any(RoomReservations.class)))
                .thenAnswer(invocation -> {
                    RoomReservations saved = invocation.getArgument(0);
                    return RoomReservations.builder()
                            .id(reservationId)  // 여기 세팅 가능
                            .checkIn(saved.getCheckIn())
                            .checkOut(saved.getCheckOut())
                            .reservedAt(saved.getReservedAt())
                            .originalPrice(saved.getOriginalPrice())
                            .finalPrice(saved.getFinalPrice())
                            .memberId(saved.getMemberId())
                            .roomId(saved.getRoomId())
                            .cartId(saved.getCartId())
                            .status(saved.getStatus())
                            .build();
                });

        UUID result = reservationService.createReservation(req);

        assertThat(result).isEqualTo(reservationId);
        assertThat(room.getStockCount()).isEqualTo(4);

        verify(cartRepository, times(1)).delete(cart);
        verify(reservationRepository, times(1)).save(any(RoomReservations.class));
    }

    @Test
    @DisplayName("예약 실패 - 장바구니 없음")
    void createReservation_cartNotFound() {
        ReservationDto.Request req = ReservationDto.Request.builder()
                .cartId(cart.getId())
                .memberId(cart.getMemberId())
                .roomId(cart.getRoomId())
                .checkInDate(cart.getCheckInDate())
                .checkOutDate(cart.getCheckOutDate())
                .build();

        when(cartRepository.findById(cart.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.createReservation(req))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.CART_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("예약 실패 - 방 없음")
    void createReservation_roomNotFound() {
        ReservationDto.Request req = ReservationDto.Request
                .builder()
                .cartId(cart.getId())
                .memberId(cart.getMemberId())
                .roomId(cart.getRoomId())
                .checkInDate(cart.getCheckInDate())
                .checkOutDate(cart.getCheckOutDate())
                .build();

        when(cartRepository.findById(cart.getId())).thenReturn(Optional.of(cart));
        when(roomsRepository.findByIdForUpdate(cart.getRoomId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.createReservation(req))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.ROOM_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("예약 실패 - 날짜 겹침")
    void createReservation_overlap() {
        ReservationDto.Request req = ReservationDto.Request
                .builder()
                .cartId(cart.getId())
                .memberId(cart.getMemberId())
                .roomId(cart.getRoomId())
                .checkInDate(cart.getCheckInDate())
                .checkOutDate(cart.getCheckOutDate())
                .build();

        when(cartRepository.findById(cart.getId())).thenReturn(Optional.of(cart));
        when(roomsRepository.findByIdForUpdate(cart.getRoomId())).thenReturn(Optional.of(room));
        when(reservationRepository.existsOverlappingReservation(any(), any(), any()))
                .thenReturn(true);

        assertThatThrownBy(() -> reservationService.createReservation(req))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.RESERVATION_OVERLAP.getMessage());
    }
}
