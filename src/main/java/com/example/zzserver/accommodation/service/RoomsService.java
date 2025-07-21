package com.example.zzserver.accommodation.service;

import com.example.zzserver.accommodation.dto.request.RoomsRequest;
import com.example.zzserver.accommodation.dto.response.RoomsResponse;
import com.example.zzserver.accommodation.entity.Rooms;
import com.example.zzserver.accommodation.repository.RoomsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RoomsService {

    private final RoomsRepository roomsRepository;

    public RoomsResponse create(RoomsRequest request) {
        Rooms room = new Rooms();
        room.setAccommodationId(request.getAccommodationId());
        room.setName(request.getName());
        room.setMaxOccupacy(request.getMaxOccupacy());
        room.setPeopleCount(request.getPeopleCount());
        room.setAvailable(request.isAvailable());
        return RoomsResponse.from(roomsRepository.save(room));
    }

    public RoomsResponse findById(UUID roomsId) {
        return roomsRepository.findById(roomsId)
                .map(rooms -> RoomsResponse.from(rooms))
                .orElseThrow(() -> new RuntimeException("방을 찾을 수 없습니다."));
    }

    public List<RoomsResponse> getAllByAccommodation(UUID accommodationId) {
        return roomsRepository.findByAccommodationId(accommodationId).stream()
                .map(RoomsResponse::from)
                .toList();
    }

    public RoomsResponse update(UUID id, RoomsRequest request) {
        Rooms room = roomsRepository.findById(id).orElseThrow(()->new RuntimeException("방이 없습니다."));
        room.update(request.getName(), request.getMaxOccupacy(), request.isAvailable(), request.getPeopleCount());
        return RoomsResponse.from(roomsRepository.save(room));
    }

    public void delete(UUID id) {
        roomsRepository.deleteById(id);
    }
}
