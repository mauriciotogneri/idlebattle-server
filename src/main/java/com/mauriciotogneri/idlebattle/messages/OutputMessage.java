package com.mauriciotogneri.idlebattle.messages;

import com.mauriciotogneri.idlebattle.types.FinishState;
import com.mauriciotogneri.idlebattle.types.OutputEvent;

import org.jetbrains.annotations.NotNull;

public class OutputMessage
{
    public final OutputEvent event;
    public final String playerName;
    public final String matchId;
    public final Integer laneId;
    public final Integer amount;
    public final Integer direction;
    public final Integer attackLevel;
    public final FinishState finishState;
    public final MatchStatus matchStatus;
    public final MatchConfiguration configuration;

    private OutputMessage(OutputEvent event,
                          String playerName,
                          String matchId,
                          Integer laneId,
                          Integer amount,
                          Integer direction,
                          Integer attackLevel,
                          FinishState finishState,
                          MatchStatus matchStatus,
                          MatchConfiguration configuration)
    {
        this.event = event;
        this.playerName = playerName;
        this.matchId = matchId;
        this.laneId = laneId;
        this.amount = amount;
        this.direction = direction;
        this.attackLevel = attackLevel;
        this.finishState = finishState;
        this.matchStatus = matchStatus;
        this.configuration = configuration;
    }

    public OutputMessage withPlayerName(String playerName)
    {
        return new OutputMessage(event,
                                 playerName,
                                 matchId,
                                 laneId,
                                 amount,
                                 direction,
                                 attackLevel,
                                 finishState,
                                 matchStatus,
                                 configuration);
    }

    public OutputMessage withMatchId(String matchId)
    {
        return new OutputMessage(event,
                                 playerName,
                                 matchId,
                                 laneId,
                                 amount,
                                 direction,
                                 attackLevel,
                                 finishState,
                                 matchStatus,
                                 configuration);
    }

    public OutputMessage withLaneId(Integer laneId)
    {
        return new OutputMessage(event,
                                 playerName,
                                 matchId,
                                 laneId,
                                 amount,
                                 direction,
                                 attackLevel,
                                 finishState,
                                 matchStatus,
                                 configuration);
    }

    public OutputMessage withAmount(Integer amount)
    {
        return new OutputMessage(event,
                                 playerName,
                                 matchId,
                                 laneId,
                                 amount,
                                 direction,
                                 attackLevel,
                                 finishState,
                                 matchStatus,
                                 configuration);
    }

    public OutputMessage withDirection(Integer direction)
    {
        return new OutputMessage(event,
                                 playerName,
                                 matchId,
                                 laneId,
                                 amount,
                                 direction,
                                 attackLevel,
                                 finishState,
                                 matchStatus,
                                 configuration);
    }

    public OutputMessage withAttackLevel(Integer attackLevel)
    {
        return new OutputMessage(event,
                                 playerName,
                                 matchId,
                                 laneId,
                                 amount,
                                 direction,
                                 attackLevel,
                                 finishState,
                                 matchStatus,
                                 configuration);
    }

    public OutputMessage withConfiguration(MatchConfiguration configuration)
    {
        return new OutputMessage(event,
                                 playerName,
                                 matchId,
                                 laneId,
                                 amount,
                                 direction,
                                 attackLevel,
                                 finishState,
                                 matchStatus,
                                 configuration);
    }

    public OutputMessage withMatchStatus(MatchStatus matchStatus)
    {
        return new OutputMessage(event,
                                 playerName,
                                 matchId,
                                 laneId,
                                 amount,
                                 direction,
                                 attackLevel,
                                 finishState,
                                 matchStatus,
                                 configuration);
    }

    public OutputMessage withFinishState(FinishState finishState)
    {
        return new OutputMessage(event,
                                 playerName,
                                 matchId,
                                 laneId,
                                 amount,
                                 direction,
                                 attackLevel,
                                 finishState,
                                 matchStatus,
                                 configuration);
    }

    @NotNull
    public static OutputMessage create(OutputEvent event)
    {
        return new OutputMessage(event, null, null, null, null, null, null, null, null, null);
    }

    @NotNull
    public static OutputMessage welcome()
    {
        return create(OutputEvent.WELCOME);
    }

    @NotNull
    public static OutputMessage waitingPublic()
    {
        return create(OutputEvent.WAITING_PUBLIC);
    }

    @NotNull
    public static OutputMessage waitingPrivate(String matchId)
    {
        return create(OutputEvent.WAITING_PRIVATE)
                .withMatchId(matchId);
    }

    @NotNull
    public static OutputMessage matchReady(
            MatchStatus matchStatus,
            MatchConfiguration configuration)
    {
        return create(OutputEvent.MATCH_READY)
                .withMatchStatus(matchStatus)
                .withConfiguration(configuration);
    }

    @NotNull
    public static OutputMessage matchStarted()
    {
        return create(OutputEvent.MATCH_STARTED);
    }

    @NotNull
    public static OutputMessage matchUpdate(MatchStatus matchStatus)
    {
        return create(OutputEvent.MATCH_UPDATE)
                .withMatchStatus(matchStatus);
    }

    @NotNull
    public static OutputMessage laneRewardWon()
    {
        return create(OutputEvent.LANE_REWARD_WON);
    }

    @NotNull
    public static OutputMessage laneRewardLost()
    {
        return create(OutputEvent.LANE_REWARD_LOST);
    }

    @NotNull
    public static OutputMessage laneWon()
    {
        return create(OutputEvent.LANE_WON);
    }

    @NotNull
    public static OutputMessage laneLost()
    {
        return create(OutputEvent.LANE_LOST);
    }

    @NotNull
    public static OutputMessage unitsLaunched(int laneId, int amount, int direction, int attackLevel)
    {
        return create(OutputEvent.UNITS_LAUNCHED)
                .withLaneId(laneId)
                .withAmount(amount)
                .withDirection(direction)
                .withAttackLevel(attackLevel);
    }

    @NotNull
    public static OutputMessage matchFinished(FinishState finishState)
    {
        return create(OutputEvent.MATCH_FINISHED)
                .withFinishState(finishState);
    }

    @NotNull
    public static OutputMessage echo(String matchId)
    {
        return create(OutputEvent.ECHO)
                .withMatchId(matchId);
    }

    @NotNull
    public static OutputMessage invalidMatchId(String matchId)
    {
        return create(OutputEvent.INVALID_MATCH_ID)
                .withMatchId(matchId);
    }

    @NotNull
    public static OutputMessage invalidPlayerName(String playerName)
    {
        return create(OutputEvent.INVALID_PLAYER_NAME)
                .withPlayerName(playerName);
    }

    @NotNull
    public static OutputMessage invalidLaneId(Integer laneId)
    {
        return create(OutputEvent.INVALID_LANE_ID)
                .withLaneId(laneId);
    }

    @NotNull
    public static OutputMessage invalidAmount(Integer amount)
    {
        return create(OutputEvent.INVALID_AMOUNT)
                .withAmount(amount);
    }

    @NotNull
    public static OutputMessage playerDisconnected(String playerName, String matchId)
    {
        return create(OutputEvent.PLAYER_DISCONNECTED)
                .withPlayerName(playerName)
                .withMatchId(matchId);
    }
}