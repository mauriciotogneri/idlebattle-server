package com.mauriciotogneri.idlebattle.messages;

public class PlayerStatus
{
    private final String name;
    private final Integer direction;
    private final Boolean isSelf;
    private final Integer points;
    private final Double money;
    private final Integer mineLevel;
    private final Integer attackLevel;

    public PlayerStatus(String name,
                        Integer direction,
                        Boolean isSelf,
                        Integer points,
                        Double money,
                        Integer mineLevel,
                        Integer attackLevel)
    {
        this.name = name;
        this.direction = direction;
        this.isSelf = isSelf;
        this.points = points;
        this.money = money;
        this.mineLevel = mineLevel;
        this.attackLevel = attackLevel;
    }
}
