package com.example.zzserver.accommodation.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@Builder
@Table(name = "ROOM_AMENITIES")
@NoArgsConstructor
@AllArgsConstructor
public class RoomAmenities {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  // TODO 엔티티 관계 매핑
  private UUID roomId;

  // TODO 엔티티 관계 매핑
  private UUID amenityId;

  public void setId(UUID id) {
    this.id = id;
  }

  public void setRoomId(UUID roomId) {
    this.roomId = roomId;
  }

  public void setAmenityId(UUID amenityId) {
    this.amenityId = amenityId;
  }

  public UUID getId() {
    return id;
  }

  public UUID getRoomId() {
    return roomId;
  }

  public UUID getAmenityId() {
    return amenityId;
  }
}
