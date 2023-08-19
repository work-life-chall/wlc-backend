package com.bonfire.challenge.config;

import com.bonfire.challenge.repository.UserRepository;
import com.bonfire.challenge.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurity {
    private final Environment env;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ObjectPostProcessor<Object> objectPostProcessor;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth.requestMatchers("/login").permitAll());
        http.csrf(AbstractHttpConfigurer::disable)
                // add jwt filters (1. authentication, 2. authorization)
                .addFilter(getJwtAuthorizationFilter())
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .addFilter(getAuthenticationFilter())
                .formLogin(login -> login	// form 방식 로그인 사용
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(withDefaults());
        return http.build();
    }

    private JwtAuthenticationTokenFilter getJwtAuthorizationFilter() throws Exception {
        AuthenticationManagerBuilder builder = new AuthenticationManagerBuilder(objectPostProcessor);
        return new JwtAuthenticationTokenFilter(authenticationManager(builder), userRepository, env);
    }

    private AuthenticationFilter getAuthenticationFilter() throws Exception {
        AuthenticationManagerBuilder builder = new AuthenticationManagerBuilder(objectPostProcessor);
        return new AuthenticationFilter(authenticationManager(builder), userService, env);
    }

    public AuthenticationManager authenticationManager(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
        return auth.build();
    }
}