package com.mauriciotogneri.idlebattle.game;

import java.util.ArrayList;
import java.util.List;

public class Lane
{
    private final int index;
    private double wall = 0.5;
    private double inertia = 0;
    private final List<Units> units = new ArrayList<>();

    public Lane(int index)
    {
        this.index = index;
    }

    public void update(double dt)
    {
        for (Units units : units)
        {
            units.update(dt);
        }
    }
}
