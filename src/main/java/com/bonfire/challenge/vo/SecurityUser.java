package com.bonfire.challenge.vo;

import com.bonfire.challenge.entity.UserEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@Slf4j
@Getter
@Setter
public class SecurityUser extends User {

    private UserEntity userEntity;

    public SecurityUser(UserEntity userEntity) {
        super(userEntity.getUsername(), userEntity.getPassword(), AuthorityUtils.createAuthorityList(Objects.requireNonNull(Role.findBy(userEntity.getRole())).toString()));

        log.info("SecurityUser user.username = {}", userEntity.getUsername());
        log.info("SecurityUser user.password = {}", userEntity.getPassword());
        log.info("SecurityUser user.role = {}", Role.findBy(userEntity.getRole()));

        this.userEntity = userEntity;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(()->{return String.valueOf(Role.findBy(userEntity.getRole()));}); // ROLE_USER
        return authorities;
    }

}