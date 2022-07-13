package com.mauriciotogneri.idlebattle.messages;

public class MatchStatus
{
    private final String id;
    private final Integer remainingTime;
    private final PlayerStatus[] players;
    private final LaneStatus[] lanes;

    public MatchStatus(String id,
                       Integer remainingTime,
                       PlayerStatus[] player,
                       LaneStatus[] lanes)
    {
        this.id = id;
        this.remainingTime = remainingTime;
        this.players = player;
        this.lanes = lanes;
    }
}
