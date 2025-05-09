package com.msa_delivery.delivery.domain.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDeliveryRoute is a Querydsl query type for DeliveryRoute
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDeliveryRoute extends EntityPathBase<DeliveryRoute> {

    private static final long serialVersionUID = 1952697527L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDeliveryRoute deliveryRoute = new QDeliveryRoute("deliveryRoute");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final ComparablePath<java.util.UUID> arrivalId = createComparable("arrivalId", java.util.UUID.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    //inherited
    public final StringPath deletedBy = _super.deletedBy;

    public final QDelivery delivery;

    public final QDeliveryManager deliveryManager;

    public final EnumPath<DeliveryStatus> deliveryStatus = createEnum("deliveryStatus", DeliveryStatus.class);

    public final ComparablePath<java.util.UUID> departureId = createComparable("departureId", java.util.UUID.class);

    public final NumberPath<Integer> distance = createNumber("distance", Integer.class);

    public final NumberPath<Integer> duration = createNumber("duration", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> endTime = createDateTime("endTime", java.time.LocalDateTime.class);

    public final NumberPath<Integer> expectDistance = createNumber("expectDistance", Integer.class);

    public final NumberPath<Integer> expectDuration = createNumber("expectDuration", Integer.class);

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    //inherited
    public final BooleanPath isDelete = _super.isDelete;

    public final NumberPath<Integer> sequence = createNumber("sequence", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> startTime = createDateTime("startTime", java.time.LocalDateTime.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QDeliveryRoute(String variable) {
        this(DeliveryRoute.class, forVariable(variable), INITS);
    }

    public QDeliveryRoute(Path<? extends DeliveryRoute> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDeliveryRoute(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDeliveryRoute(PathMetadata metadata, PathInits inits) {
        this(DeliveryRoute.class, metadata, inits);
    }

    public QDeliveryRoute(Class<? extends DeliveryRoute> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.delivery = inits.isInitialized("delivery") ? new QDelivery(forProperty("delivery"), inits.get("delivery")) : null;
        this.deliveryManager = inits.isInitialized("deliveryManager") ? new QDeliveryManager(forProperty("deliveryManager")) : null;
    }

}

