package com.example.zzserver.accommodation.restcontroller;

import com.example.zzserver.accommodation.service.AccommodationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccommodationRestController {

  private final AccommodationService accommodationService;

  public AccommodationRestController(AccommodationService accommodationService) {
    this.accommodationService = accommodationService;
  }

  @GetMapping("accommodations")
  public ResponseEntity<?> getAccommodations() {
    return ResponseEntity.ok(accommodationService.readDisplayedList());
  }
}
