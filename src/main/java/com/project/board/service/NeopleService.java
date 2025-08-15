package com.project.board.service;

import com.project.board.dto.neople.CyphersUserDTO;
import com.project.board.util.WebClientUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NeopleService {

    @Value("${common.neople.base-url}")
    String BASE_URL;

    @Value("${common.neople.cyphers}")
    String KEY_CYPHERS;

    private final WebClientUtil webClient;

    public Map userSearch(String nickname) {

        StringBuilder url = new StringBuilder(BASE_URL);
        url.append("/cy/players?nickname=").append(nickname).append("&apikey=").append(KEY_CYPHERS);

        return webClient.getWebClient().get()
                .uri(url.toString())
                .retrieve()
                .bodyToMono(Map.class).block();

    }

    public CyphersUserDTO userDetail(String playerId) {
        StringBuilder url = new StringBuilder(BASE_URL);
        url.append("/cy/players/").append(playerId).append("?apikey=").append(KEY_CYPHERS);

        return webClient.getWebClient().get()
                .uri(url.toString())
                .retrieve()
                .bodyToMono(CyphersUserDTO.class).block();
    }


}
