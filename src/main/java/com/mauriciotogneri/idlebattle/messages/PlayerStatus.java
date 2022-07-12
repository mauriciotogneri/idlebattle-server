package com.mauriciotogneri.idlebattle.messages;

public class PlayerStatus
{
    private final int points;
    private final double money;
    private final int mineLevel;
    private final int attackLevel;

    public PlayerStatus(int points, double money, int mineLevel, int attackLevel)
    {
        this.points = points;
        this.money = money;
        this.mineLevel = mineLevel;
        this.attackLevel = attackLevel;
    }
}
