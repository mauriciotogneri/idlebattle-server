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

    private MatchConfiguration(int lanes,
                               int readyTimeout,
                               int matchTimeout,
                               int moneyRate,
                               int unitCost,
                               double unitSpeed)
    {
        this.lanes = lanes;
        this.readyTimeout = readyTimeout;
        this.matchTimeout = matchTimeout;
        this.moneyRate = moneyRate;
        this.unitCost = unitCost;
        this.unitSpeed = unitSpeed;
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

            return new MatchConfiguration(
                    lanes,
                    readyTimeout,
                    matchTimeout,
                    moneyRate,
                    unitCost,
                    unitSpeed
            );
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
