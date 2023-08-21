package com.bonfire.challenge.validation;

import jakarta.validation.Constraint;

import java.lang.annotation.*;


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UsernameValidator.class)
public @interface Username {
    String message() default "username";
    Class[] groups() default {};
    Class[] payload() default {};
}
