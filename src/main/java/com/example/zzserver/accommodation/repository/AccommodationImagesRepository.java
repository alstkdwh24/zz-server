package com.example.zzserver.accommodation.repository;

import com.example.zzserver.accommodation.dto.response.AccommodationImageResponse;
import com.example.zzserver.accommodation.entity.AccommodationImages;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AccommodationImagesRepository extends JpaRepository<AccommodationImages, UUID> {

    List<AccommodationImages> findByAccommodationId(UUID accommodationId);
}
