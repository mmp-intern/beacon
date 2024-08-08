package com.mmp.beacon.request.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPasswordResetRequest is a Querydsl query type for PasswordResetRequest
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPasswordResetRequest extends EntityPathBase<PasswordResetRequest> {

    private static final long serialVersionUID = 492729831L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPasswordResetRequest passwordResetRequest = new QPasswordResetRequest("passwordResetRequest");

    public final StringPath currentPassword = createString("currentPassword");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath newPassword = createString("newPassword");

    public final QRequest request;

    public QPasswordResetRequest(String variable) {
        this(PasswordResetRequest.class, forVariable(variable), INITS);
    }

    public QPasswordResetRequest(Path<? extends PasswordResetRequest> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPasswordResetRequest(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPasswordResetRequest(PathMetadata metadata, PathInits inits) {
        this(PasswordResetRequest.class, metadata, inits);
    }

    public QPasswordResetRequest(Class<? extends PasswordResetRequest> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.request = inits.isInitialized("request") ? new QRequest(forProperty("request"), inits.get("request")) : null;
    }

}

