package com.mauriciotogneri.idlebattle.messages;

public class LaneStatus
{
    private final boolean enabled;
    private final boolean rewardEnabled;
    private final double wall;

    public LaneStatus(boolean enabled, boolean rewardEnabled, double wall)
    {
        this.enabled = enabled;
        this.rewardEnabled = rewardEnabled;
        this.wall = wall;
    }
}
