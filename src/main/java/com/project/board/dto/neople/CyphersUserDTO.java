package com.project.board.dto.neople;

import lombok.*;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class CyphersUserDTO {

    private String playerId;

    private String nickname;

    private int grade;

    private boolean tierTest;

    private Represent represent;

    private String clanName;

    private String ratingPoint;

    private String maxRatingPoint;

    private String tierName;

    private List<Record> records;



}
