package com.mmp.beacon.security.presentation.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import java.util.List;

@Getter
public class UpdateUserRequest {

    @Size(max = 255, message = "이름은 255자를 넘을 수 없습니다.")
    private String name;

    @Email(message = "유효한 이메일 주소를 입력해 주세요.")
    @NotBlank(message = "이메일 주소를 입력해 주세요.")
    private String email;

    @Size(max = 255, message = "전화번호는 255자를 넘을 수 없습니다.")
    private String phone;

    @Size(max = 255, message = "직책은 255자를 넘을 수 없습니다.")
    private String position;

    @Size(min = 5, max = 255, message = "비밀번호는 5자에서 255자 사이여야 합니다.")
    private String password;

    private List<String> macAddr;
}
