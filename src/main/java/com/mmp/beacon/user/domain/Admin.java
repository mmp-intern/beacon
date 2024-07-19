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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_no", nullable = false)
    private Company company;

    public Admin(String userId, String password, String email, String sex, String position, Company company) {
        super(userId, password, UserRole.ADMIN, email, sex, position);
        this.company = company;
    }
}
