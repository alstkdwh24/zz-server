package com.example.zzserver.reservation.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ReservationStatusConverter implements AttributeConverter<ReservationStatus, String> {

  @Override
  public String convertToDatabaseColumn(ReservationStatus attribute) {
    if (attribute == null) {
      return null;
    }
    return attribute.name();
  }

  @Override
  public ReservationStatus convertToEntityAttribute(String dbData) {
    if (dbData == null) {
      return null;
    }
    return ReservationStatus.valueOf(dbData);
  }
}
