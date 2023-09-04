package com.bonfire.challenge.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserDto {
    private String id;
    private String username;
    private String password;
    private String comCode;
    private int role;
    private boolean isDisabled;
    private int failureCnt;
    private boolean locked;
}
