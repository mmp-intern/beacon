package com.mmp.beacon.beacon.query.response;

import lombok.Data;

@Data
public class BeaconResponse {

    private Long id;
    private String macAddr;
    private Long user_Id; //user의 고유 pk
    private String userId;
    private String userName;

}
