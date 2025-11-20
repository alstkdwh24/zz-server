package com.example.zzserver.amenities.repository;


import com.example.zzserver.amenities.entity.Amenities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AmenitiesRepository extends JpaRepository<Amenities, UUID> {

}
