package com.mauriciotogneri.idlebattle.statistics;

import com.mauriciotogneri.idlebattle.messages.MatchConfiguration;
import com.mauriciotogneri.idlebattle.types.EndReason;

public class MatchStats
{
    public final String id;
    public final String start;
    public final String end;
    public final Integer time;
    public final EndReason endReason;
    public final MatchConfiguration configuration;
    public final PlayerStats[] players;

    public MatchStats(String id,
                      String start,
                      String end,
                      Integer time,
                      EndReason endReason,
                      MatchConfiguration configuration,
                      PlayerStats[] players)
    {
        this.id = id;
        this.start = start;
        this.end = end;
        this.time = time;
        this.endReason = endReason;
        this.configuration = configuration;
        this.players = players;
    }
}
