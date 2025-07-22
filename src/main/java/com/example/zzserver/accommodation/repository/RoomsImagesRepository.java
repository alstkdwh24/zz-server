package com.example.zzserver.accommodation.repository;

import com.example.zzserver.accommodation.entity.Rooms;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RoomsImagesRepository extends JpaRepository<Rooms, UUID> {

}
