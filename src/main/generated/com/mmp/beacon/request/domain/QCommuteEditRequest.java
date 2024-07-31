package com.mmp.beacon.request.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCommuteEditRequest is a Querydsl query type for CommuteEditRequest
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCommuteEditRequest extends EntityPathBase<CommuteEditRequest> {

    private static final long serialVersionUID = 911655447L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCommuteEditRequest commuteEditRequest = new QCommuteEditRequest("commuteEditRequest");

    public final EnumPath<com.mmp.beacon.commute.domain.AttendanceStatus> attendanceStatus = createEnum("attendanceStatus", com.mmp.beacon.commute.domain.AttendanceStatus.class);

    public final com.mmp.beacon.commute.domain.QCommute commute;

    public final DateTimePath<java.time.LocalDateTime> endedAt = createDateTime("endedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QRequest request;

    public final DateTimePath<java.time.LocalDateTime> startedAt = createDateTime("startedAt", java.time.LocalDateTime.class);

    public QCommuteEditRequest(String variable) {
        this(CommuteEditRequest.class, forVariable(variable), INITS);
    }

    public QCommuteEditRequest(Path<? extends CommuteEditRequest> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCommuteEditRequest(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCommuteEditRequest(PathMetadata metadata, PathInits inits) {
        this(CommuteEditRequest.class, metadata, inits);
    }

    public QCommuteEditRequest(Class<? extends CommuteEditRequest> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.commute = inits.isInitialized("commute") ? new com.mmp.beacon.commute.domain.QCommute(forProperty("commute"), inits.get("commute")) : null;
        this.request = inits.isInitialized("request") ? new QRequest(forProperty("request"), inits.get("request")) : null;
    }

}

