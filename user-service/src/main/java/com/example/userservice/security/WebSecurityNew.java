package com.example.userservice.security;

import com.example.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;

@Configuration
@EnableWebSecurity
public class WebSecurityNew {
    private UserService userService;
    // 패스워드 암호화를 위해 Bcrypt 암호화 방식 사용
    // Bcrypt 암호화 방식이란, 해시 함수를 이용하여 비밀번호를 여러번 암호화하는 방식
    private BCryptPasswordEncoder bCryptPasswordEncoder; // 비밀번호 암호화
    private Environment env;

    @Value("${server.ip}")
    public static String ALLOWED_IP_ADDRESS;

    public WebSecurityNew(Environment env, UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.env = env;
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    @Bean //권한 작업 처리
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        // Configure AuthenticationManagerBuilder
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);

        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        http.csrf( (csrf) -> csrf.disable() );

        http.authorizeHttpRequests(
                authorize -> authorize
                        .requestMatchers("/**")
                        .access(
                                new WebExpressionAuthorizationManager("hasIpAddress('192.168.123.12') or hasIpAddress('::1') or hasIpAddress('" + ALLOWED_IP_ADDRESS + "')")
                        )
                        .anyRequest().authenticated()
        )
                .authenticationManager(authenticationManager)
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.headers((headers) -> headers.frameOptions( (frameOptions) -> frameOptions.sameOrigin()));
        http.addFilter(getAuthenticationFilter(authenticationManager));

        return http.build();
    }

    private AuthenticationFilterNew getAuthenticationFilter(AuthenticationManager authenticationManager) throws Exception {
        return new AuthenticationFilterNew(authenticationManager, userService, env);
    }
}
