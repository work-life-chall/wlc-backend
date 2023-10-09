package com.bonfire.challenge.controller;

import com.bonfire.challenge.dto.UserDto;
import com.bonfire.challenge.entity.UserEntity;
import com.bonfire.challenge.service.UserService;
import com.bonfire.challenge.vo.RequestUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/admin")
public class AdminController {
    private final UserService userService;
    @Value("${spring.servlet.multipart.location}")
    private String location;

    /**
     * 유저 개별 등록
     * @param requestUser
     * @param errors
     * @return
     */
    @PostMapping("/user")
    public ResponseEntity<UserEntity> createUser(@Valid @RequestBody RequestUser requestUser, Errors errors) {
        // 회원 등록 시 유효성 검사 실패
        if (errors.hasErrors() && errors.hasFieldErrors("username")) {
            log.error("[createUser] error msg: {}", errors.getAllErrors().get(0).getDefaultMessage());
            throw new ValidationException(errors.toString());
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Object principal = authentication.getPrincipal();

        // requestUser To userDto
        userService.createUser(new ObjectMapper().convertValue(requestUser, UserDto.class));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * 유저 대량 등록 (엑셀파일)
     * @param multipartFile
     * @return
     * @throws IOException
     */
    @PostMapping("/users")
    public ResponseEntity<HashMap<String, Object>> createUsers(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        // 엑셀 파일 로컬에 저장
        String filePath = location + "/" + UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();
        File localSaveFile = new File(filePath);
        multipartFile.transferTo(localSaveFile);

        // 엑셀 파일의 유저 정보 추출
        List<UserDto> createUsers = new ArrayList<>();
        String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());

        if (!extension.equals("xlsx") && !extension.equals("xls")) {
            throw new IOException("엑셀 파일만 업로드 해주세요.");
        }

        Workbook workbook = null;
        FileInputStream fis = new FileInputStream(new File(filePath));

        if (extension.equals("xlsx")) {
            workbook = new XSSFWorkbook(fis);
        } else {
            workbook = new HSSFWorkbook(fis);
        }

        Sheet worksheet = workbook.getSheetAt(0);
        for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {
            Row row = worksheet.getRow(i);
            UserDto user = new UserDto();
            user.setUsername(String.valueOf(row.getCell(0)));
            user.setPassword(String.valueOf(row.getCell(1)));
            user.setComCode(String.valueOf(row.getCell(2)));
            createUsers.add(user);
        }

        // 유저 정보 DB에 저장
        userService.createMultiUser(createUsers);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PatchMapping("/user")
    public ResponseEntity<UserEntity> updateUser(@Valid @RequestBody RequestUser requestUser, Errors errors) {
        // 회원 수정 시 유효성 검사 실패
        if (errors.hasErrors() && errors.hasFieldErrors("password")) {
            log.error("[createUser] error msg: {}", errors.getAllErrors().get(1).getDefaultMessage());
            throw new ValidationException(errors.toString());
        }

        userService.updateUser(new ObjectMapper().convertValue(requestUser, UserDto.class), true);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/user/{username}")
    public ResponseEntity<UserEntity> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/{username}/password/reset")
    public ResponseEntity<String> resetPassword(@PathVariable String username) {
        userService.resetPassword(username);
        userService.resetFailureCntAndLock(username);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/{username}/locked/reset")
    public ResponseEntity<String> resetLocked(@PathVariable String username) {
        userService.resetFailureCntAndLock(username);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
