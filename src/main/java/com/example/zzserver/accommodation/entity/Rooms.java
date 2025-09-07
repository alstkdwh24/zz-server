package com.example.zzserver.accommodation.entity;

import com.example.zzserver.config.exception.CustomException;
import com.example.zzserver.config.exception.ErrorCode;
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

  public void update(String name, Long maxOccupacy, boolean isAvailable, Integer peopleCount){
    this.name = name;
    this.maxOccupacy = maxOccupacy;
    this.available = isAvailable;
    this.peopleCount = peopleCount;
  }

  public void decreaseAvailable(int count) {
    if (this.peopleCount == null || this.peopleCount < count) {
      throw new CustomException(ErrorCode.RESERVATION_OVERLAP);
    }
    this.peopleCount -= count;
    if (this.peopleCount == 0) {
      this.available = false;
    }
  }

  public void increaseAvailable(int count) {
    this.peopleCount += count;
    this.available = true;
  }
}
