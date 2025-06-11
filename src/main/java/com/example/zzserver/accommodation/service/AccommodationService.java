package com.example.zzserver.accommodation.service;

import com.example.zzserver.accommodation.dto.response.AccommodationResponseDto;
import com.example.zzserver.accommodation.entity.Accommodations;
import com.example.zzserver.accommodation.repository.AccommodationRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AccommodationService {

  private final AccommodationRepository accommodationRepository;

  public AccommodationService(AccommodationRepository accommodationRepository) {
    this.accommodationRepository = accommodationRepository;
  }

  public List<AccommodationResponseDto> readDisplayedList() {
    List<Accommodations> all = accommodationRepository.findByDisplayedTrue();

    return all.stream()
        .map(AccommodationResponseDto::from)
        .toList();
  }
}
