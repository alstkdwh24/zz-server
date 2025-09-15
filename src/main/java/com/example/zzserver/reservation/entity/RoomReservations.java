package com.example.zzserver.reservation.entity;

import com.example.zzserver.config.exception.CustomException;
import com.example.zzserver.config.exception.ErrorCode;
import com.example.zzserver.reservation.consts.ReservationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "ROOM_RESERVATIONS")
@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoomReservations {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private LocalDateTime checkIn;

  private LocalDateTime checkOut;

  private LocalDateTime reservedAt;

  // TODO 엔티티 관계 매핑
  private UUID memberId;

  // TODO 엔티티 관계 매핑
  private UUID roomId;

  // TODO 엔티티 관계 매핑
  private UUID cartId;

  @Enumerated(EnumType.STRING)
  private ReservationStatus status;

  // 예약 확인
  public void confirm() {
    if (this.status != ReservationStatus.PENDING) {
      throw new CustomException(ErrorCode.RESERVATION_CONFIRM);
    }
    this.status = ReservationStatus.CONFIRMED;
  }

  // 예약 취소
  public void cancel() {
    if (this.status == ReservationStatus.CANCELED) {
      throw new CustomException(ErrorCode.RESERVATION_CANCEL_OVERLAP);
    }
    this.status = ReservationStatus.CANCELED;
  }

}
