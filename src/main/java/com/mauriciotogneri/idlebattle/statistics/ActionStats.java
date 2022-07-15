package com.mauriciotogneri.idlebattle.statistics;

import com.mauriciotogneri.idlebattle.game.Player;
import com.mauriciotogneri.idlebattle.messages.MatchStatus;
import com.mauriciotogneri.idlebattle.types.EndReason;
import com.mauriciotogneri.idlebattle.types.StatsEvent;

import org.jetbrains.annotations.NotNull;

public class ActionStats
{
    public final StatsEvent event;
    public final String playerId;
    public final Integer laneId;
    public final Integer amount;
    public final Integer direction;
    public final Integer attackLevel;
    public final EndReason endReason;
    public final MatchStatus matchStatus;

    private ActionStats(StatsEvent event,
                        String playerId,
                        Integer laneId,
                        Integer amount,
                        Integer direction,
                        Integer attackLevel,
                        EndReason endReason,
                        MatchStatus matchStatus)
    {
        this.event = event;
        this.playerId = playerId;
        this.laneId = laneId;
        this.amount = amount;
        this.direction = direction;
        this.attackLevel = attackLevel;
        this.endReason = endReason;
        this.matchStatus = matchStatus;
    }

    public static @NotNull ActionStats increaseMine(@NotNull Player player)
    {
        return new ActionStats(
                StatsEvent.INCREASE_MINE,
                player.id(),
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public static @NotNull ActionStats increaseAttack(@NotNull Player player)
    {
        return new ActionStats(
                StatsEvent.INCREASE_ATTACK,
                player.id(),
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public static @NotNull ActionStats unitsLaunched(
            @NotNull Player player,
            int laneId,
            int amount,
            int direction,
            int attackLevel)
    {
        return new ActionStats(
                StatsEvent.UNITS_LAUNCHED,
                player.id(),
                laneId,
                amount,
                direction,
                attackLevel,
                null,
                null
        );
    }

    public static @NotNull ActionStats playerDisconnected(@NotNull Player player)
    {
        return new ActionStats(
                StatsEvent.PLAYER_DISCONNECTED,
                player.id(),
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public static @NotNull ActionStats matchReady(MatchStatus matchStatus)
    {
        return new ActionStats(
                StatsEvent.MATCH_READY,
                null,
                null,
                null,
                null,
                null,
                null,
                matchStatus
        );
    }

    public static @NotNull ActionStats matchStarted()
    {
        return new ActionStats(
                StatsEvent.MATCH_STARTED,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public static @NotNull ActionStats matchUpdate(MatchStatus matchStatus)
    {
        return new ActionStats(
                StatsEvent.MATCH_UPDATE,
                null,
                null,
                null,
                null,
                null,
                null,
                matchStatus
        );
    }

    public static @NotNull ActionStats matchFinished(MatchStatus matchStatus, EndReason endReason)
    {
        return new ActionStats(
                StatsEvent.MATCH_FINISHED,
                null,
                null,
                null,
                null,
                null,
                endReason,
                matchStatus
        );
    }
}
