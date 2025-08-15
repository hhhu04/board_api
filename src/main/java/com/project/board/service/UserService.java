package com.project.board.service;

import com.project.board.dto.LoginDTO;
import com.project.board.entity.User;
import com.project.board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public User selectUserByUserId(LoginDTO loginDTO) {
        return userRepository.readUserByUserId(loginDTO.getUserId());
    }
}
