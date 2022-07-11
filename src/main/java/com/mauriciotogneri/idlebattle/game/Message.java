package com.mauriciotogneri.idlebattle.game;

import com.mauriciotogneri.idlebattle.types.Event;

import org.jetbrains.annotations.NotNull;

public class Message
{
    public final Event event;
    public final String playerName;
    public final String matchId;
    public final Integer laneId;
    public final Integer amount;

    public Message(Event event,
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

    @NotNull
    public static Message waitingPublic()
    {
        return new Message(Event.WAITING, null, null, null, null);
    }

    @NotNull
    public static Message waitingPrivate(String matchId)
    {
        return new Message(Event.WAITING, null, matchId, null, null);
    }

    @NotNull
    public static Message matchStarted(String matchId)
    {
        return new Message(Event.MATCH_STARTED, null, matchId, null, null);
    }

    @NotNull
    public static Message matchUpdate(String matchId)
    {
        return new Message(Event.MATCH_UPDATE, null, matchId, null, null);
    }

    @NotNull
    public static Message invalidMatchId(String matchId)
    {
        return new Message(Event.INVALID_MATCH_ID, null, matchId, null, null);
    }

    @NotNull
    public static Message invalidPlayerName(String playerName)
    {
        return new Message(Event.INVALID_PLAYER_NAME, playerName, null, null, null);
    }

    @NotNull
    public static Message playerDisconnected(String name, String matchId)
    {
        return new Message(Event.PLAYER_DISCONNECTED, name, matchId, null, null);
    }
}