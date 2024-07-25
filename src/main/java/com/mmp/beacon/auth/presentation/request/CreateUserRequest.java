package com.mmp.beacon.security.presentation.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequest {

    @NotBlank(message = "Please enter your user ID.")
    private String userId;

    @Size(min = 5, max = 255, message = "Password must be between 5 and 255 characters.")
    @NotBlank(message = "Please enter a password.")
    private String password;

    @Size(max = 255, message = "Phone number should not exceed 255 characters.")
    @NotBlank(message = "Please enter a phone number.")
    private String phone;

    @NotBlank(message = "Please enter your name.")
    private String name;

    @Email(message = "Please enter a valid email address.")
    @NotBlank(message = "Please enter a valid email address.")
    private String email;

    @NotBlank(message = "Please enter your position.")
    private String position;

    @NotBlank(message = "Please enter your company.")
    private String company;

    @NotBlank(message = "Please enter the user role.")
    private String role;
}

