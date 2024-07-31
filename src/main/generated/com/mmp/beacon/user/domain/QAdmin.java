package com.mmp.beacon.user.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAdmin is a Querydsl query type for Admin
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAdmin extends EntityPathBase<Admin> {

    private static final long serialVersionUID = 682146377L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAdmin admin = new QAdmin("admin");

    public final QAbstractUser _super;

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

    public QAdmin(String variable) {
        this(Admin.class, forVariable(variable), INITS);
    }

    public QAdmin(Path<? extends Admin> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAdmin(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAdmin(PathMetadata metadata, PathInits inits) {
        this(Admin.class, metadata, inits);
    }

    public QAdmin(Class<? extends Admin> type, PathMetadata metadata, PathInits inits) {
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

