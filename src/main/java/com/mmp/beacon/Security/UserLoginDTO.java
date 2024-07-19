package com.mmp.beacon.Security;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginDTO {
    @NotBlank(message = "Please enter a valid user ID or email address.")
    private String userId;

    @NotBlank(message = "Please enter a password.")
    private String password;
}
