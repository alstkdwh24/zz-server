package com.example.zzserver.accommodation.repository;

import com.example.zzserver.accommodation.entity.Accommodations;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccommodationRepository extends JpaRepository<Accommodations, UUID> {

  List<Accommodations> findByDisplayedTrue();
}
