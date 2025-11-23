package com.example.zzserver.rooms.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDiscountPolicy is a Querydsl query type for DiscountPolicy
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDiscountPolicy extends EntityPathBase<DiscountPolicy> {

    private static final long serialVersionUID = 1898230094L;

    public static final QDiscountPolicy discountPolicy = new QDiscountPolicy("discountPolicy");

    public final ComparablePath<java.util.UUID> accommodationId = createComparable("accommodationId", java.util.UUID.class);

    public final BooleanPath active = createBoolean("active");

    public final DateTimePath<java.time.LocalDateTime> endDate = createDateTime("endDate", java.time.LocalDateTime.class);

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final NumberPath<Double> rate = createNumber("rate", Double.class);

    public final ComparablePath<java.util.UUID> roomId = createComparable("roomId", java.util.UUID.class);

    public final EnumPath<com.example.zzserver.rooms.consts.DiscountScope> scope = createEnum("scope", com.example.zzserver.rooms.consts.DiscountScope.class);

    public final DateTimePath<java.time.LocalDateTime> startDate = createDateTime("startDate", java.time.LocalDateTime.class);

    public final EnumPath<com.example.zzserver.rooms.consts.DiscountType> type = createEnum("type", com.example.zzserver.rooms.consts.DiscountType.class);

    public QDiscountPolicy(String variable) {
        super(DiscountPolicy.class, forVariable(variable));
    }

    public QDiscountPolicy(Path<? extends DiscountPolicy> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDiscountPolicy(PathMetadata metadata) {
        super(DiscountPolicy.class, metadata);
    }

}

