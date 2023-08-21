package com.bonfire.challenge.vo;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum AuthenticationFailureType {
    BadCredentialsException("비밀번호불일치", "비밀번호불일치"),
    UsernameNotFoundException("계정없음", "계정없음"),
    AccountExpiredException("계정만료", "계정만료"),
    CredentialsExpiredException("비밀번호만료", "비밀번호만료"),
    DisabledException("계정비활성화", "계정비활성화"),
    LockedException("계정잠김", "계정잠김"),
    NoneException("알수없는 에러", "알 수 없는 에러 입니다.")
    ;
    private String desc;
    private String type;

    AuthenticationFailureType(String desc, String type) {
        this.desc = desc;
        this.type = type;
    }
    public String getType() {
        return this.type;
    }
    private static final Map<String, AuthenticationFailureType> descriptions =
            Collections.unmodifiableMap(Stream.of(values())
                    .collect(Collectors.toMap(AuthenticationFailureType::name, Function.identity())));
    public static AuthenticationFailureType findOf(String findValue) {
        return Optional.ofNullable(descriptions.get(findValue)).orElse(NoneException);
    }
}
