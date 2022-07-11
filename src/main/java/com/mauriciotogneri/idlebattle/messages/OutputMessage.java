package com.mauriciotogneri.idlebattle.messages;

import com.mauriciotogneri.idlebattle.types.OutputEvent;

import org.jetbrains.annotations.NotNull;

public class OutputMessage
{
    public final OutputEvent event;
    public final String playerName;
    public final String matchId;
    public final Integer laneId;
    public final Integer amount;
    public final PlayerStatus playerStatus;

    public OutputMessage(OutputEvent event,
                         String playerName,
                         String matchId,
                         Integer laneId,
                         Integer amount,
                         PlayerStatus playerStatus)
    {
        this.event = event;
        this.playerName = playerName;
        this.matchId = matchId;
        this.laneId = laneId;
        this.amount = amount;
        this.playerStatus = playerStatus;
    }

    @NotNull
    public static OutputMessage waitingPublic()
    {
        return new OutputMessage(OutputEvent.WAITING, null, null, null, null, null);
    }

    @NotNull
    public static OutputMessage waitingPrivate(String matchId)
    {
        return new OutputMessage(OutputEvent.WAITING, null, matchId, null, null, null);
    }

    @NotNull
    public static OutputMessage matchStarted(String matchId)
    {
        return new OutputMessage(OutputEvent.MATCH_STARTED, null, matchId, null, null, null);
    }

    @NotNull
    public static OutputMessage playerUpdate(PlayerStatus playerStatus)
    {
        return new OutputMessage(OutputEvent.PLAYER_UPDATE, null, null, null, null, playerStatus);
    }

    @NotNull
    public static OutputMessage matchUpdate(String matchId)
    {
        return new OutputMessage(OutputEvent.MATCH_UPDATE, null, matchId, null, null, null);
    }

    @NotNull
    public static OutputMessage invalidMatchId(String matchId)
    {
        return new OutputMessage(OutputEvent.INVALID_MATCH_ID, null, matchId, null, null, null);
    }

    @NotNull
    public static OutputMessage invalidPlayerName(String playerName)
    {
        return new OutputMessage(OutputEvent.INVALID_PLAYER_NAME, playerName, null, null, null, null);
    }

    @NotNull
    public static OutputMessage invalidLaneId(Integer laneId)
    {
        return new OutputMessage(OutputEvent.INVALID_LANE_ID, null, null, laneId, null, null);
    }

    @NotNull
    public static OutputMessage invalidAmount(Integer amount)
    {
        return new OutputMessage(OutputEvent.INVALID_AMOUNT, null, null, null, amount, null);
    }

    @NotNull
    public static OutputMessage playerDisconnected(String name, String matchId)
    {
        return new OutputMessage(OutputEvent.PLAYER_DISCONNECTED, name, matchId, null, null, null);
    }
}