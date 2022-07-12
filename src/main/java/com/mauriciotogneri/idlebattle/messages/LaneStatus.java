package com.mauriciotogneri.idlebattle.messages;

public class LaneStatus
{
    private final boolean enabled;
    private final boolean rewardEnabled;

    public LaneStatus(boolean enabled, boolean rewardEnabled)
    {
        this.enabled = enabled;
        this.rewardEnabled = rewardEnabled;
    }
}
