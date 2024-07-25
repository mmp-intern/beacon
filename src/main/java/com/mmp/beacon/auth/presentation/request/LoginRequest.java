package com.mmp.beacon.auth.presentation.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    @NotBlank(message = "Please enter a valid user ID or email address.")
    private String userId;

    @NotBlank(message = "Please enter a password.")
    private String password;
}
