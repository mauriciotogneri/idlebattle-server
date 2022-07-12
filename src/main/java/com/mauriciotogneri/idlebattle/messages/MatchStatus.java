package com.mauriciotogneri.idlebattle.messages;

import java.util.List;

public class MatchStatus
{
    private final String id;
    private final double totalTime;
    private final List<LaneStatus> lanes;

    public MatchStatus(String id, double totalTime, List<LaneStatus> lanes)
    {
        this.id = id;
        this.totalTime = totalTime;
        this.lanes = lanes;
    }
}
