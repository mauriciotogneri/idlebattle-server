package com.mauriciotogneri.idlebattle.messages;

public class LaneStatus
{
    private final Boolean enabled;
    private final Boolean rewardEnabled;
    private final Double wall;

    public LaneStatus(boolean enabled, boolean rewardEnabled, double wall)
    {
        this.enabled = enabled;
        this.rewardEnabled = rewardEnabled;
        this.wall = wall;
    }
}
