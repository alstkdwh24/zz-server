package com.example.zzserver.reservation.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "ROOM_RESERVATIONS")
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

  private ReservationStatus status;
}
