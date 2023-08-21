package com.bonfire.challenge.validation;

import com.bonfire.challenge.entity.UserEntity;
import com.bonfire.challenge.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

/**
 * username 중복검사 validate
 */
@RequiredArgsConstructor
public class UsernameValidator implements ConstraintValidator<Username, String> {

    private final UserRepository userRepository;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        UserEntity userEntity = userRepository.findByUsername(value);
        return userEntity == null;
    }
}
