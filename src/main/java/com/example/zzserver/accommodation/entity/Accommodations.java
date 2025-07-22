package com.example.zzserver.accommodation.entity;

import com.example.zzserver.accommodation.consts.AccommodationType;
import com.example.zzserver.address.domain.Address;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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

@Getter
@Builder
@Entity
@Table(name = "ACCOMMODATIONS")
@NoArgsConstructor
@AllArgsConstructor
public class Accommodations {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  // TODO 엔티티 관계 매핑
  private UUID bussinessUserId;

  private String name;

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "zipCode", column = @Column(name = "zipCode")),
      @AttributeOverride(name = "address", column = @Column(name = "address")),
      @AttributeOverride(name = "detailAddress", column = @Column(name = "detailAddress"))
  })
  private Address address;

  private Double latitude;

  private Double longitude;

  private String phoneNumber;

  @ColumnDefault("false")
  private boolean displayed;

  private AccommodationType type;

  public void update(String name, String phoneNumber, Address address,
                     Double latitude, Double longitude, AccommodationType type) {
    this.name = name;
    this.phoneNumber = phoneNumber;
    this.address = address;
    this.latitude = latitude;
    this.longitude = longitude;
    this.type = type;
  }
}
