package com.project.board.util.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.project.board.repository.UserRepository;
import io.jsonwebtoken.*;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationTokenProvider {

    private final UserRepository userRepository;

    private static final String JWT_ISSUER = "atteri";

    private static final String CLAIM_NAME_TOKEN_TYPE = "tokenType";

    @Value("${common.jwt.secret}")
    String SECRET_KEY;

    public Token validateToken(String token, String refreshToken, HttpServletRequest req,  HttpServletResponse resp) throws IOException {
        if (StringUtils.isNotEmpty(token)) {

            Token result = new Token(false);

            try {
                Jwts.parser().setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8)).parseClaimsJws(token);
                return new Token(true);
            } catch (SignatureException e) {
                log.error("[SignatureException]",e);
            } catch (MalformedJwtException e) {
                log.error("[MalformedJwtException]",e);
            } catch (ExpiredJwtException e) {
                log.info("[expired token] {}", token);
                result = refreshToken(refreshToken, req, resp);
            } catch (UnsupportedJwtException e) {
                log.error("[UnsupportedJwtException]",e);
            } catch (IllegalArgumentException e) {
                log.error("[IllegalArgumentException]",e);
            }
            return result;
        } else {
            return new Token(false);
        }
    }

}
