package com.mauriciotogneri.idlebattle.game;

import com.mauriciotogneri.idlebattle.app.Constants;
import com.mauriciotogneri.idlebattle.messages.MatchConfiguration;

public class Units
{
    private final MatchConfiguration configuration;
    private final int direction;
    private final double totalDamage;
    private double progress;

    public Units(MatchConfiguration configuration, int direction, int amount, double damagePerUnit)
    {
        this.configuration = configuration;
        this.direction = direction;
        this.totalDamage = amount * ((amount / configuration.blockMultiplier) + 1) * damagePerUnit;

        if (direction == Constants.DIRECTION_UP)
        {
            progress = 0;
        }
        else if (direction == Constants.DIRECTION_DOWN)
        {
            progress = 1;
        }
    }

    public double totalDamage()
    {
        return totalDamage;
    }

    public int direction()
    {
        return direction;
    }

    public double force()
    {
        return totalDamage * direction;
    }

    public boolean passedWall(double wall)
    {
        if (direction == Constants.DIRECTION_UP)
        {
            return progress >= wall;
        }
        else if (direction == Constants.DIRECTION_DOWN)
        {
            return progress <= wall;
        }
        else
        {
            return false;
        }
    }

    public void update(double dt)
    {
        progress += dt * configuration.unitSpeed * direction;
    }
}
