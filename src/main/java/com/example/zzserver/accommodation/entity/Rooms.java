package com.example.zzserver.accommodation.entity;

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
@Table(name = "ROOMS")
@NoArgsConstructor
@AllArgsConstructor
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

  public UUID getId() {
    return id;
  }

  public UUID getAccommodationId() {
    return accommodationId;
  }

  public String getName() {
    return name;
  }

  public long getMaxOccupacy() {
    return maxOccupacy;
  }

  public boolean isAvailable() {
    return available;
  }

  public Integer getPeopleCount() {
    return peopleCount;
  }

  public UUID getAccommodationId(UUID accommodationId ) {
    return this.accommodationId;
  }

  public String getName(String name) {
    return name;
  }

  public long getMaxOccupacy(long maxOccupacy) {
    return this.maxOccupacy;
  }

  public Integer getPeopleCount(int peopleCount) {
    return this.peopleCount;
  }

  public void setAvailable(boolean available) {
    this.available = available;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public void setAccommodationId(UUID accommodationId) {
    this.accommodationId = accommodationId;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setMaxOccupacy(long maxOccupacy) {
    this.maxOccupacy = maxOccupacy;
  }

  public void setPeopleCount(Integer peopleCount) {
    this.peopleCount = peopleCount;
  }

  public void update(String name, Long maxOccupacy, boolean isAvailable, Integer peopleCount){
    this.name = name;
    this.maxOccupacy = maxOccupacy;
    this.available = isAvailable;
    this.peopleCount = peopleCount;
  }
}
