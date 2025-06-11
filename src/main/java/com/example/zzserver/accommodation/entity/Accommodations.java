package com.example.zzserver.accommodation.entity;

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
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "ACCOMMODATIONS")
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

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Address getAddress() {
    return address;
  }

  public Double getLatitude() {
    return latitude;
  }

  public Double getLongitude() {
    return longitude;
  }

  public AccommodationType getType() {
    return type;
  }
}
