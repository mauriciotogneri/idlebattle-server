package com.mauriciotogneri.idlebattle.game;

import com.mauriciotogneri.idlebattle.app.Constants;

import java.util.ArrayList;
import java.util.List;

public class Lane
{
    private boolean enabled = true;
    private boolean rewardEnabled = true;
    private double wall = 0.5;
    private double force = 0;
    private List<Units> units = new ArrayList<>();

    public boolean isEnabled()
    {
        return enabled;
    }

    public double wall()
    {
        return wall;
    }

    public void launchUnits(Units units)
    {
        this.units.add(units);
    }

    public boolean update(double dt, Player player1, Player player2)
    {
        boolean sendUpdate = false;

        if (enabled)
        {
            List<Units> unitsAlive = new ArrayList<>();

            for (Units units : units)
            {
                units.update(dt);

                if (units.passedWall(wall))
                {
                    force += units.force();
                    sendUpdate = true;
                }
                else
                {
                    unitsAlive.add(units);
                }
            }

            if (rewardEnabled)
            {
                if (wall >= 0.75)
                {
                    sendUpdate = true;
                    rewardEnabled = false;
                    player1.addMoney(Constants.LINE_REWARD);
                }
                else if (wall <= 0.25)
                {
                    sendUpdate = true;
                    rewardEnabled = false;
                    player2.addMoney(Constants.LINE_REWARD);
                }
            }

            if (wall >= 1)
            {
                sendUpdate = true;
                wall = 1;
                player1.addPoint();
                player2.addMoney(Constants.LOST_LANE_MONEY);
                enabled = false;
                units.clear();
            }
            else if (wall <= 0)
            {
                sendUpdate = true;
                wall = 0;
                player2.addPoint();
                player1.addMoney(Constants.LOST_LANE_MONEY);
                enabled = false;
                units.clear();
            }
            else
            {
                if (force != 0)
                {
                    final double distance = dt * force * Constants.UNIT_SPEED;

                    if (Math.abs(force) > Math.abs(distance))
                    {
                        wall += distance;
                        force -= distance;
                    }
                    else
                    {
                        wall += force;
                        force = 0;
                    }
                }

                units = unitsAlive;
            }
        }

        return sendUpdate;
    }
}
