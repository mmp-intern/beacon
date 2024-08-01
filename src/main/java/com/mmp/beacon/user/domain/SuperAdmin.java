package com.mmp.beacon.user.domain;

import com.mmp.beacon.company.domain.Company;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("SUPER_ADMIN")
public class SuperAdmin extends AbstractUser {



    public SuperAdmin(String userId, String password, UserRole role, Company company) {
        super(userId, password, role, company);
    }
}