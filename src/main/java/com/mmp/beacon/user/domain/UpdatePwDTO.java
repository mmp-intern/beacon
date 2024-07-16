package com.mmp.beacon.user.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePwDTO {
    private String password;
    private String confirmPw;
}
