package com.mauriciotogneri.idlebattle.game;

import com.mauriciotogneri.idlebattle.app.Constants;
import com.mauriciotogneri.idlebattle.messages.LaneStatus;
import com.mauriciotogneri.idlebattle.messages.MatchConfiguration;

import java.util.ArrayList;
import java.util.List;

public class Lane
{
    private final MatchConfiguration configuration;
    private boolean enabled = true;
    private boolean rewardEnabled = true;
    private double wall = 0.5;
    private double force = 0;
    private List<Units> units = new ArrayList<>();

    public Lane(MatchConfiguration configuration)
    {
        this.configuration = configuration;
    }

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

                if (units.direction() == Constants.DIRECTION_UP)
                {
                    if (units.progress() >= wall)
                    {
                        force += units.totalDamage();
                    }
                    else
                    {
                        unitsAlive.add(units);
                    }
                }
                else if (units.direction() == Constants.DIRECTION_DOWN)
                {
                    if (units.progress() >= (1 - wall))
                    {
                        force -= units.totalDamage();
                    }
                    else
                    {
                        unitsAlive.add(units);
                    }
                }
            }

            if (rewardEnabled)
            {
                if (wall >= 0.75)
                {
                    sendUpdate = true;
                    rewardEnabled = false;
                    player1.addMoney(configuration.laneRewardMoney);
                }
                else if (wall <= 0.25)
                {
                    sendUpdate = true;
                    rewardEnabled = false;
                    player2.addMoney(configuration.laneRewardMoney);
                }
            }

            if (wall >= 1)
            {
                sendUpdate = true;
                player1.addPoint();
                player2.addMoney(configuration.lostLaneMoney);
                wall = 1;
                enabled = false;
                units.clear();
            }
            else if (wall <= 0)
            {
                sendUpdate = true;
                player2.addPoint();
                player1.addMoney(configuration.lostLaneMoney);
                wall = 0;
                enabled = false;
                units.clear();
            }
            else
            {
                if (force != 0)
                {
                    double distance = dt * Math.signum(force) * configuration.unitSpeed;

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

    public LaneStatus status()
    {
        return new LaneStatus(enabled, rewardEnabled, wall);
    }
}
