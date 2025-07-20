package com.example.zzserver.accommodation.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "AMENITIES")
public class Amenities {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private String name;

  private String iconUrl;

  public Amenities() {

  }

  public Amenities(UUID id, String name, String iconUrl) {
    this.id = id;
    this.name = name;
    this.iconUrl = iconUrl;
  }

  public UUID getId() {
    return id;
  }

  public String getName(String name) {
    return this.name;
  }

  public String getName() {
    return name;
  }

  public String getIconUrl(String iconUrl) {
    return this.iconUrl;
  }

  public String getIconUrl() {
    return iconUrl;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setIconUrl(String iconUrl) {
    this.iconUrl = iconUrl;
  }
}
