package com.example.zzserver.accommodation.entity;

import jakarta.persistence.*;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Builder
@Table(name = "ACCOMMODATION_IMAGES")
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationImages {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private String imageUrl;

  @ColumnDefault("false")
  private boolean displayed;

  // TODO 엔티티 관계 매핑
  private UUID accommodationId;
}
