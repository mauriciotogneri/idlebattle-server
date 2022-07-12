package com.mauriciotogneri.idlebattle.game;

import com.mauriciotogneri.idlebattle.messages.MatchConfiguration;

import org.jetbrains.annotations.NotNull;

public class Units
{
    private final MatchConfiguration configuration;
    private final int direction;
    private final double totalDamage;
    private double progress;

    public Units(@NotNull MatchConfiguration configuration, int direction, int amount, double damagePerUnit)
    {
        this.configuration = configuration;
        this.direction = direction;
        this.totalDamage = amount * ((amount / configuration.blockMultiplier) + 1) * damagePerUnit;
        this.progress = 0;
    }

    public int direction()
    {
        return direction;
    }

    public double progress()
    {
        return progress;
    }

    public double totalDamage()
    {
        return totalDamage;
    }

    public void update(double dt)
    {
        progress += dt * configuration.unitSpeed;
    }
}
