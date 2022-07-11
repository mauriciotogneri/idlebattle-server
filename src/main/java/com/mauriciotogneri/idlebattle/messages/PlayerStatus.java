package com.mauriciotogneri.idlebattle.messages;

public class PlayerStatus
{
    private final int index;
    private final String name;
    private final int points;
    private final int money;
    private final int mineLevel;
    private final int attackLevel;

    public PlayerStatus(int index, String name, int points, int money, int mineLevel, int attackLevel)
    {
        this.index = index;
        this.name = name;
        this.points = points;
        this.money = money;
        this.mineLevel = mineLevel;
        this.attackLevel = attackLevel;
    }
}
