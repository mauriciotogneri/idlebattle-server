package com.mauriciotogneri.idlebattle.game;

import com.mauriciotogneri.idlebattle.app.Constants;

public class Units
{
    private final int direction;
    private final int amount;
    private final double damagePerUnit;
    private final double totalDamage;
    private double progress;

    public Units(int direction, int amount, double damagePerUnit)
    {
        this.direction = direction;
        this.amount = amount;
        this.damagePerUnit = damagePerUnit;
        this.totalDamage = amount * ((amount / Constants.BLOCK_MULTIPLIER) + 1) * damagePerUnit;

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
        progress += (dt * Constants.UNIT_SPEED) * direction;
    }
}
