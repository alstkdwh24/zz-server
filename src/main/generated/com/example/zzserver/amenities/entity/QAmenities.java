package com.example.zzserver.amenities.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAmenities is a Querydsl query type for Amenities
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAmenities extends EntityPathBase<Amenities> {

    private static final long serialVersionUID = -1465688195L;

    public static final QAmenities amenities = new QAmenities("amenities");

    public final StringPath iconUrl = createString("iconUrl");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final StringPath name = createString("name");

    public QAmenities(String variable) {
        super(Amenities.class, forVariable(variable));
    }

    public QAmenities(Path<? extends Amenities> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAmenities(PathMetadata metadata) {
        super(Amenities.class, metadata);
    }

}

