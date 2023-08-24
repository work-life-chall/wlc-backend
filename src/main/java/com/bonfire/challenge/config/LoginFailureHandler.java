package com.bonfire.challenge.config;

import com.bonfire.challenge.dto.UserDto;
import com.bonfire.challenge.entity.UserEntity;
import com.bonfire.challenge.repository.UserRepository;
import com.bonfire.challenge.service.UserService;
import com.bonfire.challenge.vo.AuthenticationFailureType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoginFailureHandler implements org.springframework.security.web.authentication.AuthenticationFailureHandler {

    private final UserService userService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String error = exception.getMessage();

        log.info("[onAuthenticationFailure] login failure username: {}, password: {}, error msg: {}", username, password, error);

        if(exception instanceof BadCredentialsException) {
            loginFailureCount(username);
        }

        exception.printStackTrace();
        sendErrorToResponse(response, exception);

        // 로그인 화면으로 이동 ??

        // spring example
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        response.setStatus(HttpStatus.UNAUTHORIZED.value());
//        Map<String, Object> data = new HashMap<>();
//        data.put(
//                "timestamp",
//                Calendar.getInstance().getTime());
//        data.put(
//                "exception",
//                exception.getMessage());
//
//        response.getOutputStream()
//                .println(objectMapper.writeValueAsString(data));
    }

    /**
     * 비밀번호 5회 틀리면 계정 lock
     * @param username
     */
    private void loginFailureCount(String username) {
        UserDto loginUser = userService.getUserDetailsByUsername(username);
        int currFailureCnt = loginUser.getFailureCnt() + 1;
        if (!loginUser.isLocked()) {
            //failureCnt++
            loginUser.setFailureCnt(currFailureCnt);

            if (currFailureCnt >= 5) {
                loginUser.setLocked(true);      // 로그인 5회 실패 시 계정 잠금
            }
            userService.updateUser(loginUser);  // db에 저장
        }
    }

    /**
     * Exception 상태코드와 type을 response에 담아서 보냄
     * @param response
     * @param exception
     */
    private void sendErrorToResponse(HttpServletResponse response, AuthenticationException exception) {
        try {
            String message = getExceptionMessage(exception);
            response.sendError(401, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * get exception type
     * @param exception
     * @return
     */
    private String getExceptionMessage(AuthenticationException exception) {
        AuthenticationFailureType authenticationFailureType = AuthenticationFailureType.findOf(exception.getClass().getSimpleName());
        return authenticationFailureType.getType();
    }
}
