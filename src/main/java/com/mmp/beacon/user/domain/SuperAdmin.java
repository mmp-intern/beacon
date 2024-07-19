package com.mmp.beacon.user.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("SUPER_ADMIN")
public class SuperAdmin extends AbstractUser {

    public SuperAdmin(String userId, String password, String email, String sex, String position) {
        super(userId, password, UserRole.SUPER_ADMIN, email, sex, position);
    }
}
