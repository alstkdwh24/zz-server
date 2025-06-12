package com.example.zzserver.accommodation.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "ACCOMMODATION_IMAGES")
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
