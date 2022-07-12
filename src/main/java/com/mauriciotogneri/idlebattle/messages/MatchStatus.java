package com.mauriciotogneri.idlebattle.messages;

import java.util.List;

public class MatchStatus
{
    private final String id;
    private final int remainingTime;
    private final List<LaneStatus> lanes;

    public MatchStatus(String id, int remainingTime, List<LaneStatus> lanes)
    {
        this.id = id;
        this.remainingTime = remainingTime;
        this.lanes = lanes;
    }
}
