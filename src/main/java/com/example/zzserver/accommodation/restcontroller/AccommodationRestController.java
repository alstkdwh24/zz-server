package com.example.zzserver.accommodation.restcontroller;

import com.example.zzserver.accommodation.dto.request.AccommodationRequest;
import com.example.zzserver.accommodation.service.AccommodationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/accommodations")
public class AccommodationRestController {

  private final AccommodationService accommodationService;

  public AccommodationRestController(AccommodationService accommodationService) {
    this.accommodationService = accommodationService;
  }

  @GetMapping("/")
  public ResponseEntity<?> getAccommodations() {
    return ResponseEntity.ok(accommodationService.readDisplayedList());
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getAccommodationById(@PathVariable("id") UUID id) {
    return ResponseEntity.ok(accommodationService.findById(id));
  }

  @PostMapping("/")
  public ResponseEntity<?> createAccommodations(@RequestBody AccommodationRequest accommodationRequest) {
    return ResponseEntity.ok(accommodationService.createAccommodation(accommodationRequest));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<?> updateAccommodations(@PathVariable("id") UUID id, @RequestBody AccommodationRequest accommodationRequest) {
    return ResponseEntity.ok(accommodationService.updateAccommodations(id,accommodationRequest));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteAccommodations(@PathVariable("id") UUID id) {
    accommodationService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

}
