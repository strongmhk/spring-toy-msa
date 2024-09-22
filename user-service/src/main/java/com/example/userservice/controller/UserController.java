package com.example.userservice.controller;

import com.example.userservice.valueobject.Greeting;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
class UserController {
    private final Environment env;
    private final Greeting greeting;

    @GetMapping("/heath-check")
    public String status() {
        return "It's Working in User Service";
    }

    @GetMapping("/welcome")
    public String welcome() {
//        return env.getProperty("greeting.message"); // application.yml에 정의한 greeting.message 값 반환
        return greeting.getMessage(); // Greeting.java에서 @Value("${greeting.message}")로 설정한 greeting.message 값 반환
    }
}



