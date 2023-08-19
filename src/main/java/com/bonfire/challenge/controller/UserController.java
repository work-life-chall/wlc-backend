package com.bonfire.challenge.controller;

import com.bonfire.challenge.dto.UserDto;
import com.bonfire.challenge.entity.UserEntity;
import com.bonfire.challenge.service.UserService;
import com.bonfire.challenge.vo.RequestUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    @GetMapping("/home")
    public String home() {
        return "login 한 유저만 보임";
    }
    @PostMapping("/user")
    public ResponseEntity<UserEntity> createUser(@RequestBody RequestUser requestUser) {
        // requestUser To userDto
        userService.createUser(new ObjectMapper().convertValue(requestUser, UserDto.class));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
