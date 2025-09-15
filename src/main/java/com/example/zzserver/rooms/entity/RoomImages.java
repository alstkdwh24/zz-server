package com.example.zzserver.rooms.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Builder
@Table(name = "ROOM_IMAGES")
@NoArgsConstructor
@AllArgsConstructor
public class RoomImages {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private String imageUrl;

  @ColumnDefault("false")
  private boolean displayed;

  // TODO 엔티티 관계 매핑
  private UUID roomId;
}
