package com.project.board.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.board.dto.LoginDTO;
import com.project.board.exception.ErrorConst;
import com.project.board.util.Auth;
import com.project.board.util.JwtUtil;
import com.project.board.util.ResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.info(request.getRequestURI());

        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        Auth auth = handlerMethod.getMethodAnnotation(Auth.class);

        if(auth != null) {

            String authToken = request.getHeader("Authorization");
            if (StringUtils.hasText(authToken) && authToken.startsWith("Bearer ")) {
                authToken = authToken.substring(7);
            }

            if(!StringUtils.hasText(authToken)){
                if(!auth.isOptional()){
                    sendErrorResponse(response,new ResponseDTO(HttpStatus.FORBIDDEN, ErrorConst.REQUIRED_AUTH.getCode(), ErrorConst.REQUIRED_AUTH.getMessage()));
                    return false;
                }
            }
            else{
                ResponseDTO dto = jwtUtil.validateToken(authToken, request);
                if(!dto.isResultCode()){
                    sendErrorResponse(response,dto);
                    return false;
                }
            }

        }


        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("{} {}", response.getStatus(), request.getRequestURI());
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

    private void sendErrorResponse(HttpServletResponse response, ResponseDTO dto) throws IOException {
        response.setStatus(dto.getErrorCode());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(new ObjectMapper().writeValueAsString(dto));

    }

}


