package com.project.board.controller;

import com.project.board.dto.LoginDTO;
import com.project.board.entity.User;
import com.project.board.service.UserService;
import com.project.board.util.jwt.JwtAuthenticationTokenProvider;
import com.project.board.util.jwt.JwtType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final JwtAuthenticationTokenProvider jwt;

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO, Authentication auth) {
        Map<String,String> data = new HashMap<>();

        if(auth != null){
            return ResponseEntity.status(403).build();
        }

        User user = userService.selectUserByUserId(loginDTO);

        if(user == null){
            return ResponseEntity.status(401).build();
        }
        else if(!user.getPassword().equals(loginDTO.getPassword())){
            return ResponseEntity.status(401).build();
        }

        String token = jwt.createJwtToken(JwtType.ACCESS_TOKEN, user.getUserId());
        String refreshToken = jwt.createJwtToken(JwtType.REFRESH_TOKEN, user.getUserId());
        jwt.saveToken(user.getIdx(), refreshToken);

        data.put("token", token);
        data.put("refreshToken", refreshToken);

        return ResponseEntity.ok(data);
    }

}
