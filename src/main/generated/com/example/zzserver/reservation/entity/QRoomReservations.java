package com.example.zzserver.reservation.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRoomReservations is a Querydsl query type for RoomReservations
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRoomReservations extends EntityPathBase<RoomReservations> {

    private static final long serialVersionUID = 187183025L;

    public static final QRoomReservations roomReservations = new QRoomReservations("roomReservations");

    public final ComparablePath<java.util.UUID> cartId = createComparable("cartId", java.util.UUID.class);

    public final DateTimePath<java.time.LocalDateTime> checkIn = createDateTime("checkIn", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> checkOut = createDateTime("checkOut", java.time.LocalDateTime.class);

    public final NumberPath<java.math.BigDecimal> finalPrice = createNumber("finalPrice", java.math.BigDecimal.class);

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final ComparablePath<java.util.UUID> memberId = createComparable("memberId", java.util.UUID.class);

    public final NumberPath<java.math.BigDecimal> originalPrice = createNumber("originalPrice", java.math.BigDecimal.class);

    public final DateTimePath<java.time.LocalDateTime> reservedAt = createDateTime("reservedAt", java.time.LocalDateTime.class);

    public final ComparablePath<java.util.UUID> roomId = createComparable("roomId", java.util.UUID.class);

    public final EnumPath<com.example.zzserver.reservation.consts.ReservationStatus> status = createEnum("status", com.example.zzserver.reservation.consts.ReservationStatus.class);

    public QRoomReservations(String variable) {
        super(RoomReservations.class, forVariable(variable));
    }

    public QRoomReservations(Path<? extends RoomReservations> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRoomReservations(PathMetadata metadata) {
        super(RoomReservations.class, metadata);
    }

}

