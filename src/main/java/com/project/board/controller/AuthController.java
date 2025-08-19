package com.project.board.controller;

import com.project.board.dto.LoginDTO;
import com.project.board.service.UserService;
import com.project.board.util.Auth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
public class AuthController {

    private final UserService userService;

    @Auth
    @GetMapping("/user/info")
    public ResponseEntity<?> userInfo(Authentication auth){
        LoginDTO loginDTO = (LoginDTO) auth.getPrincipal();
        return ResponseEntity.ok(loginDTO);
    }

}
