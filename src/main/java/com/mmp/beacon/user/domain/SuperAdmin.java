package com.mmp.beacon.user.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "super_admin")
public class SuperAdmin extends AbstractUser {

    public SuperAdmin(String userId, String password, UserRole role) {
        super(userId, password, role);
    }
}