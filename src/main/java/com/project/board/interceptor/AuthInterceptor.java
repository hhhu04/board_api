package com.project.board.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.board.dto.LoginDTO;
import com.project.board.exception.ErrorConst;
import com.project.board.util.ResponseDTO;
import com.project.board.util.jwt.JwtAuthenticationTokenProvider;
import com.project.board.util.jwt.Token;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtAuthenticationTokenProvider jwtAuthenticationTokenProvider;

    @Value("${common.jwt.secret}")
    String SECRET_KEY;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String authToken = request.getHeader("Authorization");
        if (StringUtils.hasText(authToken) && authToken.startsWith("Bearer ")) {
            authToken = authToken.substring(7);
        }


        try {
            Jwts.parser().setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8)).parseClaimsJws(authToken);
            int idx = Integer.parseInt(Jwts.parser().setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8)).parseClaimsJws(authToken).getBody().get("aud").toString());
            LoginDTO dto = new LoginDTO();
            dto.setIdx(idx);

            ArrayList<SimpleGrantedAuthority> authList = new ArrayList<>();
            authList.add(new SimpleGrantedAuthority("ROLE_" + "USER"));
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(dto,dto.getPassword(), authList);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (SignatureException e) {
            log.error("[SignatureException]",e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(new ObjectMapper().writeValueAsString(new ResponseDTO(HttpStatus.UNAUTHORIZED, ErrorConst.INVALID_TOKEN.getCode(), ErrorConst.INVALID_TOKEN.getMessage())));
            return false;
        } catch (MalformedJwtException e) {
            log.error("[MalformedJwtException]",e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(new ObjectMapper().writeValueAsString(new ResponseDTO(HttpStatus.UNAUTHORIZED, ErrorConst.INVALID_TOKEN.getCode(), ErrorConst.INVALID_TOKEN.getMessage())));
            return false;
        } catch (ExpiredJwtException e) {
            log.info("[expired token] {}", authToken);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(new ObjectMapper().writeValueAsString(new ResponseDTO(HttpStatus.UNAUTHORIZED, ErrorConst.EXPRIED_TOKEN.getCode(), ErrorConst.EXPRIED_TOKEN.getMessage())));
            return false;
        } catch (UnsupportedJwtException e) {
            log.error("[UnsupportedJwtException]",e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(new ObjectMapper().writeValueAsString(new ResponseDTO(HttpStatus.UNAUTHORIZED, ErrorConst.INVALID_TOKEN.getCode(), ErrorConst.INVALID_TOKEN.getMessage())));
            return false;
        } catch (IllegalArgumentException e) {
            log.error("[IllegalArgumentException]",e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(new ObjectMapper().writeValueAsString(new ResponseDTO(HttpStatus.UNAUTHORIZED, ErrorConst.INVALID_TOKEN.getCode(), ErrorConst.INVALID_TOKEN.getMessage())));
            return false;
        }


        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}


