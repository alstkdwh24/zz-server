package com.example.zzserver.address.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public class Address {

  private String zipCode;

  private String address;

  private String detailAddress;

  public static Address of(String zipCode, String address, String detailAddress) {
    Address address1 = new Address();
    address1.zipCode = zipCode;
    address1.address = address;
    address1.detailAddress = detailAddress;
    return address1;
  }
}
