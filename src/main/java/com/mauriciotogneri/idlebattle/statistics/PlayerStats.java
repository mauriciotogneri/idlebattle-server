package com.mauriciotogneri.idlebattle.statistics;

public class PlayerStats
{
    public final String name;
    public final Integer direction;
    public final Integer points;
    public final Double money;
    public final Integer mineLevel;
    public final Integer attackLevel;

    public PlayerStats(String name,
                       Integer direction,
                       Integer points,
                       Double money,
                       Integer mineLevel,
                       Integer attackLevel)
    {
        this.name = name;
        this.direction = direction;
        this.points = points;
        this.money = money;
        this.mineLevel = mineLevel;
        this.attackLevel = attackLevel;
    }
}
