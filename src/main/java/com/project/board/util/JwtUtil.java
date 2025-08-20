package com.project.board.util;

import com.project.board.dto.LoginDTO;
import com.project.board.exception.ErrorConst;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${common.jwt.secret}")
    String SECRET;

    public ResponseDTO validateToken(String token, HttpServletRequest request) {

        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

        try {
            // Bearer 접두사 제거
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // JWT 파싱 및 검증
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // 토큰 유효성 검사
            Date expiration = claims.getExpiration();
            if (expiration.before(new Date())) {
                System.out.println("expried");
                return new ResponseDTO(HttpStatus.UNAUTHORIZED,ErrorConst.EXPRIED_TOKEN.getCode(),ErrorConst.EXPRIED_TOKEN.getMessage());
            }

            // 발행자(issuer) 검증
            String issuer = claims.getIssuer();
            if (!"board-auth".equals(issuer)) {
                return new ResponseDTO(HttpStatus.UNAUTHORIZED,ErrorConst.INVALID_TOKEN.getCode(),ErrorConst.INVALID_TOKEN.getMessage());
            }

            LoginDTO dto = new LoginDTO();
            dto.setIdx((Integer) claims.get("idx"));
            dto.setUserId((String) claims.get("user_id"));

            ArrayList<SimpleGrantedAuthority> authList = new ArrayList<>();
            authList.add(new SimpleGrantedAuthority("ROLE_" + "USER"));

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(dto, dto.getPassword(), authList);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            return new ResponseDTO();

        } catch (ExpiredJwtException e) {
            return new ResponseDTO(HttpStatus.UNAUTHORIZED,ErrorConst.EXPRIED_TOKEN.getCode(),ErrorConst.EXPRIED_TOKEN.getMessage());
        } catch (UnsupportedJwtException | MalformedJwtException | SecurityException | IllegalArgumentException e) {
            return new ResponseDTO(HttpStatus.UNAUTHORIZED,ErrorConst.INVALID_TOKEN.getCode(),ErrorConst.INVALID_TOKEN.getMessage());
        } catch (Exception e) {
            return new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR,ErrorConst.UNKNOWN_ERROR.getCode(),ErrorConst.UNKNOWN_ERROR.getMessage());
        }
    }


}