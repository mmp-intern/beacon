package com.mmp.beacon.user.domain;

import com.mmp.beacon.company.domain.Company;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("USER")
public class User extends AbstractUser {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_no", nullable = false)
    private Company company;

    @Column(name = "user_name", nullable = false)
    private String name;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "position", nullable = false)
    private String position;

    public User(String userId, String password, String name, String phone, String email, String position, Company company) {
        super(userId, password, UserRole.USER);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.position = position;
        this.company = company;
    }

    public void setRole(UserRole role) {
        super.setRole(role);
    }
}
