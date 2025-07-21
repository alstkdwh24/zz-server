package com.example.zzserver.accommodation.repository;

import com.example.zzserver.accommodation.entity.RoomImages;
import com.example.zzserver.accommodation.entity.Rooms;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RoomsRepository extends JpaRepository<Rooms, UUID> {

    List<Rooms> findByAccommodationId(UUID accommodationId);
}
