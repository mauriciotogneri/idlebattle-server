package com.mauriciotogneri.idlebattle.messages;

import com.mauriciotogneri.idlebattle.app.Constants;

import org.jetbrains.annotations.NotNull;

public class MatchConfiguration
{
    public final int lanes;
    public final int readyTimeout; // in seconds
    public final int matchTimeout; // in seconds

    public final int moneyRate;
    public final int unitCost;
    public final double unitSpeed;

    private MatchConfiguration(int lanes,
                               int readyTimeout,
                               int matchTimeout,
                               int moneyRate,
                               int unitCost,
                               double unitSpeed)
    {
        this.lanes = lanes;
        this.readyTimeout = readyTimeout;
        this.matchTimeout = matchTimeout;
        this.moneyRate = moneyRate;
        this.unitCost = unitCost;
        this.unitSpeed = unitSpeed;
    }

    public int winnerLimit()
    {
        return (lanes / 2) + 1;
    }

    @NotNull
    public static MatchConfiguration create()
    {
        return new MatchConfiguration(
                Constants.LANES,
                Constants.READY_TIMEOUT,
                Constants.MATCH_TIMEOUT,
                Constants.MONEY_RATE,
                Constants.UNIT_COST,
                Constants.UNIT_SPEED
        );
    }
}
