package com.project.board.controller;

import com.project.board.service.NeopleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@RestController
@RequestMapping("/api/cyphers")
@RequiredArgsConstructor
@Slf4j
public class NeopleController {

    private final NeopleService neopleService;

    @GetMapping("/user/search")
    public ResponseEntity<?> userSearch(@RequestParam("nickname") String nickname) throws NoSuchAlgorithmException {
        Map data = neopleService.userSearch(nickname);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/user/{playerId}")
    public ResponseEntity<?> userDetail(@PathVariable String playerId){
        return ResponseEntity.ok(neopleService.userDetail(playerId));
    }


    public static String encoder(String input) throws NoSuchAlgorithmException {
        if(input == null){
            throw new NullPointerException("input is null");
        }
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(input.getBytes());

        return bytesToHex(md.digest());
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

}
