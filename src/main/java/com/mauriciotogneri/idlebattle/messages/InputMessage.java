package com.mauriciotogneri.idlebattle.messages;

import com.mauriciotogneri.idlebattle.types.InputEvent;

public class InputMessage
{
    public final InputEvent event;
    public final String playerName;
    public final String matchId;
    public final Integer laneId;
    public final Integer amount;

    public InputMessage(InputEvent event,
                        String playerName,
                        String matchId,
                        Integer laneId,
                        Integer amount)
    {
        this.event = event;
        this.playerName = playerName;
        this.matchId = matchId;
        this.laneId = laneId;
        this.amount = amount;
    }
}