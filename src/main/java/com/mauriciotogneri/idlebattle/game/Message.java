package com.mauriciotogneri.idlebattle.game;

import org.jetbrains.annotations.NotNull;

public class Message
{
    public final Event event;
    public final String playerName;
    public final String matchId;

    public Message(Event event,
                   String playerName,
                   String matchId)
    {
        this.event = event;
        this.playerName = playerName;
        this.matchId = matchId;
    }

    @NotNull
    public static Message waitingPublic()
    {
        return new Message(Event.WAITING, null, null);
    }

    @NotNull
    public static Message waitingPrivate(String matchId)
    {
        return new Message(Event.WAITING, null, matchId);
    }

    @NotNull
    public static Message matchStarted(String matchId)
    {
        return new Message(Event.MATCH_STARTED, null, matchId);
    }

    @NotNull
    public static Message invalidMatchId(String matchId)
    {
        return new Message(Event.INVALID_MATCH_ID, null, matchId);
    }
}