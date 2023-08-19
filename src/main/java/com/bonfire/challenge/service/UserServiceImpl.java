package com.bonfire.challenge.service;

import com.bonfire.challenge.dto.UserDto;
import com.bonfire.challenge.entity.UserEntity;
import com.bonfire.challenge.repository.UserRepository;
import com.bonfire.challenge.vo.RequestUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("[loadUserByUsername] username: {}", username);
        Optional<UserEntity> result = Optional.ofNullable(userRepository.findByUsername(username));
        UserEntity findUser = result.get();
        log.debug("[loadUserByUsername] findUser: {}", findUser);
        return new org.springframework.security.core.userdetails.User(findUser.getUsername(), findUser.getPassword(),
                true, true, true, true, new ArrayList<>());
    }

    @Override
    public void createUser(UserDto userDto) {
        userDto.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        userDto.setRole(1);
        UserEntity userEntity = new ObjectMapper().convertValue(userDto, UserEntity.class);
        userRepository.save(userEntity);
    }

    @Override
    public int createMultiUser(List<UserDto> userDtos) {
        return 0;
    }

    @Override
    public UserDto getUserDetailsByUsername(String username) {
        UserEntity userEntity = userRepository.findByUsername(username);
        if (userEntity == null)
            throw new UsernameNotFoundException(username);
        return new ObjectMapper().convertValue(userEntity, UserDto.class);
    }

}
