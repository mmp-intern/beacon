package com.mmp.beacon.beacon.domain;

import com.mmp.beacon.global.domain.BaseEntity;
import com.mmp.beacon.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "beacon")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Beacon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "beacon_no")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", nullable = true)
    private User user;

    @Column(name = "beacon_mac_addr", length = 50, unique = true, nullable = false)
    private String macAddr;

    public Beacon(String macAddr) {
        this.macAddr = macAddr;
    }

    public void updateMacAddr(String macAddr) {
        this.macAddr = macAddr;
    }

}
