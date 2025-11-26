package com.example.zzserver.rooms.entity;

import com.example.zzserver.config.exception.CustomException;
import com.example.zzserver.config.exception.ErrorCode;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
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

  private Integer stockCount;

  // 방 가격
  private BigDecimal basePrice;

  // 할인된 가격
  private BigDecimal discountedPrice;

  public void update(String name, Long maxOccupacy, boolean isAvailable, Integer stockCount){
    this.name = name;
    this.maxOccupacy = maxOccupacy;
    this.available = isAvailable;
    this.stockCount = stockCount;
  }

  public void decreaseAvailable(int count) {
    if (count < 0) {
      throw new CustomException(ErrorCode.INVALID_REQUEST);
    }

    if (count == 0) {
      return;
    }

    if (this.stockCount == null || this.stockCount < count) {
      throw new CustomException(ErrorCode.RESERVATION_OVERLAP);
    }

    this.stockCount -= count;

    if (this.stockCount == 0) {
      this.available = false;
    }
  }

  public void increaseAvailable(int count) {
    if (count <= 0) {
      throw new CustomException(ErrorCode.INVALID_REQUEST);
    }
    this.stockCount += count;
    this.available = true;
  }
}
