package com.mmp.beacon.user.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAbstractUser is a Querydsl query type for AbstractUser
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAbstractUser extends EntityPathBase<AbstractUser> {

    private static final long serialVersionUID = -851195181L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAbstractUser abstractUser = new QAbstractUser("abstractUser");

    public final com.mmp.beacon.global.domain.QBaseEntity _super = new com.mmp.beacon.global.domain.QBaseEntity(this);

    public final com.mmp.beacon.company.domain.QCompany company;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createAt = _super.createAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath password = createString("password");

    public final EnumPath<UserRole> role = createEnum("role", UserRole.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updateAt = _super.updateAt;

    public final StringPath userId = createString("userId");

    public QAbstractUser(String variable) {
        this(AbstractUser.class, forVariable(variable), INITS);
    }

    public QAbstractUser(Path<? extends AbstractUser> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAbstractUser(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAbstractUser(PathMetadata metadata, PathInits inits) {
        this(AbstractUser.class, metadata, inits);
    }

    public QAbstractUser(Class<? extends AbstractUser> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.company = inits.isInitialized("company") ? new com.mmp.beacon.company.domain.QCompany(forProperty("company")) : null;
    }

}

