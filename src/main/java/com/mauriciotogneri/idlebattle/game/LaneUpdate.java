package com.mauriciotogneri.idlebattle.game;

import com.mauriciotogneri.idlebattle.messages.OutputMessage;
import com.mauriciotogneri.idlebattle.types.OutputEvent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LaneUpdate
{
    private final OutputEvent event;
    private final Player player;

    private LaneUpdate(OutputEvent event, Player player)
    {
        this.event = event;
        this.player = player;
    }

    public void send()
    {
        OutputMessage message = message();

        if (message != null)
        {
            player.send(message);
        }
    }

    private @Nullable OutputMessage message()
    {
        switch (event)
        {
            case LANE_REWARD_WON:
                return OutputMessage.laneRewardWon();
            case LANE_REWARD_LOST:
                return OutputMessage.laneRewardLost();
            case LANE_WON:
                return OutputMessage.laneWon();
            case LANE_LOST:
                return OutputMessage.laneLost();
        }

        return null;
    }

    public static @NotNull LaneUpdate laneRewardWon(Player player)
    {
        return new LaneUpdate(OutputEvent.LANE_REWARD_WON, player);
    }

    public static @NotNull LaneUpdate laneRewardLost(Player player)
    {
        return new LaneUpdate(OutputEvent.LANE_REWARD_LOST, player);
    }

    public static @NotNull LaneUpdate laneWon(Player player)
    {
        return new LaneUpdate(OutputEvent.LANE_WON, player);
    }

    public static @NotNull LaneUpdate laneLost(Player player)
    {
        return new LaneUpdate(OutputEvent.LANE_LOST, player);
    }
}
