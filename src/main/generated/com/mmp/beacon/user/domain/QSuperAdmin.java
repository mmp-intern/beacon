package com.mmp.beacon.user.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSuperAdmin is a Querydsl query type for SuperAdmin
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSuperAdmin extends EntityPathBase<SuperAdmin> {

    private static final long serialVersionUID = 1453567450L;

    public static final QSuperAdmin superAdmin = new QSuperAdmin("superAdmin");

    public final QAbstractUser _super = new QAbstractUser(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createAt = _super.createAt;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final StringPath password = _super.password;

    //inherited
    public final EnumPath<UserRole> role = _super.role;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updateAt = _super.updateAt;

    //inherited
    public final StringPath userId = _super.userId;

    public QSuperAdmin(String variable) {
        super(SuperAdmin.class, forVariable(variable));
    }

    public QSuperAdmin(Path<? extends SuperAdmin> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSuperAdmin(PathMetadata metadata) {
        super(SuperAdmin.class, metadata);
    }

}

