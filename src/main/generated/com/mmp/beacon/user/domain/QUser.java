package com.mmp.beacon.user.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = -1362858607L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUser user = new QUser("user");

    public final QAbstractUser _super;

    public final com.mmp.beacon.company.domain.QCompany company;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createAt;

    public final StringPath email = createString("email");

    //inherited
    public final NumberPath<Long> id;

    public final StringPath name = createString("name");

    //inherited
    public final StringPath password;

    public final StringPath phone = createString("phone");

    public final StringPath position = createString("position");

    //inherited
    public final EnumPath<UserRole> role;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updateAt;

    //inherited
    public final StringPath userId;

    public QUser(String variable) {
        this(User.class, forVariable(variable), INITS);
    }

    public QUser(Path<? extends User> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUser(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUser(PathMetadata metadata, PathInits inits) {
        this(User.class, metadata, inits);
    }

    public QUser(Class<? extends User> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QAbstractUser(type, metadata, inits);
        this.company = inits.isInitialized("company") ? new com.mmp.beacon.company.domain.QCompany(forProperty("company")) : null;
        this.createAt = _super.createAt;
        this.id = _super.id;
        this.password = _super.password;
        this.role = _super.role;
        this.updateAt = _super.updateAt;
        this.userId = _super.userId;
    }

}

