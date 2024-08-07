package com.mmp.beacon.request.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProfileUpdateRequest is a Querydsl query type for ProfileUpdateRequest
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProfileUpdateRequest extends EntityPathBase<ProfileUpdateRequest> {

    private static final long serialVersionUID = -2082619447L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProfileUpdateRequest profileUpdateRequest = new QProfileUpdateRequest("profileUpdateRequest");

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final StringPath phone = createString("phone");

    public final StringPath position = createString("position");

    public final QRequest request;

    public QProfileUpdateRequest(String variable) {
        this(ProfileUpdateRequest.class, forVariable(variable), INITS);
    }

    public QProfileUpdateRequest(Path<? extends ProfileUpdateRequest> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProfileUpdateRequest(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProfileUpdateRequest(PathMetadata metadata, PathInits inits) {
        this(ProfileUpdateRequest.class, metadata, inits);
    }

    public QProfileUpdateRequest(Class<? extends ProfileUpdateRequest> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.request = inits.isInitialized("request") ? new QRequest(forProperty("request"), inits.get("request")) : null;
    }

}

