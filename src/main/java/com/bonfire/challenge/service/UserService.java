package com.bonfire.challenge.service;

import com.bonfire.challenge.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    void createUser(UserDto userDto);
    int createMultiUser(List<UserDto> userDtos);
    UserDto getUserDetailsByUsername(String username);
    List<UserDto> getUsers();
    void updateUser(UserDto userDto);
    void deleteUser(String username);
    void resetFailureCntAndLock(String username);
}
