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

    public String createJwtToken(JwtType jwtType, String id) {

        long now = System.currentTimeMillis();

        String jwtToken = JWT.create()
                .withIssuer(JWT_ISSUER)
                .withAudience(id)
                .withClaim(CLAIM_NAME_TOKEN_TYPE, jwtType.name())
                .withIssuedAt(new Date(now))
                .withExpiresAt(new Date(now + 1 * (1000 * jwtType.getExpireSec())))
                .sign(Algorithm.HMAC256(SECRET_KEY));

        return jwtToken;

    }

    public String parseTokenString(HttpServletRequest request) {
        String bearerToken = request.getHeader("shop_token");
        if(bearerToken == null || bearerToken.equals("")) {
            Cookie[] getCookie = request.getCookies();
            if(getCookie != null) {
                for(Cookie cookie : getCookie) {
                    if(cookie.getName().equals("shop_token")) {
                        bearerToken = cookie.getValue();
                    }
                }
            }
        }

        return bearerToken;
    }

    public String getRefreshToken(HttpServletRequest request) {
        String refreshToken = request.getHeader("shop_refreshToken");
        if(refreshToken == null || refreshToken.equals("")) {
            Cookie[] getCookie = request.getCookies();
            if(getCookie != null) {
                for(Cookie cookie : getCookie) {
                    if(cookie.getName().equals("shop_refreshToken")) {
                        refreshToken = cookie.getValue();
                    }
                }
            }
        }
        return refreshToken;
    }

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

    private Token validateRefreshToken(String refreshToken, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (StringUtils.isNotEmpty(refreshToken)) {

            Token result = new Token(false);

            try {
                Jwts.parser().setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8)).parseClaimsJws(refreshToken);
                return new Token(true);
            } catch (SignatureException e) {
                log.error("[SignatureException]",e);
            } catch (MalformedJwtException e) {
                log.error("[MalformedJwtException]",e);
            } catch (ExpiredJwtException e) {
                log.info("[expired refreshToken] {}", refreshToken);
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

    public Token refreshToken(String refreshToken, HttpServletRequest req, HttpServletResponse resp) throws IOException {

        Token result = new Token(false);

        try {

            Token token = validateRefreshToken(refreshToken, req, resp);

            if(token.isValidate()) {

                String idx = Jwts.parser().setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8)).parseClaimsJws(refreshToken).getBody().get("aud").toString();

                boolean exists = userRepository.existsToken(idx, refreshToken);
                if(!exists){
                    return new Token(false);
                }

                JSONObject jso = new JSONObject();

                String domain = "";
                String aToken = createJwtToken(JwtType.ACCESS_TOKEN, idx);
                String refresh = createJwtToken(JwtType.REFRESH_TOKEN, idx);

                if (!req.getServerName().equals("localhost") && !req.getServerName().contains("192.168.50")) {
                    String serverName = req.getServerName();
                    domain = serverName.substring(serverName.indexOf(".") + 1);
                }

//                updateRefreshToken(Integer.parseInt(idx),refresh);

                Cookie setCookie = new Cookie("shop_token", aToken);
                setCookie.setMaxAge(60 * 60 * 24 * 7);
                setCookie.setPath("/");
                if(req.isSecure()){
                    setCookie.setSecure(true);
                }
                if (!domain.equals("")) {
                    setCookie.setDomain(domain);
                }
                resp.addCookie(setCookie);

                Cookie newRefreshToken = new Cookie("shop_refreshToken", refresh);
                newRefreshToken.setMaxAge(60 * 60 * 24 * 7);
                newRefreshToken.setPath("/");
                if(req.isSecure()){
                    newRefreshToken.setSecure(true);
                }
                if (!domain.equals("")) {
                    newRefreshToken.setDomain(domain);
                }
                resp.addCookie(newRefreshToken);

                jso.put("msg", "success");

                Token vo = new Token(true);
                vo.setToken(aToken);
                vo.setRefreshToken(refresh);
                vo.setRefresh(true);
                return vo;
            }
            else {
                return new Token(false);
            }

        } catch (Exception e1) {
            log.error("[refreshToken error]",e1);
        }

        return result;
    }

    public void saveToken(int userIdx, String token){

        long stamp = Long.parseLong(Jwts.parser().setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8)).parseClaimsJws(token).getBody().get("exp").toString());

        LocalDateTime utcTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(stamp), ZoneId.of("Asia/Seoul"));

        userRepository.saveToken(userIdx,token,utcTime);

    }

}
