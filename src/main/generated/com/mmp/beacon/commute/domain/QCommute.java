package com.mmp.beacon.commute.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCommute is a Querydsl query type for Commute
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCommute extends EntityPathBase<Commute> {

    private static final long serialVersionUID = -2019575229L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCommute commute = new QCommute("commute");

    public final com.mmp.beacon.global.domain.QBaseEntity _super = new com.mmp.beacon.global.domain.QBaseEntity(this);

    public final EnumPath<AttendanceStatus> attendanceStatus = createEnum("attendanceStatus", AttendanceStatus.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createAt = _super.createAt;

    public final DatePath<java.time.LocalDate> date = createDate("date", java.time.LocalDate.class);

    public final TimePath<java.time.LocalTime> endedAt = createTime("endedAt", java.time.LocalTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final TimePath<java.time.LocalTime> startedAt = createTime("startedAt", java.time.LocalTime.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updateAt = _super.updateAt;

    public final com.mmp.beacon.user.domain.QUser user;

    public final EnumPath<WorkStatus> workStatus = createEnum("workStatus", WorkStatus.class);

    public QCommute(String variable) {
        this(Commute.class, forVariable(variable), INITS);
    }

    public QCommute(Path<? extends Commute> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCommute(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCommute(PathMetadata metadata, PathInits inits) {
        this(Commute.class, metadata, inits);
    }

    public QCommute(Class<? extends Commute> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.mmp.beacon.user.domain.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

