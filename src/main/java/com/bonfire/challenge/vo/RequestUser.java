package com.bonfire.challenge.vo;

import com.bonfire.challenge.validation.Username;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class RequestUser {

    @Id
    @NotBlank
    @Username(message = "이미 존재하는 ID 입니다.")
    private String username;

    @NotBlank
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
    private String password;

    @NotBlank
    private String comCode;

    private int role;

    private boolean isDisabled;
}
