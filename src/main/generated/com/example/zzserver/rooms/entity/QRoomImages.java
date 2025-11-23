package com.example.zzserver.rooms.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRoomImages is a Querydsl query type for RoomImages
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRoomImages extends EntityPathBase<RoomImages> {

    private static final long serialVersionUID = 1564668974L;

    public static final QRoomImages roomImages = new QRoomImages("roomImages");

    public final BooleanPath displayed = createBoolean("displayed");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final StringPath imageUrl = createString("imageUrl");

    public final ComparablePath<java.util.UUID> roomId = createComparable("roomId", java.util.UUID.class);

    public QRoomImages(String variable) {
        super(RoomImages.class, forVariable(variable));
    }

    public QRoomImages(Path<? extends RoomImages> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRoomImages(PathMetadata metadata) {
        super(RoomImages.class, metadata);
    }

}

