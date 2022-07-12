package com.mauriciotogneri.idlebattle.messages;

import java.util.List;

public class MatchStatus
{
    private final String id;
    private final Integer remainingTime;
    private final List<PlayerStatus> players;
    private final List<LaneStatus> lanes;

    public MatchStatus(String id,
                       Integer remainingTime,
                       List<PlayerStatus> player,
                       List<LaneStatus> lanes)
    {
        this.id = id;
        this.remainingTime = remainingTime;
        this.players = player;
        this.lanes = lanes;
    }
}
