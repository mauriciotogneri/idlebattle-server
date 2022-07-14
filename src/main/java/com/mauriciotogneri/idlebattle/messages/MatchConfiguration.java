package com.mauriciotogneri.idlebattle.messages;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class MatchConfiguration
{
    public final int lanes;
    public final int readyTimeout; // in seconds
    public final int matchTimeout; // in seconds

    public final int initialMoney;
    public final int moneyRate;
    public final int mineCostMultiplier;
    public final int attackCostMultiplier;

    public final int lostLaneMoney;
    public final int laneRewardMoney;

    public final int unitCost;
    public final double unitSpeed;
    public final double unitBaseDamage;
    public final double blockMultiplier;

    public MatchConfiguration(int lanes,
                              int readyTimeout,
                              int matchTimeout,
                              int initialMoney,
                              int moneyRate,
                              int mineCostMultiplier,
                              int attackCostMultiplier,
                              int lostLaneMoney,
                              int laneRewardMoney,
                              int unitCost,
                              double unitSpeed,
                              double unitBaseDamage,
                              double blockMultiplier)
    {
        this.lanes = lanes;
        this.readyTimeout = readyTimeout;
        this.matchTimeout = matchTimeout;
        this.initialMoney = initialMoney;
        this.moneyRate = moneyRate;
        this.mineCostMultiplier = mineCostMultiplier;
        this.attackCostMultiplier = attackCostMultiplier;
        this.lostLaneMoney = lostLaneMoney;
        this.laneRewardMoney = laneRewardMoney;
        this.unitCost = unitCost;
        this.unitSpeed = unitSpeed;
        this.unitBaseDamage = unitBaseDamage;
        this.blockMultiplier = blockMultiplier;
    }

    public int winnerLimit()
    {
        return (lanes / 2) + 1;
    }

    @NotNull
    public static MatchConfiguration fromFile()
    {
        try
        {
            InputStream inputStream = Files.newInputStream(Paths.get("params.properties"));
            Properties properties = new Properties();
            properties.load(inputStream);
            inputStream.close();

            int lanes = Integer.parseInt(properties.getProperty("lanes"));
            int readyTimeout = Integer.parseInt(properties.getProperty("ready_timeout"));
            int matchTimeout = Integer.parseInt(properties.getProperty("match_timeout"));

            int initialMoney = Integer.parseInt(properties.getProperty("initial_money"));
            int moneyRate = Integer.parseInt(properties.getProperty("money_rate"));
            int mineCostMultiplier = Integer.parseInt(properties.getProperty("mine_cost_multiplier"));
            int attackCostMultiplier = Integer.parseInt(properties.getProperty("attack_cost_multiplier"));

            int lostLaneMoney = Integer.parseInt(properties.getProperty("lost_lane_money"));
            int laneRewardMoney = Integer.parseInt(properties.getProperty("lane_reward_money"));

            int unitCost = Integer.parseInt(properties.getProperty("unit_cost"));
            double unitSpeed = Double.parseDouble(properties.getProperty("unit_speed"));
            double unitBaseDamage = Double.parseDouble(properties.getProperty("unit_base_damage"));
            double blockMultiplier = Double.parseDouble(properties.getProperty("block_multiplier"));

            return new MatchConfiguration(
                    lanes,
                    readyTimeout,
                    matchTimeout,
                    initialMoney,
                    moneyRate,
                    mineCostMultiplier,
                    attackCostMultiplier,
                    lostLaneMoney,
                    laneRewardMoney,
                    unitCost,
                    unitSpeed,
                    unitBaseDamage,
                    blockMultiplier);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
