package com.example.zzserver.rooms.repository;

import com.example.zzserver.rooms.entity.RoomImages;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RoomsImagesRepository extends JpaRepository<RoomImages, UUID> {

    List<RoomImages> findByRoomId(UUID roomsId);

}
