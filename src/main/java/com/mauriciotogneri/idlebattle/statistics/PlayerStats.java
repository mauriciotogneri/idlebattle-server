package com.mauriciotogneri.idlebattle.statistics;

public class PlayerStats
{
    public final String id;
    public final String name;
    public final Integer direction;
    public final Integer points;
    public final Integer money;
    public final Integer mineLevel;
    public final Integer attackLevel;

    public PlayerStats(String id,
                       String name,
                       Integer direction,
                       Integer points,
                       Integer money,
                       Integer mineLevel,
                       Integer attackLevel)
    {
        this.id = id;
        this.name = name;
        this.direction = direction;
        this.points = points;
        this.money = money;
        this.mineLevel = mineLevel;
        this.attackLevel = attackLevel;
    }
}
