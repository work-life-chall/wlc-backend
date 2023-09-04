package com.bonfire.challenge.controller;

import com.bonfire.challenge.dto.UserDto;
import com.bonfire.challenge.entity.UserEntity;
import com.bonfire.challenge.service.UserService;
import com.bonfire.challenge.vo.RequestUser;
import com.bonfire.challenge.vo.ResponseUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
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

    // 개별 유저 등록
    @PostMapping("/user")
    public ResponseEntity<UserEntity> createUser(@Valid @RequestBody RequestUser requestUser, Errors errors) {
        // 회원 등록 시 유효성 검사 실패
        if (errors.hasErrors() && errors.hasFieldErrors("username")) {
            log.error("[createUser] error msg: {}", errors.getAllErrors().get(0).getDefaultMessage());
            throw new ValidationException(errors.toString());
        }

        // requestUser To userDto
        userService.createUser(new ObjectMapper().convertValue(requestUser, UserDto.class));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // 대량 유저 등록
    @PostMapping("/users")
    public ResponseEntity<UserEntity> createUsers() {
        // 엑셀 업로드한 대량 유저 등록
        return new ResponseEntity<>(HttpStatus.CREATED);
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

    @PatchMapping("/user")
    public ResponseEntity<UserEntity> updateUser(@Valid @RequestBody RequestUser requestUser, Errors errors) {
        // 회원 수정 시 유효성 검사 실패
        if (errors.hasErrors() && errors.hasFieldErrors("password")) {
            log.error("[createUser] error msg: {}", errors.getAllErrors().get(1).getDefaultMessage());
            throw new ValidationException(errors.toString());
        }

        userService.updateUser(new ObjectMapper().convertValue(requestUser, UserDto.class));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/user/{username}")
    public ResponseEntity<UserEntity> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/user/{username}/reset")
    public ResponseEntity<String> resetPassword(@PathVariable String username) {
        userService.resetPassword(username);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
