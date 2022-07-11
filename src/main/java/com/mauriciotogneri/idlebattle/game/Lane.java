package com.mauriciotogneri.idlebattle.game;

import com.mauriciotogneri.idlebattle.app.Constants;

import java.util.ArrayList;
import java.util.List;

public class Lane
{
    private final int id;
    private boolean enabled = true;
    private double wall = Constants.INITIAL_WALL_VALUE;
    private double inertia = 0;
    private final List<Units> units = new ArrayList<>();

    public Lane(int id)
    {
        this.id = id;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void launchUnits(Units units)
    {
        this.units.add(units);
    }

    public void update(double dt)
    {
        for (Units units : units)
        {
            units.update(dt);
        }
    }
}
