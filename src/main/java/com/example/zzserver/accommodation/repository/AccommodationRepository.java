package com.example.zzserver.accommodation.repository;

import com.example.zzserver.accommodation.entity.Accommodations;
import java.util.List;
import java.util.UUID;

import com.example.zzserver.accommodation.repository.custom.CustomAccommodationRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccommodationRepository extends JpaRepository<Accommodations, UUID> , CustomAccommodationRepository {

  List<Accommodations> findByDisplayedTrue();
}
