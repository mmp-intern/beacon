package com.mmp.beacon.Security.query.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserProfileResponse {
    private String userId;
    private String email;
    private String position;
    private String name;
    private String phone;
    private String company;
    private String role;
}
