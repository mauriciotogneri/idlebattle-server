package com.mauriciotogneri.idlebattle.messages;

public class PlayerIdentity
{
    private final String name;
    private final boolean isSelf;

    public PlayerIdentity(String name, boolean isSelf)
    {
        this.name = name;
        this.isSelf = isSelf;
    }
}
