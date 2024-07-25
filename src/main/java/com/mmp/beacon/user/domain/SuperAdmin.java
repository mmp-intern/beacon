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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_no", nullable = false)
    private Company company;

    public SuperAdmin(String userId, String password, Company company) {
        super(userId, password, UserRole.SUPER_ADMIN);
        this.company = company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}