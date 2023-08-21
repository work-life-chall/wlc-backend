package com.bonfire.challenge.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class RequestLogin {
    @NotBlank
    @Id
    private String username;

    @NotBlank
    private String password;
}
