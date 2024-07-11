package com.mmp.beacon.beacon.domain;

import com.mmp.beacon.global.domain.BaseEntity;
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

    @Column(name = "beacon_mac_addr", length = 50, unique = true, nullable = false)
    private String macAddr;
}
