package com.mmp.beacon.security.presentation.request;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;


@Getter
public class AdminCreateRequest {

    @NotBlank(message = "사용자 ID를 입력해 주세요.")
    private String userId;

    @Size(min = 5, max = 255, message = "비밀번호는 5자에서 255자 사이여야 합니다.")
    @NotBlank(message = "비밀번호를 입력해 주세요.")
    private String password;

    @NotBlank(message = "회사를 입력해 주세요.")
    private String company;

}
