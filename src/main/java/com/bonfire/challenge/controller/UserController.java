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
import org.springframework.web.multipart.MultipartFile;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
    public ResponseEntity<UserEntity> createUsers(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        // 엑셀 파일 로컬에 저장
        String rootPath = FileSystemView.getFileSystemView().getHomeDirectory().toString();
        String basePath = rootPath + "/" + {{회사코드}};        // 회사 admin 계정의 회사코드 세팅??
        String filePath = basePath + "/" + multipartFile.getOriginalFilename();
        File localSaveFile = new File(filePath);
        multipartFile.transferTo(localSaveFile);

        // 엑셀 파일의 유저 정보 추출
        List<UserDto> createUsers = new ArrayList<>();
        String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());

        if (!extension.equals("xlsx") && !extension.equals("xls")) {
            throw new IOException("엑셀 파일만 업로드 해주세요.");
        }

        Workbook workbook = null;

        if (extension.equals("xlsx")) {
            workbook = new XSSFWorkbook(file.getInputStream());
        } else if (extension.equals("xls")) {
            workbook = new HSSFWorkbook(file.getInputStream());
        }

        Sheet worksheet = workbook.getSheetAt(0);

        for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {

            Row row = worksheet.getRow(i);

            UserDto user = new UserDto();
            user.setUsername(row.getCell(0).getNumericCellValue());
            user.setComCode(row.getCell(0).getNumericCellValue());


            createUsers.add(user);
        }

        // 유저 정보 DB에 저장
        userService.createMultiUser(createUsers);
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
