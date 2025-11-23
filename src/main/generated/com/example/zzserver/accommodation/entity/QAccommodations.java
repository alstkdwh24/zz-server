package com.example.zzserver.accommodation.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAccommodations is a Querydsl query type for Accommodations
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAccommodations extends EntityPathBase<Accommodations> {

    private static final long serialVersionUID = 410528182L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAccommodations accommodations = new QAccommodations("accommodations");

    public final com.example.zzserver.address.domain.QAddress address;

    public final ComparablePath<java.util.UUID> bussinessUserId = createComparable("bussinessUserId", java.util.UUID.class);

    public final BooleanPath displayed = createBoolean("displayed");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final NumberPath<Double> latitude = createNumber("latitude", Double.class);

    public final NumberPath<Double> longitude = createNumber("longitude", Double.class);

    public final StringPath name = createString("name");

    public final StringPath phoneNumber = createString("phoneNumber");

    public final EnumPath<com.example.zzserver.accommodation.consts.AccommodationType> type = createEnum("type", com.example.zzserver.accommodation.consts.AccommodationType.class);

    public QAccommodations(String variable) {
        this(Accommodations.class, forVariable(variable), INITS);
    }

    public QAccommodations(Path<? extends Accommodations> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAccommodations(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAccommodations(PathMetadata metadata, PathInits inits) {
        this(Accommodations.class, metadata, inits);
    }

    public QAccommodations(Class<? extends Accommodations> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.address = inits.isInitialized("address") ? new com.example.zzserver.address.domain.QAddress(forProperty("address")) : null;
    }

}

