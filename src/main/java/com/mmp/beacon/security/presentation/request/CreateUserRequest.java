package com.mmp.beacon.security.presentation.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CreateUserRequest {

    @NotBlank(message = "사용자 ID를 입력해 주세요.")
    private String userId;

    @Size(min = 5, max = 255, message = "비밀번호는 5자에서 255자 사이여야 합니다.")
    @NotBlank(message = "비밀번호를 입력해 주세요.")
    private String password;

    @Size(max = 255, message = "전화번호는 255자를 넘을 수 없습니다.")
    @NotBlank(message = "전화번호를 입력해 주세요.")
    private String phone;

    @NotBlank(message = "이름을 입력해 주세요.")
    private String name;

    @Email(message = "유효한 이메일 주소를 입력해 주세요.")
    @NotBlank(message = "이메일 주소를 입력해 주세요.")
    private String email;

    @NotBlank(message = "직책을 입력해 주세요.")
    private String position;

    @NotBlank(message = "회사를 입력해 주세요.")
    private String company;

    @NotBlank(message = "사용자 역할을 입력해 주세요.")
    private String role;
}
