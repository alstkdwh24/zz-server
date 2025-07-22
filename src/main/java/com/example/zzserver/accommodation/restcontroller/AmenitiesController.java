package com.example.zzserver.accommodation.restcontroller;

import com.example.zzserver.accommodation.dto.request.AmenitiesRequest;
import com.example.zzserver.accommodation.dto.response.AmenitiesResponse;
import com.example.zzserver.accommodation.service.AmenitiesService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/amenities")
@AllArgsConstructor
public class AmenitiesController {

    private final AmenitiesService amenitiesService;

    @PostMapping
    public ResponseEntity<UUID> create(@RequestBody AmenitiesRequest request) {
        return ResponseEntity.ok(amenitiesService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<AmenitiesResponse>> findAll() {
        return ResponseEntity.ok(amenitiesService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        amenitiesService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
