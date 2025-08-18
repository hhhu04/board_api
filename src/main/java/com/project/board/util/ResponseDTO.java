package com.project.board.util;

import lombok.*;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class ResponseDTO<T> {
    boolean resultCode   = true;
    String message      = "성공";
    T object;
    int errorCode = 0;

    public ResponseDTO(T object) {
        this.object = object;
    }

    public ResponseDTO(HttpStatus status, T object) {
        this.resultCode = false;
        this.message = "실패";
        this.object = object;
        this.errorCode = status.value();
    }

    public ResponseDTO(HttpStatus status, T object, String message) {
        this.resultCode = false;
        this.message = message;
        this.object = object;
        this.errorCode = status.value();
    }


}
