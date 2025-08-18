package com.project.board.controller;

import com.project.board.service.UserService;
import com.project.board.util.Auth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

    @Auth
    @GetMapping("/test")
    public String loginCheck() {
        return "test";
    }


}
