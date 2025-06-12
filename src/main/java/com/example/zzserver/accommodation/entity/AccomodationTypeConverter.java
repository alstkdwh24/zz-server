package com.example.zzserver.accommodation.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AccomodationTypeConverter implements AttributeConverter<AccommodationType, String> {

  @Override
  public String convertToDatabaseColumn(AccommodationType attribute) {
    if (attribute == null) {
      return null;
    }
    return attribute.name();
  }

  @Override
  public AccommodationType convertToEntityAttribute(String dbData) {
    if (dbData == null) {
      return null;
    }
    return AccommodationType.valueOf(dbData);
  }
}
