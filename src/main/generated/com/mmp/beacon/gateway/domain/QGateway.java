package com.mmp.beacon.gateway.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QGateway is a Querydsl query type for Gateway
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QGateway extends EntityPathBase<Gateway> {

    private static final long serialVersionUID = -1049313021L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QGateway gateway = new QGateway("gateway");

    public final com.mmp.beacon.global.domain.QBaseEntity _super = new com.mmp.beacon.global.domain.QBaseEntity(this);

    public final com.mmp.beacon.company.domain.QCompany company;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createAt = _super.createAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath macAddr = createString("macAddr");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updateAt = _super.updateAt;

    public QGateway(String variable) {
        this(Gateway.class, forVariable(variable), INITS);
    }

    public QGateway(Path<? extends Gateway> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QGateway(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QGateway(PathMetadata metadata, PathInits inits) {
        this(Gateway.class, metadata, inits);
    }

    public QGateway(Class<? extends Gateway> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.company = inits.isInitialized("company") ? new com.mmp.beacon.company.domain.QCompany(forProperty("company")) : null;
    }

}

