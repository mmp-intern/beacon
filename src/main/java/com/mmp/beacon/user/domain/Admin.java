package com.mmp.beacon.user.domain;

import com.mmp.beacon.company.domain.Company;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("ADMIN")
public class Admin extends AbstractUser {


    @Column(name = "user_name", nullable = false)
    private String name;

    public Admin(String userId, String password, UserRole role, Company company, String name) {
        super(userId, password, role, company);
        this.name = name;


    }
}