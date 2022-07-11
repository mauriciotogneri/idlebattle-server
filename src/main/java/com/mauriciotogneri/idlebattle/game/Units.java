package com.mauriciotogneri.idlebattle.game;

import com.mauriciotogneri.idlebattle.app.Constants;

public class Units
{
    private final int direction;
    private final int amount;
    private final double damagePerUnit;
    private double progress = 0;

    public Units(int direction, int amount, double damagePerUnit)
    {
        this.direction = direction;
        this.amount = amount;
        this.damagePerUnit = damagePerUnit;
    }

    public void update(double dt)
    {
        progress += dt * Constants.UNIT_SPEED;
    }
}
