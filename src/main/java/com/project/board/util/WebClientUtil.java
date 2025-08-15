package com.project.board.util;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientUtil {

    public WebClient getWebClient() {
        return WebClient.builder()
                .defaultHeader("Content-Type", "application/json;charset=UTF-8")
                .defaultHeader("Accept", "application/json")
                .build();
    }

}
