package com.mmp.beacon.user.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSuperAdmin is a Querydsl query type for SuperAdmin
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSuperAdmin extends EntityPathBase<SuperAdmin> {

    private static final long serialVersionUID = 1453567450L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSuperAdmin superAdmin = new QSuperAdmin("superAdmin");

    public final QAbstractUser _super;

    // inherited
    public final com.mmp.beacon.company.domain.QCompany company;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createAt;

    //inherited
    public final NumberPath<Long> id;

    //inherited
    public final StringPath password;

    //inherited
    public final EnumPath<UserRole> role;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updateAt;

    //inherited
    public final StringPath userId;

    public QSuperAdmin(String variable) {
        this(SuperAdmin.class, forVariable(variable), INITS);
    }

    public QSuperAdmin(Path<? extends SuperAdmin> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSuperAdmin(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSuperAdmin(PathMetadata metadata, PathInits inits) {
        this(SuperAdmin.class, metadata, inits);
    }

    public QSuperAdmin(Class<? extends SuperAdmin> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QAbstractUser(type, metadata, inits);
        this.company = _super.company;
        this.createAt = _super.createAt;
        this.id = _super.id;
        this.password = _super.password;
        this.role = _super.role;
        this.updateAt = _super.updateAt;
        this.userId = _super.userId;
    }

}

