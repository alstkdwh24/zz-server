package com.example.zzserver.accommodation.repository;

import com.example.zzserver.accommodation.entity.RoomAmenities;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RoomAmenitiesRepository extends JpaRepository<RoomAmenities, UUID> {

    List<RoomAmenities> findByRoomId(UUID roomId);

    RoomAmenities findByRoomIdAndAmenityId(UUID roomId, UUID amenityId);
}
