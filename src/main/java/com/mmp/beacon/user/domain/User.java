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

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "position", nullable = false)
    private String position;

    public User(String userId, String password, String name, String phone, String email, String sex, String position, Company company) {
        super(userId, password, UserRole.USER, email, sex, position);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.company = company;
        this.position = position;
    }
    public void setRole(UserRole role) {
        this.role = role;
    }

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;


}
