package com.example.zzserver.accommodation.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAccommodationImages is a Querydsl query type for AccommodationImages
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAccommodationImages extends EntityPathBase<AccommodationImages> {

    private static final long serialVersionUID = 1582143285L;

    public static final QAccommodationImages accommodationImages = new QAccommodationImages("accommodationImages");

    public final ComparablePath<java.util.UUID> accommodationId = createComparable("accommodationId", java.util.UUID.class);

    public final BooleanPath displayed = createBoolean("displayed");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final StringPath imageUrl = createString("imageUrl");

    public QAccommodationImages(String variable) {
        super(AccommodationImages.class, forVariable(variable));
    }

    public QAccommodationImages(Path<? extends AccommodationImages> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAccommodationImages(PathMetadata metadata) {
        super(AccommodationImages.class, metadata);
    }

}

