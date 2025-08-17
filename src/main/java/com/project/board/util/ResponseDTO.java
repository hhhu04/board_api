package com.project.board.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class ResponseDTO<T> {
    String resultCode   = "SUCCESS";
    String message      = "성공";
    T object;
    int errorCode = 0;

    public ResponseDTO(T object) {
        this.object = object;
    }

    public ResponseDTO(HttpStatus status, T object) {
        this.resultCode = "Fail";
        this.message = "실패";
        this.object = object;
        this.errorCode = status.value();
    }

    public ResponseDTO(HttpStatus status, T object, String message) {
        this.resultCode = "Fail";
        this.message = message;
        this.object = object;
        this.errorCode = status.value();
    }


}
