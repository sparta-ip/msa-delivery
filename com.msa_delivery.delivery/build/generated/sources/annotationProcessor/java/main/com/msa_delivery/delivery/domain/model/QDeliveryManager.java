package com.msa_delivery.delivery.domain.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDeliveryManager is a Querydsl query type for DeliveryManager
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDeliveryManager extends EntityPathBase<DeliveryManager> {

    private static final long serialVersionUID = -908769637L;

    public static final QDeliveryManager deliveryManager = new QDeliveryManager("deliveryManager");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    //inherited
    public final StringPath deletedBy = _super.deletedBy;

    public final ListPath<Delivery, QDelivery> deliveries = this.<Delivery, QDelivery>createList("deliveries", Delivery.class, QDelivery.class, PathInits.DIRECT2);

    public final ListPath<DeliveryRoute, QDeliveryRoute> deliveryRoutes = this.<DeliveryRoute, QDeliveryRoute>createList("deliveryRoutes", DeliveryRoute.class, QDeliveryRoute.class, PathInits.DIRECT2);

    public final ComparablePath<java.util.UUID> hubId = createComparable("hubId", java.util.UUID.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final BooleanPath isDelete = _super.isDelete;

    public final ComparablePath<java.util.UUID> orderId = createComparable("orderId", java.util.UUID.class);

    public final NumberPath<Integer> sequence = createNumber("sequence", Integer.class);

    public final StringPath slackId = createString("slackId");

    public final EnumPath<DeliveryManagerType> type = createEnum("type", DeliveryManagerType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public QDeliveryManager(String variable) {
        super(DeliveryManager.class, forVariable(variable));
    }

    public QDeliveryManager(Path<? extends DeliveryManager> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDeliveryManager(PathMetadata metadata) {
        super(DeliveryManager.class, metadata);
    }

}

