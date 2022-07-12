package com.mauriciotogneri.idlebattle.messages;

public class PlayerIdentity
{
    private final String name;
    private final int direction;
    private final boolean isSelf;

    public PlayerIdentity(String name, int direction, boolean isSelf)
    {
        this.name = name;
        this.direction = direction;
        this.isSelf = isSelf;
    }
}
