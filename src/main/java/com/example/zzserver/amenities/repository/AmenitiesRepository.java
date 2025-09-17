package com.example.zzserver.amenities.repository;

import com.example.zzserver.amenities.entity.Amenities;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AmenitiesRepository extends JpaRepository<Amenities, UUID> {

}
