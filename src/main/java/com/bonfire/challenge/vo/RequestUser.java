package com.bonfire.challenge.vo;

import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.data.annotation.Id;

@Data
public class RequestUser {
    @NotNull
    @Id
    private String username;

    @NotNull
    private String password;

    @NotNull
    private String comCode;

}
