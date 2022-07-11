package com.mauriciotogneri.idlebattle.game;

import java.util.ArrayList;
import java.util.List;

public class Lane
{
    private final int id;
    private boolean enabled = true;
    private double wall = 0.5;
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
