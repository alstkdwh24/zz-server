package com.example.zzserver.amenities.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRoomAmenities is a Querydsl query type for RoomAmenities
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRoomAmenities extends EntityPathBase<RoomAmenities> {

    private static final long serialVersionUID = -586407454L;

    public static final QRoomAmenities roomAmenities = new QRoomAmenities("roomAmenities");

    public final ComparablePath<java.util.UUID> amenityId = createComparable("amenityId", java.util.UUID.class);

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final ComparablePath<java.util.UUID> roomId = createComparable("roomId", java.util.UUID.class);

    public QRoomAmenities(String variable) {
        super(RoomAmenities.class, forVariable(variable));
    }

    public QRoomAmenities(Path<? extends RoomAmenities> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRoomAmenities(PathMetadata metadata) {
        super(RoomAmenities.class, metadata);
    }

}

