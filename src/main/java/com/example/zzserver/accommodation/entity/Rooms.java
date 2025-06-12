package com.example.zzserver.accommodation.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "ROOMS")
public class Rooms {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  // TODO 엔티티 관계 매핑
  private UUID accommodationId;

  private String name;

  @ColumnDefault("1")
  private long maxOccupacy;

  @ColumnDefault("true")
  private boolean available;

  private Integer peopleCount;
}
