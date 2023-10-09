package com.bonfire.challenge.vo;

import lombok.Data;

@Data
public class ResponseUser {
    private String username;
    private String password;
    private String comCode;
    private Role role;
}
