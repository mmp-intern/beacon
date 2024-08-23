package com.mmp.beacon.user.domain;

import com.mmp.beacon.company.domain.Company;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.mmp.beacon.beacon.domain.Beacon;


@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "employee")
public class User extends AbstractUser {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_no", nullable = false)
    private Company company;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "position")
    private String position;

    public User(String userId, String password, UserRole role, Company company, String name, String email, String phone, String position) {
        super(userId, password, role);
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.position = position;
        this.company = company;
    }

    public void updateProfile(String name, String email, String phone, String position, Beacon beacon) {
        if (name != null) this.name = name;
        if (email != null) this.email = email;
        if (phone != null) this.phone = phone;
        if (position != null) this.position = position;
        if (beacon != null) beacon.assignUser((User) this);
    }
}
