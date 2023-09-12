package com.bonfire.challenge.controller;

import com.bonfire.challenge.dto.UserDto;
import com.bonfire.challenge.service.UserService;
import com.bonfire.challenge.vo.ResponseUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@Slf4j
public class UserController {
    private final UserService userService;

    // 로그인 유저 테스트용
    @GetMapping("/home")
    public String home() {
        return "login 한 유저만 보임";
    }

    @GetMapping("/users")
    public List<ResponseUser> getUsers() {
        List<UserDto> users = userService.getUsers();
        return users.stream()
                .map(u -> new ObjectMapper().convertValue(u, ResponseUser.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/user/{username}")
    public ResponseUser getUser(@PathVariable String username) {
        UserDto userDto = userService.getUserDetailsByUsername(username);
        return new ObjectMapper().convertValue(userDto, ResponseUser.class);
    }

}
