package com.mauriciotogneri.idlebattle.messages;

import java.util.List;

public class MatchStatus
{
    private final String id;
    private final double remainingTime;
    private final List<LaneStatus> lanes;

    public MatchStatus(String id, double remainingTime, List<LaneStatus> lanes)
    {
        this.id = id;
        this.remainingTime = remainingTime;
        this.lanes = lanes;
    }
}
