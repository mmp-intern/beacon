package com.mmp.beacon.beacon.domain.presentation;

import lombok.Data;

@Data
public class BeaconResponse {

    private Long id;
    private String macAddr;
    private Long userId;
    private String userName;  // 필요에 따라 사용자 이름도 반환
}
