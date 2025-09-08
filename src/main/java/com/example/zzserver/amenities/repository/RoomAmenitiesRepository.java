package com.example.zzserver.amenities.repository;

import com.example.zzserver.amenities.entity.RoomAmenities;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomAmenitiesRepository extends JpaRepository<RoomAmenities, UUID> {

    List<RoomAmenities> findByRoomId(UUID roomId);

    Optional<RoomAmenities> findByRoomIdAndAmenityId(UUID roomId, UUID amenityId);
}
