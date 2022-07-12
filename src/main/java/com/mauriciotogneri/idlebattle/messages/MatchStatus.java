package com.mauriciotogneri.idlebattle.messages;

import com.mauriciotogneri.idlebattle.game.Lane;

import java.util.List;

public class MatchStatus
{
    private final String id;
    private final double totalTime;
    private final List<PlayerIdentity> players;
    private final List<Lane> lanes;

    public MatchStatus(String id, double totalTime, List<PlayerIdentity> players, List<Lane> lanes)
    {
        this.id = id;
        this.totalTime = totalTime;
        this.players = players;
        this.lanes = lanes;
    }
}
