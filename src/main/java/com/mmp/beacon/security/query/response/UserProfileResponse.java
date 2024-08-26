package com.mmp.beacon.security.query.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class UserProfileResponse {

    private Long Id;
    private String userId;
    private String email;
    private String position;
    private String name;
    private String phone;
    private Long companyId;
    private String company;
    private String role;
    private List<Long> beaconIds;
    private List<String> macAddr;

}
