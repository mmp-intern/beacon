package com.mmp.beacon.Security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserProfileDTO {
    private String userId;
    private String email;
    private String sex;
    private String position;
    private String name;
    private String phone;
    private String company;
}
