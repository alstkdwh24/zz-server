package com.example.zzserver.accommodation.restcontroller;

import com.example.zzserver.accommodation.dto.request.AccommodationRequest;
import com.example.zzserver.accommodation.dto.request.AccommodationSearchCondition;
import com.example.zzserver.accommodation.service.AccommodationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
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

  @GetMapping("/search")
  public ResponseEntity<?> getAccommodationSearch(@RequestBody AccommodationSearchCondition condition) {
    return ResponseEntity.ok(accommodationService.search(condition));
  }

  @PostMapping("/")
  public ResponseEntity<?> createAccommodations(@RequestPart(value = "request") AccommodationRequest accommodationRequest,
                                                @RequestPart(value = "images",required = false) List<MultipartFile>files) {
    return ResponseEntity.ok(accommodationService.createAccommodation(accommodationRequest,files));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<?> updateAccommodations(@PathVariable("id") UUID id,
                                                @RequestPart(value = "request") AccommodationRequest accommodationRequest,
                                                @RequestPart(value = "updated",required = false) List<MultipartFile> newImages,
                                                @RequestPart(value = "deleteImageIds", required = false) List<UUID> imageIds) {
    return ResponseEntity.ok(accommodationService.updateAccommodations(id,accommodationRequest,newImages,imageIds));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteAccommodations(@PathVariable("id") UUID id) {
    accommodationService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

}
