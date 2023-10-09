package com.bonfire.challenge.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown =true)
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
