package com.mauriciotogneri.idlebattle.messages;

public class MatchStatus
{
    private final String id;
    private final Double remainingTime;
    private final PlayerStatus[] players;
    private final LaneStatus[] lanes;

    public MatchStatus(String id,
                       Double remainingTime,
                       PlayerStatus[] player,
                       LaneStatus[] lanes)
    {
        this.id = id;
        this.remainingTime = remainingTime;
        this.players = player;
        this.lanes = lanes;
    }
}
