package com.mauriciotogneri.idlebattle.game;

import org.java_websocket.WebSocket;

public class Player
{
    private final WebSocket webSocket;
    private final int index;
    private final String name;
    private float money = 0;

    public Player(WebSocket webSocket, int index, String name)
    {
        this.webSocket = webSocket;
        this.index = index;
        this.name = name;
    }

    public Player player(int index)
    {
        return new Player(webSocket, index, name);
    }
}
