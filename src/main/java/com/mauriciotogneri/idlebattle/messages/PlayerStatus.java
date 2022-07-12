package com.mauriciotogneri.idlebattle.messages;

public class PlayerStatus
{
    private final String name;
    private final Boolean isSelf;
    private final Integer direction;
    private final Integer points;
    private final Integer money;
    private final Integer mineLevel;
    private final Integer attackLevel;

    public PlayerStatus(String name,
                        Boolean isSelf,
                        Integer direction,
                        Integer points,
                        Integer money,
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
