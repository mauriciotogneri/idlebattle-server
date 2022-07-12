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
    public final int moneyRate;
    public final int unitCost;
    public final double unitSpeed;
    public final int initialMoney;
    public final int lostLaneMoney;
    public final int laneRewardMoney;
    public final int mineCostMultiplier;
    public final int attackCostMultiplier;
    public final double blockMultiplier;
    public final double unitBaseDamage;

    private MatchConfiguration(int lanes,
                               int readyTimeout,
                               int matchTimeout,
                               int moneyRate,
                               int unitCost,
                               double unitSpeed,
                               int initialMoney,
                               int lostLaneMoney,
                               int laneRewardMoney,
                               int mineCostMultiplier,
                               int attackCostMultiplier,
                               double blockMultiplier,
                               double unitBaseDamage)
    {
        this.lanes = lanes;
        this.readyTimeout = readyTimeout;
        this.matchTimeout = matchTimeout;
        this.moneyRate = moneyRate;
        this.unitCost = unitCost;
        this.unitSpeed = unitSpeed;
        this.initialMoney = initialMoney;
        this.lostLaneMoney = lostLaneMoney;
        this.laneRewardMoney = laneRewardMoney;
        this.mineCostMultiplier = mineCostMultiplier;
        this.attackCostMultiplier = attackCostMultiplier;
        this.blockMultiplier = blockMultiplier;
        this.unitBaseDamage = unitBaseDamage;
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

            int lanes = Integer.parseInt(properties.getProperty("LANES"));
            int readyTimeout = Integer.parseInt(properties.getProperty("READY_TIMEOUT"));
            int matchTimeout = Integer.parseInt(properties.getProperty("MATCH_TIMEOUT"));
            int moneyRate = Integer.parseInt(properties.getProperty("MONEY_RATE"));
            int unitCost = Integer.parseInt(properties.getProperty("UNIT_COST"));
            double unitSpeed = Double.parseDouble(properties.getProperty("UNIT_SPEED"));
            int initialMoney = Integer.parseInt(properties.getProperty("INITIAL_MONEY"));
            int lostLaneMoney = Integer.parseInt(properties.getProperty("LOST_LANE_MONEY"));
            int laneRewardMoney = Integer.parseInt(properties.getProperty("LANE_REWARD_MONEY"));
            int mineCostMultiplier = Integer.parseInt(properties.getProperty("MINE_COST_MULTIPLIER"));
            int attackCostMultiplier = Integer.parseInt(properties.getProperty("ATTACK_COST_MULTIPLIER"));
            double blockMultiplier = Double.parseDouble(properties.getProperty("BLOCK_MULTIPLIER"));
            double unitBaseDamage = Double.parseDouble(properties.getProperty("UNIT_BASE_DAMAGE"));

            return new MatchConfiguration(
                    lanes,
                    readyTimeout,
                    matchTimeout,
                    moneyRate,
                    unitCost,
                    unitSpeed,
                    initialMoney,
                    lostLaneMoney,
                    laneRewardMoney,
                    mineCostMultiplier,
                    attackCostMultiplier,
                    blockMultiplier,
                    unitBaseDamage);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
