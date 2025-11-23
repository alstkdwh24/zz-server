package com.example.zzserver.accommodation.repository.custom;

import com.example.zzserver.accommodation.dto.request.AccommodationSearchCondition;
import com.example.zzserver.accommodation.dto.response.AccommodationSearchResponse;
import com.example.zzserver.accommodation.entity.QAccommodationImages;
import com.example.zzserver.accommodation.entity.QAccommodations;
import com.example.zzserver.rooms.entity.QRooms;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CustomAccommodationRepositoryImpl implements CustomAccommodationRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<AccommodationSearchResponse> search(AccommodationSearchCondition condition) {
        QAccommodations acc = QAccommodations.accommodations;
        QRooms room = QRooms.rooms;
        // 숙소 이미지.
        QAccommodationImages img = QAccommodationImages.accommodationImages;

        return  queryFactory
                .select(Projections.constructor(
                        AccommodationSearchResponse.class,
                        acc.id,
                        acc.name,
                        acc.address.address,
                        room.basePrice.min(),  // 숙소 최저가
                        room.basePrice.max(),  // 숙소 최고가
                        room.available,
                        img.imageUrl.max()
                ))
                .from(acc)
                .leftJoin(room).on(room.accommodationId.eq(acc.id))
                .leftJoin(img).on(img.accommodationId.eq(acc.id)
                        .and(img.displayed.isTrue()))
                .where(
                        containsKeyword(condition.getKeyword()),
                        eqCity(condition.getCity()),
                        gtePeople(condition.getPeopleCount()),
                        gtePrice(condition.getMinPrice()),
                        ltePrice(condition.getMaxPrice())
                )
                .groupBy(acc.id)
                .orderBy(sortBy(condition.getSort(), room, acc))
                .fetch();
    }

    private BooleanExpression containsKeyword(String keyword) {
        return keyword == null ? null : QAccommodations.accommodations.name.containsIgnoreCase(keyword);
    }

    private BooleanExpression eqCity(String city) {
        return city == null ? null : QAccommodations.accommodations.address.address.eq(city);
    }

    private BooleanExpression gtePeople(Integer people) {
        return people == null ? null : QRooms.rooms.peopleCount.goe(people);
    }

    private BooleanExpression gtePrice(Integer min) {
        return min == null ? null : QRooms.rooms.basePrice.goe(min);
    }

    private BooleanExpression ltePrice(Integer max) {
        return max == null ? null : QRooms.rooms.basePrice.loe(max);
    }
    
    // 검색 정렬
    private OrderSpecifier<?> sortBy(String sort, QRooms room, QAccommodations acc) {
        if (sort == null) return room.basePrice.asc(); // 기본: 최저가 순

        return switch (sort) {
            case "priceAsc" -> room.basePrice.asc();
            case "priceDesc" -> room.basePrice.desc();
            case "name" -> acc.name.asc();
            case "latest" -> acc.id.desc(); // UUID 난수지만 그래도 최신 느낌
            default -> room.basePrice.asc();
        };
    }
}
