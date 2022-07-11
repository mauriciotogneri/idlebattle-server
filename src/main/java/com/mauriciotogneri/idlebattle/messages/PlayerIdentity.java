package com.mauriciotogneri.idlebattle.messages;

public class PlayerIdentity
{
    private final String name;
    private final int direction;

    public PlayerIdentity(String name, int direction)
    {
        this.name = name;
        this.direction = direction;
    }
}
