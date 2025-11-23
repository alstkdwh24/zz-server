package com.example.zzserver.accommodation.repository.custom;

import com.example.zzserver.accommodation.dto.request.AccommodationSearchCondition;
import com.example.zzserver.accommodation.dto.response.AccommodationSearchResponse;

import java.util.List;

public interface CustomAccommodationRepository {
    //숙소 검색
    List<AccommodationSearchResponse> search(AccommodationSearchCondition condition);
}
