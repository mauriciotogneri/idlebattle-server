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
    public final MatchStatus matchStatus;

    private OutputMessage(OutputEvent event,
                          String playerName,
                          String matchId,
                          Integer laneId,
                          Integer amount,
                          PlayerStatus playerStatus,
                          MatchStatus matchStatus)
    {
        this.event = event;
        this.playerName = playerName;
        this.matchId = matchId;
        this.laneId = laneId;
        this.amount = amount;
        this.playerStatus = playerStatus;
        this.matchStatus = matchStatus;
    }

    public OutputMessage withPlayerName(String playerName)
    {
        return new OutputMessage(event, playerName, matchId, laneId, amount, playerStatus, matchStatus);
    }

    public OutputMessage withMatchId(String matchId)
    {
        return new OutputMessage(event, playerName, matchId, laneId, amount, playerStatus, matchStatus);
    }

    public OutputMessage withLaneId(Integer laneId)
    {
        return new OutputMessage(event, playerName, matchId, laneId, amount, playerStatus, matchStatus);
    }

    public OutputMessage withAmount(Integer amount)
    {
        return new OutputMessage(event, playerName, matchId, laneId, amount, playerStatus, matchStatus);
    }

    public OutputMessage withPlayerStatus(PlayerStatus playerStatus)
    {
        return new OutputMessage(event, playerName, matchId, laneId, amount, playerStatus, matchStatus);
    }

    public OutputMessage withMatchStatus(MatchStatus matchStatus)
    {
        return new OutputMessage(event, playerName, matchId, laneId, amount, playerStatus, matchStatus);
    }

    @NotNull
    public static OutputMessage create(OutputEvent event)
    {
        return new OutputMessage(event, null, null, null, null, null, null);
    }

    @NotNull
    public static OutputMessage waitingPublic()
    {
        return create(OutputEvent.WAITING);
    }

    @NotNull
    public static OutputMessage waitingPrivate(String matchId)
    {
        return create(OutputEvent.WAITING).withMatchId(matchId);
    }

    @NotNull
    public static OutputMessage matchStarted(String matchId)
    {
        return create(OutputEvent.MATCH_STARTED).withMatchId(matchId);
    }

    @NotNull
    public static OutputMessage playerUpdate(PlayerStatus playerStatus)
    {
        return create(OutputEvent.PLAYER_UPDATE).withPlayerStatus(playerStatus);
    }

    @NotNull
    public static OutputMessage matchUpdate(MatchStatus matchStatus)
    {
        return create(OutputEvent.MATCH_UPDATE).withMatchStatus(matchStatus);
    }

    @NotNull
    public static OutputMessage invalidMatchId(String matchId)
    {
        return create(OutputEvent.INVALID_MATCH_ID).withMatchId(matchId);
    }

    @NotNull
    public static OutputMessage invalidPlayerName(String playerName)
    {
        return create(OutputEvent.INVALID_PLAYER_NAME).withPlayerName(playerName);
    }

    @NotNull
    public static OutputMessage invalidLaneId(Integer laneId)
    {
        return create(OutputEvent.INVALID_LANE_ID).withLaneId(laneId);
    }

    @NotNull
    public static OutputMessage invalidAmount(Integer amount)
    {
        return create(OutputEvent.INVALID_AMOUNT).withAmount(amount);
    }

    @NotNull
    public static OutputMessage playerDisconnected(String playerName, String matchId)
    {
        return create(OutputEvent.PLAYER_DISCONNECTED)
                .withPlayerName(playerName)
                .withMatchId(matchId);
    }
}