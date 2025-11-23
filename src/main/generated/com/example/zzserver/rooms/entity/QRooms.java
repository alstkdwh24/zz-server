package com.example.zzserver.rooms.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRooms is a Querydsl query type for Rooms
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRooms extends EntityPathBase<Rooms> {

    private static final long serialVersionUID = 100193565L;

    public static final QRooms rooms = new QRooms("rooms");

    public final ComparablePath<java.util.UUID> accommodationId = createComparable("accommodationId", java.util.UUID.class);

    public final BooleanPath available = createBoolean("available");

    public final NumberPath<java.math.BigDecimal> basePrice = createNumber("basePrice", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> discountedPrice = createNumber("discountedPrice", java.math.BigDecimal.class);

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final NumberPath<Long> maxOccupacy = createNumber("maxOccupacy", Long.class);

    public final StringPath name = createString("name");

    public final NumberPath<Integer> peopleCount = createNumber("peopleCount", Integer.class);

    public QRooms(String variable) {
        super(Rooms.class, forVariable(variable));
    }

    public QRooms(Path<? extends Rooms> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRooms(PathMetadata metadata) {
        super(Rooms.class, metadata);
    }

}

