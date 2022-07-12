package com.mauriciotogneri.idlebattle.messages;

import java.util.List;

public class MatchStatus
{
    private final String id;
    private final int remainingTime;
    private final List<PlayerStatus> playerStatus;
    private final List<LaneStatus> lanes;

    public MatchStatus(String id,
                       Integer remainingTime,
                       List<PlayerStatus> playerStatus,
                       List<LaneStatus> lanes)
    {
        this.id = id;
        this.remainingTime = remainingTime;
        this.playerStatus = playerStatus;
        this.lanes = lanes;
    }
}
