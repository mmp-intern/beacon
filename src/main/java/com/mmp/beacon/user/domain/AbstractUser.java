package com.mmp.beacon.user.domain;

import com.mmp.beacon.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type")
public abstract class AbstractUser extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_no")
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true, length = 50)
    private String userId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "sex")
    private String sex;

    @Column(name = "position")
    private String position;

    // 생성자
    protected AbstractUser(String userId, String password, UserRole role, String email, String sex, String position) {
        this.userId = userId;
        this.password = password;
        this.role = role;
        this.email = email;
        this.sex = sex;
        this.position = position;
    }
}
