package com.mmp.beacon.user.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAbstractUser is a Querydsl query type for AbstractUser
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAbstractUser extends EntityPathBase<AbstractUser> {

    private static final long serialVersionUID = -851195181L;

    public static final QAbstractUser abstractUser = new QAbstractUser("abstractUser");

    public final com.mmp.beacon.global.domain.QBaseEntity _super = new com.mmp.beacon.global.domain.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createAt = _super.createAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath password = createString("password");

    public final EnumPath<UserRole> role = createEnum("role", UserRole.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updateAt = _super.updateAt;

    public final StringPath userId = createString("userId");

    public QAbstractUser(String variable) {
        super(AbstractUser.class, forVariable(variable));
    }

    public QAbstractUser(Path<? extends AbstractUser> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAbstractUser(PathMetadata metadata) {
        super(AbstractUser.class, metadata);
    }

}

