package com.mmp.beacon.gateway.domain;

import com.mmp.beacon.company.domain.Company;
import com.mmp.beacon.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "gateway")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Gateway extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gateway_no")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_no", nullable = false)
    private Company company;

    @Column(name = "gateway_mac_addr", length = 50, nullable = false)
    private String macAddr;
}
