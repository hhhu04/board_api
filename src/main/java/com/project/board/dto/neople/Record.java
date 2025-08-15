package com.project.board.dto.neople;

import lombok.Data;

@Data
public class Record {

    private String gameTypeId;

    private int winCount;

    private int loseCount;

    private int stopCount;

    private int playCount;

}
