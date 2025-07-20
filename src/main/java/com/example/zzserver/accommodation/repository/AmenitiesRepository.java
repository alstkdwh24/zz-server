package com.example.zzserver.accommodation.repository;

import com.example.zzserver.accommodation.entity.Amenities;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AmenitiesRepository extends JpaRepository<Amenities, UUID> {

}
