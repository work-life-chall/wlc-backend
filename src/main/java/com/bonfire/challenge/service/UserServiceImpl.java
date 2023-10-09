package com.bonfire.challenge.service;

import com.bonfire.challenge.dto.UserDto;
import com.bonfire.challenge.entity.UserEntity;
import com.bonfire.challenge.repository.UserRepository;
import com.bonfire.challenge.vo.SecurityUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        UserEntity userEntity = result.get();
        SecurityUser findUser = new SecurityUser(userEntity);
        log.debug("[loadUserByUsername] findUser: {}", findUser);
        return findUser;
    }

    @Override
    public void createUser(UserDto userDto) {
        userDto.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        userDto.setRole(1);
        UserEntity userEntity = new ObjectMapper().convertValue(userDto, UserEntity.class);
        userRepository.save(userEntity);
    }

    @Override
    public void createMultiUser(List<UserDto> userDtos) {
        userDtos.forEach(this::createUser);
    }

    @Override
    public UserDto getUserDetailsByUsername(String username) {
        UserEntity userEntity = userRepository.findByUsername(username);
        if (userEntity == null)
            throw new UsernameNotFoundException(username);
        return new ObjectMapper().convertValue(userEntity, UserDto.class);
    }

    @Override
    public List<UserDto> getUsers() {
        List<UserEntity> users = userRepository.findAll();
        // 비활성화되지 않은 유저리스트
        return users.stream()
                .filter(u -> !u.isDisabled())
                .map(u -> new ObjectMapper().convertValue(u, UserDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public void updateUser(UserDto userDto, boolean isPatched) {
        UserEntity userEntity = userRepository.findByUsername(userDto.getUsername());

        if (userEntity == null)
            throw new UsernameNotFoundException("해당하는 username DB에 존재하지 않습니다.");

        if (isPatched) {
            userEntity.setRole(userDto.getRole() == 0 ? userEntity.getRole() : userDto.getRole());
            userEntity.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
            userEntity.setLocked(userDto.isLocked());
            userEntity.setComCode(userDto.getComCode());
            userEntity.setDisabled(userDto.isDisabled());
        }
        userEntity.setFailureCnt(userDto.getFailureCnt());
        userEntity.setLocked(userDto.isLocked());
        // 수정된 사용자 정보를 저장
        userRepository.save(userEntity);
        log.info("[updateUser] update user info : {}" , userEntity.toString());
    }

    @Override
    public void deleteUser(String username) {
        UserEntity deleteUser = userRepository.findByUsername(username);
        userRepository.delete(deleteUser);
        log.info("[deleteUser] delete user info : {}" , deleteUser.toString());
    }

    /**
     * 로그인 횟수 & 계정 잠금 초기화
     * @param username
     */
    @Override
    public void resetFailureCntAndLock(String username) {
        UserEntity userEntity = userRepository.findByUsername(username);
        userEntity.setFailureCnt(0);
        userEntity.setLocked(false);
        userRepository.save(userEntity);
    }

    /**
     * 비밀번호 초기화
     * @param username
     */
    @Override
    public void resetPassword(String username) {
        UserEntity userEntity = userRepository.findByUsername(username);
        if (userEntity != null) {
            userEntity.setPassword(bCryptPasswordEncoder.encode(userEntity.getComCode()));    // 비밀번호 초기화 -> 회사코드);
            userRepository.save(userEntity);
        }
    }

}
