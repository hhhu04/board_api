package com.project.board.util.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JwtType {

    ACCESS_TOKEN(
//         60 * 60 * 24 * 1 // 1일
//            60 * 60 // 1시간 (상용)
            60 // 2분 (테스트용)
    ),
    REFRESH_TOKEN(
//        60 * 60 * 24 * 60 // 2달
            60 * 60 * 24 * 7 // 1주일 (상용)
//        60 * 5 // 5분 (테스트용)
    );

    private long expireSec;

}

