package com.bonfire.challenge.config;

import com.bonfire.challenge.dto.UserDto;
import com.bonfire.challenge.service.UserService;
import com.bonfire.challenge.vo.AuthenticationFailureType;
import com.bonfire.challenge.vo.RequestLogin;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final UserService userService;
    private final Environment env;

    public AuthenticationFilter(AuthenticationManager authenticationManager, UserService userService, Environment env) {
        super.setAuthenticationManager(authenticationManager);
        this.userService = userService;
        this.env = env;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        if ( !request.getMethod().equals("POST") ) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        try {
            RequestLogin creds = new ObjectMapper().readValue(request.getInputStream(), RequestLogin.class);
            log.info("[attemptAuthentication] creds: {}", creds.toString());
            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getUsername(),
                            creds.getPassword(),
                            new ArrayList<>()
                    )
            );
        } catch (IOException e) {
            log.error("[attemptAuthentication] msg: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication (HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {

        // 로그인 성공 시
        String username = ((User)authResult.getPrincipal()).getUsername();
        userService.resetFailureCntAndLock(username);       // 로그인 시도 횟수, 계정 잠금 초기화
        UserDto userDetails = userService.getUserDetailsByUsername(username);
        log.info("[successfulAuthentication] login success - username : {}, role : {} " , userDetails.getUsername(), userDetails.getRole());

        try {
            Key key = Keys.hmacShaKeyFor(Objects.requireNonNull(env.getProperty("token.secret")).getBytes(StandardCharsets.UTF_8));

            String token = Jwts.builder()
                    .setSubject(userDetails.getId())
                    .claim("auth", userDetails.getRole())
                    .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(Objects.requireNonNull(env.getProperty("token.expiration_time")))))
                    .signWith(key, SignatureAlgorithm.HS512)
                    .compact();

            log.info("[successfulAuthentication] token create success");

            response.addHeader("token", token);
            response.addHeader("userId", userDetails.getId());
        } catch (Exception e) {
            log.error("[successfulAuthentication] token create fail - cause : {} , msg : {}", e.getCause(), e.getMessage());
        }

    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String error = failed.getMessage();

        log.info("[unsuccessfulAuthentication] login failure - username: {}, error msg: {}", username, error);

        if(failed instanceof BadCredentialsException) {
            loginFailureCount(username);
        }

        failed.printStackTrace();
        sendErrorToResponse(response, failed);
    }

    /**
     * 비밀번호 5회 틀리면 계정 lock
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
     */
    private String getExceptionMessage(AuthenticationException exception) {
        AuthenticationFailureType authenticationFailureType = AuthenticationFailureType.findOf(exception.getClass().getSimpleName());
        return authenticationFailureType.getType();
    }
}
